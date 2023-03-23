package com.example.addpost

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(private var posts: List<Post>, private val onPostClick: (Post) -> Unit) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPostTitle: TextView = itemView.findViewById(R.id.tvPostTitle)
        val tvPostDsc: TextView = itemView.findViewById(R.id.tvPostDsc)
        val tvPostAuthor: TextView = itemView.findViewById(R.id.tvPostAuthor)
        val tvPostDate: TextView = itemView.findViewById(R.id.tvPostDate)
    }

    //Function to update posts when filtering through search box
    fun updatePosts(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.tvPostTitle.text = post.title
        holder.tvPostDsc.text = post.description
        holder.tvPostAuthor.text = post.author
        holder.tvPostDate.text = post.createdAt
        holder.itemView.setOnClickListener { onPostClick(post) } // set click listener on item view
    }

    override fun getItemCount(): Int = posts.size
}