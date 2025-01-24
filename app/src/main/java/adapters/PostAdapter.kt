package adapters

import adapters.PostAdapter.PostViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.Singsingbogam.R
import models.Post

class PostAdapter(
    private val datas: List<Post>,
    private val onDelete: (String) -> Unit, // 삭제 이벤트 전달
    private val onEdit: (Post) -> Unit // 수정 이벤트 전달
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val data = datas[position]
        holder.title.text = data.title
        holder.contents.text = data.contents

        // 삭제 버튼 클릭 이벤트
        holder.deleteButton.setOnClickListener {
            onDelete(data.documentId ?: "")
        }

        // 수정 버튼 클릭 이벤트
        holder.editButton.setOnClickListener {
            onEdit(data)
        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.item_post_title)
        val contents: TextView = itemView.findViewById(R.id.item_post_contents)
        val deleteButton: Button = itemView.findViewById(R.id.item_post_delete)
        val editButton: Button = itemView.findViewById(R.id.item_post_edit) // 수정 버튼
    }
}
