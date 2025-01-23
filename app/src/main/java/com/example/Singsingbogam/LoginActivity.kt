package com.example.Singsingbogam

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.database.sqlite.SQLiteDatabase   // SQL 사용시 활성화
import android.database.sqlite.SQLiteOpenHelper // SQL 사용시 활성화
import com.google.firebase.auth.FirebaseAuth  // 파이어 베이스 사용시 활성화
import com.example.Singsingbogam.R

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    // 파이어베이스 사용시 활성화//private val mAuth = FirebaseAuth.getInstance()
    // SQL 사용시 활성화
    private lateinit var dbHelper: SQLiteHelper
    private lateinit var database: SQLiteDatabase

    private var mId: EditText? = null
    private var mPassword: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mId = findViewById(R.id.login_id)
        mPassword = findViewById(R.id.login_password)

        findViewById<View>(R.id.login_signup).setOnClickListener(this)
        findViewById<View>(R.id.login_success).setOnClickListener(this)

        // SQL 사용시 활성화
        dbHelper = SQLiteHelper(this)
        database = dbHelper.writableDatabase
    }

    /*
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!= null){
            //Toast.makeText(this, "자동 로그인 : " + user.getUid(), Toast.LENGTH_SHORT).show();;
            startActivity(new Intent(this, MainActivity.class));
        }
    }
*/
    override fun onClick(v: View) {
        when (v.id) {
            R.id.login_signup -> startActivity(Intent(this, SignupActivity::class.java))
            R.id.login_success -> login()
        }
    }

    private fun login() {
        val id = mId?.text.toString()
        val password = mPassword?.text.toString()

        val cursor = database.rawQuery(
            "SELECT * FROM users WHERE user_id = ? AND password = ?",
            arrayOf(id, password)
        )

        if (cursor.moveToFirst()) {
            Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, RegActivity::class.java))
        } else {
            Toast.makeText(this, "아이디 또는 비밀번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show()
        }
        cursor.close()
    }

    // SQL 사용시 사용하는 코드
    class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, "users.db", null, 1) {

        override fun onCreate(db: SQLiteDatabase) {
            val createTable = """
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL
            )
        """
            db.execSQL(createTable)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS users")
            onCreate(db)
        }
    }

// 파이어 베이스 사용시 활성화
/*    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.login_signup) {
            startActivity(Intent(this, SignupActivity::class.java))
        } else if (id == R.id.login_success) {
            mAuth.signInWithEmailAndPassword(mId!!.text.toString(), mPassword!!.text.toString())
                .addOnCompleteListener(
                    this
                ) { task ->
                    if (task.isSuccessful) {
                        val user = mAuth.currentUser
                        if (user != null) {
                            //Toast.makeText(LoginActivity.this, "로그인 성공" + user.getUid(),Toast.LENGTH_SHORT).show();
                            startActivity(
                                Intent(
                                    this@LoginActivity,
                                    RegActivity::class.java
                                )
                            )
                        }
                    } else {
                        Toast.makeText(
                            this@LoginActivity, "Login Error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

 */
}