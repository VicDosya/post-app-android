package com.example.addpost

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(private var posts: List<Post>, private val onPostClick: (Post) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    var loadMoreCallback: (() -> Unit)? = null

    override fun getItemViewType(position: Int): Int {
        return if(position == posts.size) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPostTitle: TextView = itemView.findViewById(R.id.tvPostTitle)
        val tvPostDsc: TextView = itemView.findViewById(R.id.tvPostDsc)
        val tvPostAuthor: TextView = itemView.findViewById(R.id.tvPostAuthor)
        val tvPostDate: TextView = itemView.findViewById(R.id.tvPostDate)
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val btnLoadMore: Button = itemView.findViewById(R.id.btnLoadMore)
    }

    //Function to update posts when filtering through search box
    fun updatePosts(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{
        return if (viewType == VIEW_TYPE_LOADING) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
            LoadingViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.list_post, parent, false)
            PostViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is PostViewHolder){
            if(position < posts.size){
                val post = posts[position]
                holder.tvPostTitle.text = post.title
                holder.tvPostDsc.text = post.description
                holder.tvPostAuthor.text = post.author
                holder.tvPostDate.text = post.createdAt
                holder.itemView.setOnClickListener { onPostClick(post) } // set click listener on item view
            }
        } else if (holder is LoadingViewHolder){
            holder.btnLoadMore.setOnClickListener {
                Log.d("TAG", "Fetching more posts.")
                loadMoreCallback?.invoke()
            }
        }

    }

    override fun getItemCount(): Int = posts.size + 1
}