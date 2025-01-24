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
                            val documentId = shot[FirebaseId.documentId]?.toString().orEmpty()
                            val title = shot[FirebaseId.title]?.toString().orEmpty()
                            val contents = shot[FirebaseId.contents]?.toString().orEmpty()
                            val data = Post(documentId, title, contents)
                            mDatas.add(data)
                        }
                    }
                    mAdapter = PostAdapter(mDatas)
                    mPostRecyclerView.adapter = mAdapter
                }
            }
    }

    override fun onClick(v: View) {
        startActivity(Intent(this, PostActivity::class.java))
    }

    // 메뉴 생성
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
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
