package com.example.Singsingbogam

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat

class DdayListAdapter(private val context: Context, private val ddayItems: List<DdayItem>) : BaseAdapter() {

    override fun getCount(): Int = ddayItems.size

    override fun getItem(position: Int): Any = ddayItems[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_dday_list, parent, false)
            holder = ViewHolder()
            holder.txtName = view.findViewById(R.id.txtName)
            holder.txtDday = view.findViewById(R.id.txtDday)

            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val item = ddayItems[position]
        holder.txtName.text = item.name
        holder.txtDday.text = item.ddayString

        // 이미 유통기한이 지난 경우(daysLeft < 0) 글자색을 초록색으로 변경
        if (item.daysLeft < 0) {
            holder.txtDday.setTextColor( ContextCompat.getColor(context, R.color.red))
        } else {
            // 기본색(검정 등)으로 설정
            holder.txtDday.setTextColor( ContextCompat.getColor(context, R.color.cloudyGreen))
        }

        return view
    }

    private class ViewHolder {
        lateinit var txtName: TextView
        lateinit var txtDday: TextView
    }
}
