package com.example.Singsingbogam

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBManager(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    //받아온 name의 DB 생성 후 그 안에 fridgeTBL이라는 이름의 테이블 생성
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("CREATE TABLE fridgeTBL (fName text PRIMARY KEY, fDate INTEGER)")
    }

    //이미 있으면 삭제 후 새로 생성
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS fridgeTBL")
        onCreate(db)
    }
}

