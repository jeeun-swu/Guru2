package com.example.Singsingbogam

import android.os.Bundle
import android.view.View
import android.widget.EditText
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

        findViewById<View>(R.id.post_save_btn).setOnClickListener(this)
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