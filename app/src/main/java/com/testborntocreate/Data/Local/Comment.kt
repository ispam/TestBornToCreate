package com.testborntocreate.Data.Local

import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("id")
    var comment_it: Int,
    @SerializedName("post_id")
    var post_id: Int,
    @SerializedName("name")
    var name: String,
    @SerializedName("email")
    var email: String,
    @SerializedName("comment")
    var comment: String
)