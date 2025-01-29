package com.example.Singsingbogam


import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import android.view.View

object ToastActivity {

    private var toast: Toast? = null

    fun showToast(context: Context, message: String) {      //토스트메시지에서 받아온 message만을 보여주는 함수
        // 기존 토스트가 있으면 취소
        toast?.cancel()

        // activity_toast 레이아웃을 사용하여 토스트 생성
        val inflater = LayoutInflater.from(context)
        val layout: View = inflater.inflate(R.layout.activity_toast, null)

        val text: TextView = layout.findViewById(R.id.toast_text)       //레이아웃의 텍스트뷰와 연결
        text.text = message     //입력받은 message를 text에 설정

        toast = Toast(context)
        toast?.duration = Toast.LENGTH_SHORT  // 기간 설정
        toast?.view = layout
        toast?.show()
    }
}
//