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
import models.Post

class PostActivity : AppCompatActivity(), View.OnClickListener {
    // 🌈 PostActivity (게시글 작성 & 수정 화면)

    // 🔥 UI 요소 (제목 입력, 내용 입력 필드)
    private var mTitle: EditText? = null
    private var mContents: EditText? = null

    // 🔥 Firebase Firestore 및 FirebaseAuth 인스턴스 생성
    private val mStore = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()

    // 🔥 기존 글의 document ID (수정 시 필요)
    private var documentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        // 🔥 XML 레이아웃과 변수 연결
        mTitle = findViewById(R.id.post_title_edit)
        mContents = findViewById(R.id.post_contents_edit)

        // 🔥 "저장" 버튼 클릭 리스너 설정
        findViewById<View>(R.id.post_save_btn).setOnClickListener(this)

        // ✅ **수정 모드인지 확인**
        documentId = intent.getStringExtra("documentId") // 인텐트에서 글 ID 가져오기
        if (documentId != null) {
            // 🔥 기존 글 수정 모드 -> 제목과 내용 불러오기
            mTitle?.setText(intent.getStringExtra("title"))  // 기존 제목 불러오기
            mContents?.setText(intent.getStringExtra("contents"))  // 기존 내용 불러오기
        }
    }

    override fun onClick(v: View) {
        val user = mAuth.currentUser  // 현재 로그인한 사용자 가져오기
        if (user != null) {
            // ✅ **게시글 데이터 준비**
            val data: MutableMap<String, Any> = HashMap()
            data[FirebaseId.title] = mTitle!!.text.toString()  // 제목 저장
            data[FirebaseId.contents] = mContents!!.text.toString()  // 내용 저장
            data[FirebaseId.timestamp] = FieldValue.serverTimestamp()  // Firestore 타임스탬프 추가

            if (documentId == null) {
                // 🔥 **새로운 게시글 작성**
                documentId = mStore.collection(FirebaseId.post).document().id // 새로운 문서 ID 생성
                data[FirebaseId.documentId] = documentId!!  // 문서 ID 저장
                data[FirebaseId.fullAuthor] = user.email.orEmpty() // ✅ 작성자 이메일 저장

                // 🔥 Firestore에 새 게시글 저장
                mStore.collection(FirebaseId.post).document(documentId!!).set(data, SetOptions.merge())
                    .addOnSuccessListener {
                        ToastActivity.showToast(this, "게시글이 저장되었습니다.")
                        finish() // 화면 닫기
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "저장 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // 🔥 **기존 게시글 수정**
                mStore.collection(FirebaseId.post).document(documentId!!)
                    .update(data) // 기존 데이터 업데이트
                    .addOnSuccessListener {
                        ToastActivity.showToast(this, "게시글이 수정되었습니다.")
                        finish()
                    }
                    .addOnFailureListener { e ->
                        ToastActivity.showToast(this, "수정 중 오류가 발생했습니다.")
                    }
            }
        } else {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
