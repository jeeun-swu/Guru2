package com.example.Singsingbogam

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.Singsingbogam.R
// SQL 관련 추가문
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
// 파이어베이스 관련 추가문
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class SignupActivity : AppCompatActivity(), View.OnClickListener {
    private var mIdText: EditText? = null
    private var mPasswordText: EditText? = null

    // SQL 버전
    private lateinit var dbHelper: SQLiteHelper
    private lateinit var database: SQLiteDatabase

    // 파이어베이스 버전
    //private val mAuth = FirebaseAuth.getInstance()
    //private val mStore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // SQL 사용시 추가
        dbHelper = SQLiteHelper(this)
        database = dbHelper.writableDatabase
        //

        mIdText = findViewById(R.id.sign_id)
        mPasswordText = findViewById(R.id.sign_password)

        findViewById<View>(R.id.sign_success).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sign_success -> signup()
        }
        /* 파이어베이스 버전
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

         */
    }
    private fun signup() {
        val id = mIdText?.text.toString()
        val password = mPasswordText?.text.toString()

        if (id.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val values = ContentValues().apply {
            put("user_id", id)
            put("password", password)
        }

        val result = database.insert("users", null, values)
        if (result != -1L) {
            Toast.makeText(this, "회원가입 성공!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "회원가입 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

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
}