package com.example.communityex3

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

class PostViewActivity : AppCompatActivity(), View.OnClickListener {

    private val mStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var mAdapter: PostAdapter
    private val mDatas: MutableList<Post> = mutableListOf()
    private lateinit var mPostRecyclerView: RecyclerView

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
}
