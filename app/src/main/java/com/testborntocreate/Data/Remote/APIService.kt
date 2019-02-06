package com.testborntocreate.Data.Remote

import com.testborntocreate.Data.Local.Comment
import com.testborntocreate.Data.Local.Post
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface APIService {

    @GET("/posts")
    fun getAllPosts(): Single<List<Post>>

    @GET("/posts/{postId}/comments")
    fun getAllCommentsOfPost(@Path("postId") post_id: Int): Single<List<Comment>>

    @POST("/posts/{postId}/comments")
    fun createNewCommentForPost(@Path("postId") post_id: String): Single<Comment>
}