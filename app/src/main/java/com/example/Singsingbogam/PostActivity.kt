package com.example.Singsingbogam

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.Singsingbogam.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class PostActivity : AppCompatActivity(), View.OnClickListener {
    private var mTitle: EditText? = null
    private var mContents: EditText? = null
    private val mStore = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        mTitle = findViewById(R.id.post_title_edit)
        mContents = findViewById(R.id.post_contents_edit)

        // 게시글 수정 - Intent로 전달된 데이터 받아오기
        val documentId = intent.getStringExtra("documentId")
        val title = intent.getStringExtra("title")
        val contents = intent.getStringExtra("contents")

        // 기존 데이터 EditText에 세팅
        mTitle?.setText(title ?: "")
        mContents?.setText(contents ?: "")
        findViewById<View>(R.id.post_save_btn).setOnClickListener {
            if (mAuth.currentUser != null && documentId != null) {
                val data: MutableMap<String, Any> = HashMap()
                data[FirebaseId.title] = mTitle!!.text.toString()
                data[FirebaseId.contents] = mContents!!.text.toString()
                data[FirebaseId.timestamp] = FieldValue.serverTimestamp()

                // Firestore 업데이트
                mStore.collection(FirebaseId.post).document(documentId)
                    .update(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "게시글이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "수정 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    override fun onClick(v: View) {
        if (mAuth.currentUser != null) {
            val postId = mStore.collection(FirebaseId.post).document().id
            val data: MutableMap<String, Any> = HashMap()
            data[FirebaseId.documentId] = mAuth.currentUser!!.uid
            data[FirebaseId.title] = mTitle!!.text.toString()
            data[FirebaseId.contents] = mContents!!.text.toString()
            data[FirebaseId.timestamp] = FieldValue.serverTimestamp()
            mStore.collection(FirebaseId.post).document(postId)[data] = SetOptions.merge()
            finish()
        }
    }
}