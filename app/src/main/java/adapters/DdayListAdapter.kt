package com.example.Singsingbogam

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat

//ListView에 표시할 DdayItem 데이터 목록을 받아, 각 항목(아이템)의 뷰를 만들어주는 커스텀 어댑터 클래스

class DdayListAdapter(
    private val context: Context,
    private val ddayItems: List<DdayItem>
) : BaseAdapter() {

    //전체 아이템 수 반환
    override fun getCount(): Int = ddayItems.size

    //position 위치의 아이템 객체 반환
    override fun getItem(position: Int): Any = ddayItems[position]

    //position 위치의 아이템 ID(여기서는 position을 그대로 사용)
    override fun getItemId(position: Int): Long = position.toLong()

    //position 위치의 리스트 항목을 실제로 화면에 표시해주는 메서드
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        // 재사용 가능한 뷰(convertView)가 없으면 새로 생성하고 ViewHolder를 붙임
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_dday_list, parent, false)
            holder = ViewHolder().apply {
                txtName = view.findViewById(R.id.txtName)
                txtDday = view.findViewById(R.id.txtDday)
            }
            view.tag = holder
        } else {
            // 이미 생성된 뷰가 있으면 재활용
            view = convertView
            holder = view.tag as ViewHolder
        }

        // position 위치의 DdayItem 데이터를 가져옴
        val item = ddayItems[position]

        // 재료 이름과 D-day 문자열을 TextView에 세팅
        holder.txtName.text = item.name
        holder.txtDday.text = item.ddayString

        // 이미 유통기한이 지난 경우(daysLeft < 0) 글자색을 빨간색으로 변경
        if (item.daysLeft < 0) {
            holder.txtDday.setTextColor( ContextCompat.getColor(context, R.color.red))
        } else {
            // 초록색으로 설정
            holder.txtDday.setTextColor( ContextCompat.getColor(context, R.color.cloudyGreen))
        }
        return view
    }

    //뷰를 재활용하기 위한 내부 클래스: 각 항목 뷰에 대한 참조 보관
    private class ViewHolder {
        lateinit var txtName: TextView
        lateinit var txtDday: TextView
    }
}
