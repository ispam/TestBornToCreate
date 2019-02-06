package com.testborntocreate.Data.Local

import com.google.gson.annotations.SerializedName

data class Post(
    @SerializedName("id")
    var post_id: Int,
    @SerializedName("author")
    var author: String,
    @SerializedName("title")
    var title: String,
    @SerializedName("thumbnail")
    var thumbnail: String,
    @SerializedName("image")
    var image: String,
    @SerializedName("content")
    var content: String
    )