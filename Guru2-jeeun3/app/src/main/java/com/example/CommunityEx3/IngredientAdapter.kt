package com.example.singsingbogam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IngredientAdapter(private val ingredientList: List<IngredientWithDday>) :
    RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingredient, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.bind(ingredientList[position])
    }

    override fun getItemCount(): Int = ingredientList.size

    class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName = itemView.findViewById<TextView>(R.id.tvIngredientName)
        private val tvDday = itemView.findViewById<TextView>(R.id.tvDday)

        fun bind(ingredient: IngredientWithDday) {
            tvName.text = ingredient.name
            tvDday.text = "D-${ingredient.dday}"

            // 만약 D-Day가 음수라면(이미 지난 경우), 색상 변경 등 추가 UI 처리를 해도 좋음
        }
    }
}
