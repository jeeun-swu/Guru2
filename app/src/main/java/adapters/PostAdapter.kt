package adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.Singsingbogam.PostDetailActivity
import com.example.Singsingbogam.R
import models.Post

class PostAdapter(
    private val datas: List<Post>, // 🔥 게시글 리스트
    private val currentUserEmail: String, // 🔥 현재 로그인한 사용자의 이메일
    private val onEditClick: (Post) -> Unit, // 수정 버튼 클릭 콜백 함수
    private val onDeleteClick: (Post) -> Unit // 삭제 버튼 클릭 콜백 함수
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    // 🔥 ViewHolder 생성 (각 리스트 아이템의 레이아웃 설정)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        )
    }

    // 🔥 ViewHolder에 데이터 바인딩 (게시글 정보 설정)
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val data = datas[position]

        // ✅ 게시글 제목 설정
        holder.title.text = data.title

        // ✅ 게시글 내용 50자 제한 (너무 길면 ‘...’ 추가)
        holder.contents.text = if (data.contents!!.length > 50) {
            "${data.contents!!.substring(0, 50)}..."
        } else {
            data.contents!!
        }

        // ✅ 작성자 표시 (이메일에서 '@' 앞부분만 표시)
        holder.author.text = "작성자: ${data.author ?: "알 수 없음"}"

        // ✅ 게시글 클릭 시 상세 페이지로 이동
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, PostDetailActivity::class.java).apply {
                putExtra("documentId", data.documentId) // 게시글 ID 전달
                putExtra("title", data.title) // 제목 전달
                putExtra("contents", data.contents) // 내용 전달
                putExtra("author", data.author) // 작성자 전달
                putExtra("timestamp", data.timestamp) // 작성 날짜 전달
            }
            holder.itemView.context.startActivity(intent)
        }

        // ✅ 현재 로그인한 사용자가 게시글 작성자와 같으면 수정/삭제 버튼 표시
        if (data.fullAuthor?.trim()?.lowercase() == currentUserEmail.trim().lowercase()) {
            holder.editButton.visibility = View.VISIBLE
            holder.deleteButton.visibility = View.VISIBLE
        } else {
            holder.editButton.visibility = View.GONE
            holder.deleteButton.visibility = View.GONE
        }

        // ✅ 수정 버튼 클릭 시 실행할 함수 연결
        holder.editButton.setOnClickListener { onEditClick(data) }

        // ✅ 삭제 버튼 클릭 시 실행할 함수 연결
        holder.deleteButton.setOnClickListener { onDeleteClick(data) }
    }

    // 🔥 리스트 아이템 개수 반환
    override fun getItemCount(): Int = datas.size

    // 🔥 ViewHolder 클래스 (게시글 1개를 나타내는 UI 요소)
    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.item_post_title) // 제목
        val contents: TextView = itemView.findViewById(R.id.item_post_contents) // 내용
        val author: TextView = itemView.findViewById(R.id.item_post_author) // 작성자
        val editButton: Button = itemView.findViewById(R.id.item_post_edit) // 수정 버튼
        val deleteButton: Button = itemView.findViewById(R.id.item_post_delete) // 삭제 버튼
    }
}
