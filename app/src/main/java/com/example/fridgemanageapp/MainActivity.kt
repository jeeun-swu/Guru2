package com.example.fridgemanageapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    lateinit var edtName : EditText
    lateinit var edtDate : EditText
    lateinit var btnRegister : Button
    lateinit var btnDelete : Button

    lateinit var myHelper : myDBHelper
    lateinit var sqlDB : SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        edtName = findViewById(R.id.edtName)
        edtDate = findViewById(R.id.edtDate)

        btnRegister = findViewById(R.id.btnRegister)
        btnDelete = findViewById(R.id.btnDelete)

        myHelper = myDBHelper(this)

        btnRegister.setOnClickListener {
            sqlDB = myHelper.writableDatabase
            sqlDB.execSQL("INSERT INTO fridgeTBL VALUES ( '"+edtName.text.toString()+"' , "
                    +edtDate.text.toString()+");")
            sqlDB.close()
            Toast.makeText(applicationContext, "등록됨", Toast.LENGTH_SHORT).show()
        }

        btnDelete.setOnClickListener {
            sqlDB = myHelper.writableDatabase
            sqlDB.execSQL("DELETE FROM fridgeTBL WHERE fName = '"+edtName.text.toString()+"';")
            sqlDB.close()

            Toast.makeText(applicationContext, "삭제됨", Toast.LENGTH_SHORT).show()
        }

    }

    inner class  myDBHelper(context: Context) : SQLiteOpenHelper(context, "groupDB", null, 1 ) {
        override fun onCreate(db: SQLiteDatabase?) {
            db!!.execSQL("CREATE TABLE fridgeTBL (fName CHAR(20) PRIMARY KEY, fDate Integer);")
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("DROP TABLE IF EXISTS fridgeTBL")
            onCreate(db)
        }
    }

}