package com.example.Singsingbogam

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class SignupActivity : AppCompatActivity(), View.OnClickListener {

    // 🔥 Firebase 인증 및 Firestore 초기화
    private val mAuth = FirebaseAuth.getInstance()
    private val mStore = FirebaseFirestore.getInstance()

    // 회원가입 입력 필드 (아이디 & 비밀번호)
    private var mIdText: EditText? = null
    private var mPasswordText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // 🔥 레이아웃에서 ID와 비밀번호 입력 필드 연결
        mIdText = findViewById(R.id.sign_id)
        mPasswordText = findViewById(R.id.sign_password)

        // 🔥 회원가입 버튼 클릭 이벤트 설정
        findViewById<View>(R.id.sign_success).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val email = mIdText!!.text.toString()
        val password = mPasswordText!!.text.toString()

        // ✅ Firebase Authentication을 이용한 회원가입 시도
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    if (user != null) {
                        // 🔥 Firestore에 사용자 정보 저장
                        val userMap: MutableMap<String, Any> = HashMap()
                        userMap[FirebaseId.documentId] = user.uid // Firebase 사용자 UID 저장
                        userMap[FirebaseId.Id] = email // 사용자 이메일 저장
                        userMap[FirebaseId.Password] = password // 비밀번호 저장 (⚠️ 보안 이슈)

                        // ✅ Firestore에 사용자 데이터 저장 (SetOptions.merge() 사용)
                        mStore.collection(FirebaseId.user).document(user.uid)
                            .set(userMap, SetOptions.merge())
                            .addOnSuccessListener {
                                Toast.makeText(this, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                                finish() // 회원가입 완료 후 화면 종료
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "데이터 저장 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    // ❌ 회원가입 실패 메시지
                    ToastActivity.showToast(this, "SignUp Error")
                }
            }
    }
}
