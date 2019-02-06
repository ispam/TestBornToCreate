package com.testborntocreate.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.testborntocreate.Data.Local.Post
import com.testborntocreate.R

class PostsAdapter(val list: MutableList<Post>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.format_posts, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val post = list[position]
        (holder as ViewHolder).bind(post)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val author: TextView = view.findViewById(R.id.format_posts_author)
        private val title: TextView = view.findViewById(R.id.format_posts_title)
        private val excerpt: TextView = view.findViewById(R.id.format_posts_excerpt)
        private val thumbnail: ImageView = view.findViewById(R.id.format_posts_thumbnail)

        fun bind(post: Post) {

            author.text = post.author
            title.text = post.title
            excerpt.text = post.content // TODO Recude by length this

            Picasso.get()
                .load(post.thumbnail)
                .fit()
                .into(thumbnail)

        }
    }
}