package com.example.singsingbogam

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class IngredientListActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var ingredientRecyclerView: RecyclerView
    private lateinit var ingredientAdapter: IngredientAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredient_list)

        dbHelper = DatabaseHelper(this)
        ingredientRecyclerView = findViewById(R.id.ingredientRecyclerView)

        // DB에서 데이터 가져오기
        val ingredientList = dbHelper.getAllIngredients()

        // D-Day 계산
        val ingredientWithDdays = calculateDdays(ingredientList)

        // D-Day 오름차순 정렬 (유통기한 빠른 재료가 위로)
        ingredientWithDdays.sortBy { it.dday }

        // 알림 확인: D-Day가 1인 재료가 있는지 확인
        checkOneDayLeft(ingredientWithDdays)

        // RecyclerView 설정
        ingredientAdapter = IngredientAdapter(ingredientWithDdays)
        ingredientRecyclerView.layoutManager = LinearLayoutManager(this)
        ingredientRecyclerView.adapter = ingredientAdapter
    }

    /** 식재료 리스트에 대해 D-Day를 계산해서 반환 */
    private fun calculateDdays(ingredientList: List<Ingredient>): ArrayList<IngredientWithDday> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Calendar.getInstance()

        val resultList = arrayListOf<IngredientWithDday>()

        for (ingredient in ingredientList) {
            val expirationCal = Calendar.getInstance()
            expirationCal.time = sdf.parse(ingredient.expirationDate) ?: Date()

            // D-Day 계산: (유통기한 날짜 - 오늘 날짜)
            val diffInMillis = expirationCal.timeInMillis - today.timeInMillis
            val dday = (diffInMillis / (24 * 60 * 60 * 1000)).toInt()

            resultList.add(
                IngredientWithDday(
                    ingredient.id,
                    ingredient.name,
                    ingredient.purchaseDate,
                    ingredient.expirationDate,
                    dday
                )
            )
        }
        return resultList
    }

    /** D-Day가 1인 재료가 있는지 확인 후 알림 */
    private fun checkOneDayLeft(ingredients: List<IngredientWithDday>) {
        // D-Day가 1인 재료 필터링
        val oneDayLeftIngredients = ingredients.filter { it.dday == 1 }

        if (oneDayLeftIngredients.isNotEmpty()) {
            // 식재료 이름을 콤마로 구분하여 표시
            val ingredientNames = oneDayLeftIngredients.joinToString(", ") { it.name }

            // Toast 메시지로 알림 띄우기
            Toast.makeText(
                this,
                "유통기한이 1일 남은 식재료: $ingredientNames",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

