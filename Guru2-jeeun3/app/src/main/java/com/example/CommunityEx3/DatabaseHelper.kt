package com.example.singsingbogam

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "IngredientDB.db"
        private const val DATABASE_VERSION = 1

        // 테이블 정보
        private const val TABLE_INGREDIENTS = "ingredients"
        private const val COL_ID = "_id"
        private const val COL_NAME = "name"
        private const val COL_PURCHASE_DATE = "purchaseDate"
        private const val COL_EXPIRATION_DATE = "expirationDate"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE $TABLE_INGREDIENTS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NAME TEXT NOT NULL,
                $COL_PURCHASE_DATE TEXT,
                $COL_EXPIRATION_DATE TEXT
            )
        """.trimIndent()

        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // 버전이 올라가면 테이블 재생성 등 처리
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_INGREDIENTS")
        onCreate(db)
    }

    /** 식재료 등록 */
    fun insertIngredient(name: String, purchaseDate: String, expirationDate: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NAME, name)
            put(COL_PURCHASE_DATE, purchaseDate)
            put(COL_EXPIRATION_DATE, expirationDate)
        }
        return db.insert(TABLE_INGREDIENTS, null, values)
    }

    /** 식재료 목록 조회 */
    fun getAllIngredients(): List<Ingredient> {
        val ingredientList = mutableListOf<Ingredient>()
        val selectQuery = "SELECT * FROM $TABLE_INGREDIENTS"

        val db = readableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME))
                val purchaseDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_PURCHASE_DATE))
                val expirationDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPIRATION_DATE))

                ingredientList.add(
                    Ingredient(
                        id,
                        name,
                        purchaseDate,
                        expirationDate
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return ingredientList
    }

    /** 식재료 삭제 */
    fun deleteIngredientById(id: Int) {
        val db = writableDatabase
        db.delete(TABLE_INGREDIENTS, "$COL_ID=?", arrayOf(id.toString()))
    }
}
