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
import com.example.Singsingbogam.R
import com.google.firebase.auth.FirebaseAuth

class RegActivity : AppCompatActivity() {

    lateinit var edtName: EditText
    lateinit var edtDate: EditText
    lateinit var btnRegister: Button
    lateinit var btnDelete: Button

    lateinit var myHelper: myDBHelper
    lateinit var sqlDB: SQLiteDatabase
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reg)

        edtName = findViewById(R.id.edtName)
        edtDate = findViewById(R.id.edtDate)

        btnRegister = findViewById(R.id.btnRegister)
        btnDelete = findViewById(R.id.btnDelete)

        myHelper = myDBHelper(this)

        btnRegister.setOnClickListener {
            sqlDB = myHelper.writableDatabase
            sqlDB.execSQL(
                "INSERT INTO fridgeTBL VALUES ( '" + edtName.text.toString() + "' , "
                        + edtDate.text.toString() + ");"
            )
            sqlDB.close()
            Toast.makeText(applicationContext, "등록됨", Toast.LENGTH_SHORT).show()
        }

        btnDelete.setOnClickListener {
            sqlDB = myHelper.writableDatabase
            sqlDB.execSQL("DELETE FROM fridgeTBL WHERE fName = '" + edtName.text.toString() + "';")
            sqlDB.close()

            Toast.makeText(applicationContext, "삭제됨", Toast.LENGTH_SHORT).show()
        }

    }

    class myDBHelper(context: Context) : SQLiteOpenHelper(context, "fridgeDB", null, 1) {
        override fun onCreate(db: SQLiteDatabase?) {
            db!!.execSQL("CREATE TABLE fridgeTBL (fName CHAR(20) PRIMARY KEY, fDate Integer);")
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("DROP TABLE IF EXISTS fridgeTBL")
            onCreate(db)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }
    // 메뉴 항목 클릭 처리
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_reg -> {
                startActivity(Intent(this, RegActivity::class.java))
                return true
            }
            R.id.menu_dday -> {
                // ★ 수정 포인트 ★
                startActivity(Intent(this, DdayListActivity::class.java))
                return true
            }
            R.id.menu_community -> {
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



