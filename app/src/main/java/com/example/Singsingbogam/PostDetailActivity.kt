package com.example.Singsingbogam

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import models.Comment
import adapters.CommentAdapter
import android.util.Log
import com.google.firebase.firestore.Query

class PostDetailActivity : AppCompatActivity() {

    // 🔥 Firestore & Firebase Auth 인스턴스 생성
    private val mStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    // 🔥 댓글 리스트 & 어댑터
    private lateinit var commentAdapter: CommentAdapter
    private val commentList = mutableListOf<Comment>()

    // 🔥 게시글 관련 변수
    private var postId: String? = null  // 게시글 ID
    private var postAuthor: String? = null  // 게시글 작성자

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        // 🔥 UI 요소 가져오기
        val titleView = findViewById<TextView>(R.id.detail_post_title)
        val authorView = findViewById<TextView>(R.id.detail_post_author)
        val contentsView = findViewById<TextView>(R.id.detail_post_contents)
        val timestampView = findViewById<TextView>(R.id.detail_post_timestamp)
        val commentRecyclerView = findViewById<RecyclerView>(R.id.comment_recyclerview)
        val commentAddButton = findViewById<Button>(R.id.comment_add_button)

        // 🔥 RecyclerView 설정 (댓글 목록)
        commentRecyclerView.layoutManager = LinearLayoutManager(this)

        // 🔥 인텐트로 전달받은 데이터 가져오기 (게시글 정보)
        postId = intent.getStringExtra("documentId")
        val title = intent.getStringExtra("title")
        val contents = intent.getStringExtra("contents")
        val timestamp = intent.getStringExtra("timestamp") ?: "날짜 없음"
        postAuthor = intent.getStringExtra("author")

        // 🔥 UI에 게시글 정보 표시
        titleView.text = title
        authorView.text = "작성자: $postAuthor"
        contentsView.text = contents
        timestampView.text = "작성날짜: $timestamp"

        Log.d("Firestore", "PostDetailActivity에서 받은 postId: $postId") // 🔥 디버깅 로그

        if (postId == null) {
            Log.e("Firestore", "🔥 postId가 null이어서 댓글을 불러올 수 없음.")
        } else {
            loadComments() // ✅ postId가 null이 아닐 때만 댓글 불러오기
        }

        // 🔥 댓글 리스트 어댑터 설정
        commentAdapter = CommentAdapter(
            commentList,
            mAuth.currentUser?.email.orEmpty(), // 🔥 현재 로그인한 사용자 이메일
            postAuthor.orEmpty(), // 🔥 게시글 작성자 이메일
            onDeleteClick = { comment -> deleteComment(comment) } // 🔥 댓글 삭제 기능 추가
        )
        commentRecyclerView.adapter = commentAdapter

        // 🔥 댓글 추가 버튼 클릭 시 다이얼로그 표시
        commentAddButton.setOnClickListener {
            showCommentDialog()
        }
    }

    // ✅ **댓글 작성 다이얼로그 표시**
    private fun showCommentDialog() {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_comment, null)
        builder.setView(view)

        val commentInput = view.findViewById<EditText>(R.id.comment_input)

        builder.setPositiveButton("등록") { _, _ ->
            val commentText = commentInput.text.toString().trim()
            if (commentText.isNotEmpty()) {
                addComment(commentText)
            }
        }
        builder.setNegativeButton("취소") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    // ✅ **Firestore에 댓글 추가**
    private fun addComment(content: String) {
        val user = mAuth.currentUser
        if (user != null && postId != null) {
            val commentId = mStore.collection("comments").document().id

            // 🔥 댓글 객체 생성
            val comment = Comment(
                commentId,
                postId!!,
                user.email.orEmpty(),
                content
            )

            // 🔥 Firestore에 데이터 저장
            mStore.collection("comments").document(commentId).set(comment)
                .addOnSuccessListener {
                    ToastActivity.showToast(this, "댓글이 작성되었습니다.")
                    loadComments() // 🔥 댓글 다시 불러오기
                }
                .addOnFailureListener { e ->
                    ToastActivity.showToast(this, "댓글 작성 오류.")
                }
        } else {
            ToastActivity.showToast(this, "로그인이 필요합니다.")
        }
    }

    // ✅ **Firestore에서 댓글 불러오기**
    private fun loadComments() {
        mStore.collection("comments")
            .whereEqualTo("postId", postId) // 🔥 해당 게시글의 댓글만 가져오기
            .orderBy("timestamp", Query.Direction.ASCENDING) // 🔥 오래된 댓글부터 정렬
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("Firestore", "🔥 댓글 불러오기 실패: ${e.message}")
                    return@addSnapshotListener
                }
                commentList.clear()
                for (doc in snapshot!!.documents) {
                    val comment = doc.toObject(Comment::class.java)
                    if (comment != null) {
                        commentList.add(comment)
                    }
                }
                commentAdapter.notifyDataSetChanged() // 🔥 UI 업데이트
            }
    }

    // ✅ **Firestore에서 댓글 삭제**
    private fun deleteComment(comment: Comment) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("댓글 삭제")
            .setMessage("이 댓글을 삭제하시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                mStore.collection("comments").document(comment.commentId)
                    .delete()
                    .addOnSuccessListener {
                        ToastActivity.showToast(this, "댓글이 삭제되었습니다.")
                        loadComments() // 🔥 삭제 후 댓글 새로 불러오기
                    }
                    .addOnFailureListener { e ->
                        ToastActivity.showToast(this, "삭제 중 오류 발생")
                    }
            }
            .setNegativeButton("아니오") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
