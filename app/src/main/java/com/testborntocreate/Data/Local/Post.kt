package com.testborntocreate.Data.Local

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
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
    ): Parcelable