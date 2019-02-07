package com.testborntocreate.Data.Local

import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("id")
    var comment_id: Int,
    @SerializedName("post_id")
    var post_id: Int,
    @SerializedName("author")
    var author: String,
    @SerializedName("email")
    var email: String,
    @SerializedName("comment")
    var comment: String
)

data class CommentWithName(
    @SerializedName("id")
    var comment_id: Int,
    var post_id: Int,
    var name: String,
    var email: String,
    var comment: String
)