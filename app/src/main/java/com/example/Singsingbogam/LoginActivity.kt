package com.example.Singsingbogam

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.Singsingbogam.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private val mAuth = FirebaseAuth.getInstance()

    private var mId: EditText? = null
    private var mPassword: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mId = findViewById(R.id.login_id)
        mPassword = findViewById(R.id.login_password)

        findViewById<View>(R.id.login_signup).setOnClickListener(this)
        findViewById<View>(R.id.login_success).setOnClickListener(this)

        // 엔터키를 눌렀을 때 로그인 실행
        mPassword?.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                performLogin()
                true
            } else {
                false
            }
        }
    }

    // 자동 로그인
    override fun onStart() {
        super.onStart()
        val user = mAuth.currentUser
        if (user != null) {
            startActivity(Intent(this, RegActivity::class.java))
        }
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.login_signup) {
            startActivity(Intent(this, SignupActivity::class.java))
        } else if (id == R.id.login_success) {
            performLogin()
        }
    }

    // 로그인 로직
    private fun performLogin() {
        val idText = mId?.text.toString()
        val passwordText = mPassword?.text.toString()

        if (idText.isNotEmpty() && passwordText.isNotEmpty()) {
            mAuth.signInWithEmailAndPassword(idText, passwordText)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = mAuth.currentUser
                        if (user != null) {
                            startActivity(Intent(this@LoginActivity, RegActivity::class.java))
                        }
                    } else {
                        ToastActivity.showToast(this, "Login Error")

                    }
                }
        } else {
            ToastActivity.showToast(this, "Please Fill in boteh fields")
        }
    }
}

