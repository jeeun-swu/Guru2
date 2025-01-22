package com.example.communityex3

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class SignupActivity : AppCompatActivity(), View.OnClickListener {
    private var mIdText: EditText? = null
    private var mPasswordText: EditText? = null
    private val mAuth = FirebaseAuth.getInstance()
    private val mStore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        mIdText = findViewById(R.id.sign_id)
        mPasswordText = findViewById(R.id.sign_password)

        findViewById<View>(R.id.sign_success).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        mAuth.createUserWithEmailAndPassword(
            mIdText!!.text.toString(),
            mPasswordText!!.text.toString()
        )
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    if (user != null) {
                        val userMap: MutableMap<String, Any> =
                            HashMap()
                        userMap[FirebaseId.documentId] = user.uid
                        userMap[FirebaseId.Id] = mIdText!!.text.toString()
                        userMap[FirebaseId.Password] = mPasswordText!!.text.toString()
                        // Add a new document with a generated ID
                        mStore.collection(FirebaseId.user).document(user.uid)[userMap] =
                            SetOptions.merge()
                        finish()
                    }
                    /*
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(SignupActivity.this, "Sign-Up Successful!", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else {
                                                    Toast.makeText(SignupActivity.this, "Failed to save user data: " + task.getException().getMessage(),
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });*/
                } else {
                    Toast.makeText(
                        this@SignupActivity, "SignUp Error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}