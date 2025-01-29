package com.example.Singsingbogam

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import models.Post
import adapters.PostAdapter
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.Singsingbogam.R
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale

class PostViewActivity : AppCompatActivity(), View.OnClickListener {

    // 🔥 Firestore 인스턴스 생성
    private val mStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // 🔥 RecyclerView 어댑터 및 데이터 리스트 선언
    private lateinit var mAdapter: PostAdapter
    private val mDatas: MutableList<Post> = mutableListOf()
    private lateinit var mPostRecyclerView: RecyclerView

    // 🔥 FirebaseAuth 인스턴스 (사용자 정보 확인용)
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_postview)

        // 🔥 RecyclerView 초기화
        mPostRecyclerView = findViewById(R.id.main_recyclerview)

        // 🔥 "글쓰기 버튼" 클릭 시 이벤트 처리
        findViewById<View>(R.id.main_post_edit).setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        mDatas.clear() // 데이터 초기화

        // 🔥 Firestore에서 게시글 목록 가져오기
        mStore.collection(FirebaseId.post)
            .orderBy(FirebaseId.timestamp, Query.Direction.DESCENDING) // 최신순 정렬
            .addSnapshotListener { queryDocumentSnapshots, e ->
                if (queryDocumentSnapshots != null) {
                    mDatas.clear() // 기존 데이터 클리어
                    for (snap in queryDocumentSnapshots.documents) {
                        val shot = snap.data
                        if (shot != null) {
                            val documentId = shot[FirebaseId.documentId]?.toString().orEmpty()
                            val title = shot[FirebaseId.title]?.toString().orEmpty()
                            val contents = shot[FirebaseId.contents]?.toString().orEmpty()
                            val fullAuthor = shot[FirebaseId.fullAuthor]?.toString().orEmpty()
                            val author = fullAuthor.substringBefore("@") // 이메일 @ 앞부분만 표시

                            // ✅ Firestore Timestamp -> 날짜 문자열 변환
                            val timestamp = shot[FirebaseId.timestamp] as? com.google.firebase.Timestamp
                            val dateString = timestamp?.toDate()?.let {
                                SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(it)
                            } ?: "날짜 없음"

                            // ✅ Post 객체 생성 후 리스트에 추가
                            val data = Post(documentId, title, contents, author, fullAuthor, dateString)
                            mDatas.add(data)
                        }
                    }

                    // 🔥 현재 로그인한 사용자의 이메일 가져오기
                    val currentUserEmail = mAuth.currentUser?.email.orEmpty()

                    // 🔥 RecyclerView 어댑터 설정
                    mAdapter = PostAdapter(
                        mDatas, // 게시글 데이터 리스트
                        currentUserEmail, // 현재 로그인한 사용자 이메일 전달
                        onEditClick = { post -> editPost(post) },
                        onDeleteClick = { post -> deletePost(post) }
                    )
                    mPostRecyclerView.adapter = mAdapter
                }
            }
    }

    // ✅ **게시글 수정 기능**
    private fun editPost(post: Post) {
        val intent = Intent(this, PostActivity::class.java).apply {
            putExtra("documentId", post.documentId) // 기존 글 ID 전달
            putExtra("title", post.title) // 기존 제목 전달
            putExtra("contents", post.contents) // 기존 내용 전달
        }
        startActivity(intent) // 게시글 작성/수정 화면으로 이동
    }

    // ✅ **게시글 삭제 기능**
    private fun deletePost(post: Post) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("게시글 삭제")
            .setMessage("정말 삭제하시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                // 🔥 Firestore에서 해당 게시글 삭제
                mStore.collection(FirebaseId.post).document(post.documentId!!)
                    .delete()
                    .addOnSuccessListener {
                        ToastActivity.showToast(this, "게시글이 삭제되었습니다.")
                    }
                    .addOnFailureListener { e ->
                        ToastActivity.showToast(this, "삭제 중 오류가 발생했습니다.")
                    }
            }
            .setNegativeButton("아니오") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // ✅ **새로운 글 작성 화면으로 이동**
    override fun onClick(v: View) {
        startActivity(Intent(this, PostActivity::class.java))
    }

    // ✅ **옵션 메뉴 생성 (상단 메뉴)**
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    // ✅ **옵션 메뉴 클릭 이벤트 처리**
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_reg -> {
                // 🔥 식재료 등록/삭제 화면으로 이동
                startActivity(Intent(this, RegActivity::class.java))
                return true
            }
            R.id.menu_dday -> {
                // 🔥 디데이 화면으로 이동
                startActivity(Intent(this, DdayListActivity::class.java))
                return true
            }
            R.id.menu_community -> {
                // 🔥 커뮤니티 게시판으로 이동
                startActivity(Intent(this, PostViewActivity::class.java))
                return true
            }
            R.id.menu_logout -> {
                // 🔥 로그아웃 처리
                mAuth.signOut()

                // 🔥 로그인 화면으로 이동 (기존 화면 초기화)
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
