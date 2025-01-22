package com.example.guru2_team_9

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.guru2_team_9.databinding.ItemFoodBinding

class FoodAdapter(private var foodList: List<FoodItem>)
    : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    inner class FoodViewHolder(private val binding: ItemFoodBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FoodItem) {
            // TextView들에 각각 식재료 정보 표시
            binding.tvName.text = item.name
            binding.tvPurchaseDate.text = "구매일: ${item.purchaseDate}"
            binding.tvExpirationDate.text = "유통기한: ${item.expirationDate}"

            // D-Day 계산 후 텍스트
            val dDay = item.daysLeft()
            val dDayText = when {
                dDay < 0 -> "D+${-dDay} (이미 지남)"
                dDay == 0L -> "D-Day (오늘까지)"
                else -> "D-$dDay"
            }
            binding.tvDDay.text = dDayText
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(foodList[position])
    }

    override fun getItemCount(): Int = foodList.size

    fun updateList(newList: List<FoodItem>) {
        foodList = newList
        notifyDataSetChanged()
    }
}
