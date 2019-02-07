package com.testborntocreate.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.testborntocreate.Data.Local.Comment
import com.testborntocreate.Data.Local.CommentWithName
import com.testborntocreate.R

class CommentsAdapter(val list: MutableList<Comment>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.format_comments, parent, false))
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

    fun clearList() {
        list.clear()
        notifyDataSetChanged()
    }

    fun appendComment(commentWithName: CommentWithName) {
        val comment = Comment(commentWithName.comment_id, commentWithName.post_id, commentWithName.name, commentWithName.email, commentWithName.comment)
        list.add(comment)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val comment = list[position]
        (holder as ViewHolder).bind(comment)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val author: TextView = view.findViewById(R.id.format_comments_author)
        private val email: TextView = view.findViewById(R.id.format_comments_email)
        private val comment: TextView = view.findViewById(R.id.format_comments_comment)

        fun bind(comment: Comment) {

            author.text = comment.author
            email.text = comment.email
            this.comment.text = comment.comment
        }
    }
}