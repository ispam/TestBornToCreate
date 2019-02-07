package com.testborntocreate

import com.testborntocreate.Data.Local.Comment
import com.testborntocreate.Data.Local.CommentWithName
import com.testborntocreate.Data.Local.Post
import com.testborntocreate.Data.Remote.APIService
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.random.Random

class DomainLogicTests {

    val apiService = mockk<APIService>(relaxed = true)

    @BeforeEach
    fun clear() {
        clearMocks(apiService)
    }

    @Nested
    inner class HomeActivityTests {

        @Test
        fun `getAllPosts() method is not empty`() {

            every { apiService.getAllPosts() } returns Single.just(testPostList())

            apiService.getAllPosts()
                .test()
                .assertSubscribed()
                .assertNoErrors()
                .assertResult(testPostList())
                .assertComplete()
                .dispose()

        }
    }

    @Nested
    inner class DetailsActivityTests {

        @Test
        fun `getAllCommentsOfPost() method is not empty`() {

            val random = randomGenerator()

            every { apiService.getAllCommentsOfPost(random) } answers { Single.just(testCommentList()) }

            apiService.getAllCommentsOfPost(random)
                .test()
                .assertSubscribed()
                .assertNoErrors()
                .assertComplete()
                .assertResult(testCommentList())
                .dispose()
        }

        @Test
        fun `createNewCommentForPost() method creates comment`() {

            val commentWithName = CommentWithName(
                randomGenerator(),
                randomGenerator(),
                "Test With Name 1",
                "withname@withname.com",
                "This is a basic comment"
            )

            val comment =  Comment(
                commentWithName.comment_id,
                commentWithName.post_id,
                commentWithName.name,
                commentWithName.email,
                commentWithName.comment
            )

            val random = randomGenerator()

            every { apiService.createNewCommentForPost(random, commentWithName) } answers { Single.just(comment) }


            apiService.createNewCommentForPost(random, commentWithName)
                .test()
                .assertSubscribed()
                .assertNoErrors()
                .assertResult(comment)
                .assertComplete()
                .dispose()
        }

    }

    private fun randomGenerator() = Random.nextInt(20)

    private fun testCommentList(): List<Comment> {

        val comment1 = Comment(1, 1, "TestName 1", "test1@test1.com", "this is a comment from test 1")
        val comment2 = Comment(2, 2, "TestName 2", "test2@test2.com", "this is a comment from test 2")
        val comment3 = Comment(3, 3, "TestName 3", "test3@test3.com", "this is a comment from test 3")
        val comment4 = Comment(4, 4, "TestName 4", "test4@test4.com", "this is a comment from test 4")
        val comment5 = Comment(5, 5, "TestName 5", "test5@test5.com", "this is a comment from test 5")

        return listOf(comment1, comment2, comment3, comment4, comment5)
    }

    private fun testPostList(): List<Post> {

        val post1 = Post(
            1,
            "test1",
            "Title 1",
            "https://placeholder.240*240.com",
            "https://placeholder.650*650.com",
            "Random content 1"
        )
        val post2 = Post(
            2,
            "test2",
            "Title 2",
            "https://placeholder.240*240.com",
            "https://placeholder.650*650.com",
            "Random content 2"
        )
        val post3 = Post(
            3,
            "test3",
            "Title 3",
            "https://placeholder.240*240.com",
            "https://placeholder.650*650.com",
            "Random content 3"
        )
        val post4 = Post(
            4,
            "test4",
            "Title 4",
            "https://placeholder.240*240.com",
            "https://placeholder.650*650.com",
            "Random content 4"
        )
        val post5 = Post(
            5,
            "test5",
            "Title 5",
            "https://placeholder.240*240.com",
            "https://placeholder.650*650.com",
            "Random content 5"
        )
        val post6 = Post(
            6,
            "test6",
            "Title 6",
            "https://placeholder.240*240.com",
            "https://placeholder.650*650.com",
            "Random content 6"
        )

        return listOf(post1, post2, post3, post4, post5, post6)

    }
}