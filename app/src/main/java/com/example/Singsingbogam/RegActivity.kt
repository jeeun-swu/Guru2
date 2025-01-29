package com.example.Singsingbogam

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RegActivity : AppCompatActivity() {

    lateinit var edtName: EditText
    lateinit var edtDate: EditText
    lateinit var btnRegister: Button
    lateinit var btnDelete: Button

    lateinit var dbManager: DBManager
    lateinit var sqllitedb: SQLiteDatabase
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reg)

        edtName = findViewById(R.id.edtName)
        edtDate = findViewById(R.id.edtDate)

        btnRegister = findViewById(R.id.btnRegister)
        btnDelete = findViewById(R.id.btnDelete)

        //fridgeDB라는 이름의 Database 생성
        dbManager = DBManager(this, "fridgeDB", null, 1)

        //등록 버튼 클릭 시 테이블에 이름과 유통기한 저장
        btnRegister.setOnClickListener {
            sqllitedb = dbManager.writableDatabase
            sqllitedb.execSQL(
                "INSERT INTO fridgeTBL VALUES ( '" + edtName.text.toString() + "' , "
                        + edtDate.text.toString() + ");")
            sqllitedb.close()

            //토스트 메시지로 등록사실을 알림
            ToastActivity.showToast(this, "등록됨")
        }

        //삭제 버튼 클릭 시 테이블에서 이름을 찾아 삭제
        btnDelete.setOnClickListener {
            sqllitedb = dbManager.writableDatabase
            sqllitedb.execSQL("DELETE FROM fridgeTBL WHERE fName = '" + edtName.text.toString() + "';")
            sqllitedb.close()

            ToastActivity.showToast(this, "삭제됨")
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }
    // 메뉴 항목 클릭 처리
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_reg -> {      //등록 및 삭제 화면으로 이동
                startActivity(Intent(this, RegActivity::class.java))
                return true
            }
            R.id.menu_dday -> {     //디데이 목록 화면으로 이동
                startActivity(Intent(this, DdayListActivity::class.java))
                return true
            }
            R.id.menu_community -> {    //커뮤니티 화면으로 이동
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
//


