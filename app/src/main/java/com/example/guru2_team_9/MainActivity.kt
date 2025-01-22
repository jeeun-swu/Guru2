package com.example.guru2_team_9

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.guru2_team_9.databinding.ActivityMainBinding
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: FoodAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView + Adapter 연결
        adapter = FoodAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // ★ 테스트용 더미 데이터 ★
        val dummyList = listOf(
            FoodItem("우유", LocalDate.of(2025,1,15), LocalDate.of(2025,1,20)),
            FoodItem("계란", LocalDate.of(2025,1,16), LocalDate.of(2025,1,17)),
            FoodItem("두부", LocalDate.of(2025,1,10), LocalDate.of(2025,1,15)),
            FoodItem("햄", LocalDate.of(2025,1,10), LocalDate.of(2025,1,25))
        )

        // 1) D-Day 기준 오름차순(임박 순) 정렬
        val sortedList = dummyList.sortedBy { it.daysLeft() }

        // 2) RecyclerView 갱신
        adapter.updateList(sortedList)

        // 3) 유통기한 임박 (1일 이하) 항목이 있는지 체크 후 알림
        checkExpiringItems(sortedList)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkExpiringItems(foodItems: List<FoodItem>) {
        val isExpiringSoon = foodItems.any { it.daysLeft() <= 1 }
        if (isExpiringSoon) {
            NotificationHelper.showNotification(
                context = this,
                title = "유통기한 임박",
                content = "1일 이하 남은 식재료가 있습니다!"
            )
        }
    }
}
