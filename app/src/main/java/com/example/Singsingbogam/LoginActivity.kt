package com.example.Singsingbogam

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    // 🔥 Firebase 인증 객체 초기화
    private val mAuth = FirebaseAuth.getInstance()

    // 로그인 입력 필드 (아이디 & 비밀번호)
    private var mId: EditText? = null
    private var mPassword: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 🔥 레이아웃에서 ID와 비밀번호 입력 필드 연결
        mId = findViewById(R.id.login_id)
        mPassword = findViewById(R.id.login_password)

        // 🔥 버튼 클릭 이벤트 설정
        findViewById<View>(R.id.login_signup).setOnClickListener(this) // 회원가입 이동 버튼
        findViewById<View>(R.id.login_success).setOnClickListener(this) // 로그인 버튼

        // 🔥 엔터키 입력 시 로그인 실행
        mPassword?.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                performLogin() // 로그인 실행
                true
            } else {
                false
            }
        }
    }

    // 🔥 자동 로그인 (앱 실행 시 현재 로그인된 사용자 확인)
    override fun onStart() {
        super.onStart()
        val user = mAuth.currentUser
        if (user != null) {
            // ✅ 로그인 상태라면 RegActivity로 이동
            startActivity(Intent(this, RegActivity::class.java))
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.login_signup -> {
                // ✅ 회원가입 버튼 클릭 시 SignupActivity로 이동
                startActivity(Intent(this, SignupActivity::class.java))
            }
            R.id.login_success -> {
                // ✅ 로그인 버튼 클릭 시 로그인 실행
                performLogin()
            }
        }
    }

    // 🔥 로그인 기능 실행
    private fun performLogin() {
        val idText = mId?.text.toString()  // 입력된 이메일
        val passwordText = mPassword?.text.toString()  // 입력된 비밀번호

        // ✅ 이메일과 비밀번호가 입력되었는지 확인
        if (idText.isNotEmpty() && passwordText.isNotEmpty()) {
            // ✅ Firebase Authentication을 사용하여 로그인 시도
            mAuth.signInWithEmailAndPassword(idText, passwordText)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = mAuth.currentUser
                        if (user != null) {
                            // ✅ 로그인 성공 시 RegActivity로 이동
                            startActivity(Intent(this@LoginActivity, RegActivity::class.java))
                        }
                    } else {
                        // ❌ 로그인 실패 시 오류 메시지 출력
                        ToastActivity.showToast(this, "Login Error")                    }
                }
        } else {
            // ❌ 입력값이 없는 경우 메시지 출력
            ToastActivity.showToast(this, "Please Fill in boteh fields")

        }
    }
}
