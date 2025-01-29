package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.Singsingbogam.R
import models.Comment

class CommentAdapter(
    private val comments: List<Comment>, // 🔥 Firestore에서 가져온 댓글 리스트
    private val currentUserEmail: String, // 🔥 현재 로그인한 사용자의 이메일
    private val postAuthorEmail: String, // 🔥 게시글 작성자의 이메일
    private val onDeleteClick: (Comment) -> Unit // 🔥 댓글 삭제 클릭 이벤트 콜백 함수
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]

        // ✅ 댓글 작성자의 이메일에서 '@' 앞부분만 가져와 표시
        holder.author.text = "${comment.author?.substringBefore("@")} 님의 댓글"
        holder.content.text = comment.content

        // 🔥 현재 로그인한 사용자가 작성한 댓글만 삭제 버튼을 보이도록 설정
        if (comment.author == currentUserEmail) {
            holder.deleteButton.visibility = View.VISIBLE
            holder.deleteButton.setOnClickListener { onDeleteClick(comment) } // 삭제 기능 실행
        } else {
            holder.deleteButton.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = comments.size // 🔥 댓글 개수를 반환

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val author: TextView = itemView.findViewById(R.id.comment_author) // 댓글 작성자 표시
        val content: TextView = itemView.findViewById(R.id.comment_content) // 댓글 내용 표시
        val deleteButton: ImageView = itemView.findViewById(R.id.comment_delete_btn) // 댓글 삭제 버튼 (X 아이콘)
    }
}
