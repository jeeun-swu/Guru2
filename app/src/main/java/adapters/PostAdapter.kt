package adapters

import adapters.PostAdapter.PostViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.Singsingbogam.R
import models.Post

class PostAdapter(// 제네릭 타입 명시
    private val datas: List<Post>
) : RecyclerView.Adapter<PostViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val data = datas[position]
        holder.title.text = data.title
        holder.contents.text = data.contents
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val title: TextView =
            itemView.findViewById(R.id.item_post_title)
        val contents: TextView =
            itemView.findViewById(R.id.item_post_contents)
    }
}
