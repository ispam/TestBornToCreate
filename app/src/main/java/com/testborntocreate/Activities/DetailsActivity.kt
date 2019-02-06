package com.testborntocreate.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.testborntocreate.Adapters.CommentsAdapter
import com.testborntocreate.Data.Local.Post
import com.testborntocreate.Data.Remote.APIService
import com.testborntocreate.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_details.*
import javax.inject.Inject

class DetailsActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()

    @Inject
    lateinit var apiService: APIService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        App.component.inject(this)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        title = "Post Details"

        details_recycler.layoutManager = LinearLayoutManager(this@DetailsActivity, RecyclerView.VERTICAL, false)
        getDetailsFromExtra()
    }

    private fun getDetailsFromExtra() {
        val post = intent.getParcelableExtra<Post>("post")

        details_author.text = post.author
        details_content.text = post.content
        details_title.text = post.title
        Picasso.get()
            .load(post.image)
            .into(details_image)

        loadCommentsOfPost(post.post_id)
    }

    private fun loadCommentsOfPost(post_id: Int) {

        disposable.add(apiService.getAllCommentsOfPost(post_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { details_recycler.adapter = CommentsAdapter(it.toMutableList()) }
            .doOnError { e -> Log.e("getAllCommentsOfPost", e.localizedMessage) }
            .subscribe())

    }
}
