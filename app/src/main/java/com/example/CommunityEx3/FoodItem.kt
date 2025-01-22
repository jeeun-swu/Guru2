package com.example.guru2_team_9

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class FoodItem(
    val name: String,
    val purchaseDate: LocalDate,
    val expirationDate: LocalDate
) {
    fun daysLeft(): Long {
        // 오늘 ~ 유통기한까지 남은 일수
        return ChronoUnit.DAYS.between(LocalDate.now(), expirationDate)
    }
}

