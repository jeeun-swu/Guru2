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
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.Singsingbogam.R
import com.google.firebase.auth.FirebaseAuth

class PostViewActivity : AppCompatActivity(), View.OnClickListener {

    private val mStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var mAdapter: PostAdapter
    private val mDatas: MutableList<Post> = mutableListOf()
    private lateinit var mPostRecyclerView: RecyclerView
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_postview)

        mPostRecyclerView = findViewById(R.id.main_recyclerview)

        findViewById<View>(R.id.main_post_edit).setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        mDatas.clear()

        mStore.collection(FirebaseId.post)
            .orderBy(FirebaseId.timestamp, Query.Direction.DESCENDING)
            .addSnapshotListener { queryDocumentSnapshots: QuerySnapshot?, e: FirebaseFirestoreException? ->
                if (queryDocumentSnapshots != null) {
                    mDatas.clear()
                    for (snap: DocumentSnapshot in queryDocumentSnapshots.documents) {
                        val shot = snap.data
                        if (shot != null) {
                            val documentId = snap.id // Firestore 문서 ID 가져오기
                            val title = shot[FirebaseId.title]?.toString().orEmpty()
                            val contents = shot[FirebaseId.contents]?.toString().orEmpty()
                            val data = Post(documentId, title, contents)
                            mDatas.add(data)
                        }
                    }
                    // 삭제 및 수정 이벤트 전달
                    mAdapter = PostAdapter(mDatas,
                        onDelete = { documentId -> showDeleteDialog(documentId) },
                        onEdit = { post -> navigateToEdit(post) })
                    mPostRecyclerView.adapter = mAdapter

                }
            }
    }
    // 수정 화면으로 이동
    private fun navigateToEdit(post: Post) {
        val intent = Intent(this, PostActivity::class.java)
        intent.putExtra("documentId", post.documentId)
        intent.putExtra("title", post.title)
        intent.putExtra("contents", post.contents)
        startActivity(intent)
    }
    // 삭제 다이얼로그
    private fun showDeleteDialog(documentId: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("게시글 삭제")
        builder.setMessage("해당 게시글을 삭제하시겠습니까?")
        builder.setPositiveButton("예") { _, _ ->
            deletePost(documentId)
        }
        builder.setNegativeButton("아니오", null)
        builder.show()
    }

    // 게시글 삭제
    private fun deletePost(documentId: String) {
        mStore.collection(FirebaseId.post).document(documentId)
            .delete()
            .addOnSuccessListener {
                ToastActivity.showToast(this, "게시글이 삭제되었습니다.")
            }
            .addOnFailureListener {
                ToastActivity.showToast(this, "삭제 중 오류가 발생했습니다.")
            }
    }
    override fun onClick(v: View) {
        startActivity(Intent(this, PostActivity::class.java))
    }


    // 메뉴 항목 클릭 처리
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menu_reg -> {
                // RegActivity로 이동
                startActivity(Intent(this, RegActivity::class.java))
                return true
            }
            R.id.menu_dday -> {
                startActivity(Intent(this, DdayListActivity::class.java))
                return true
            }

            R.id.menu_community -> {
                // PostViewActivity로 이동
                startActivity(Intent(this, PostViewActivity::class.java))
                return true
            }

            R.id.menu_logout -> {
                // 로그아웃 처리
                mAuth.signOut()

                // LoginActivity로 이동
                val intent = Intent(this, LoginActivity ::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish() // 현재 Activity 종료
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }
}

