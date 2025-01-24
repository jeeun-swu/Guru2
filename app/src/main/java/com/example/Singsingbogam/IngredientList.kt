package com.example.Singsingbogam

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class IngredientList : AppCompatActivity() {

    lateinit var dbManager : DBManager
    lateinit var sqllitedb : SQLiteDatabase
    lateinit var layout : LinearLayout

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ingredient_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbManager = DBManager(this, "fridgeDB", null, 1)
        sqllitedb = dbManager.readableDatabase

        layout = findViewById(R.id.ingredient)

        var cursor : Cursor
        cursor = sqllitedb.rawQuery("SELECT * FROM fridgeTBL;", null)

        var num : Int = 0
        while(cursor.moveToNext()) {
            var str_name = cursor.getString(cursor.getColumnIndex("fName")).toString()
            var exp_date = cursor.getInt(cursor.getColumnIndex("fDate"))

            var layout_item : LinearLayout = LinearLayout(this)
            layout_item.orientation = LinearLayout.VERTICAL
            layout_item.id = num

            var tvName : TextView = TextView(this)
            tvName.text = str_name
            tvName.textSize = 30f
            //배경 색 변경 가능
            tvName.setBackgroundColor(Color.LTGRAY)
            layout_item.addView(tvName)

            var tvDate : TextView = TextView(this)
            tvDate.text = exp_date.toString() + "\n"
            tvDate.textSize = 20f
            layout_item.addView(tvDate)

            layout.addView(layout_item)
            num++;
        }

        cursor.close()
        sqllitedb.close()
        dbManager.close()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_reg -> {
                // 등록 및 삭제 화면(RegActivity)으로 이동
                startActivity(Intent(this, RegActivity::class.java))
                return true
            }
            R.id.menu_dday -> {
                // 재료 목록 화면(IngredientList)으로 이동
                startActivity(Intent(this, IngredientList::class.java))
                return true
            }
            R.id.menu_community -> {
                // 커뮤니티 화면(PostViewActivity)으로 이동
                startActivity(Intent(this, PostViewActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}