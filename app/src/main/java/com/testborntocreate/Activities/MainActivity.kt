package com.testborntocreate.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.testborntocreate.Adapters.PostsAdapter
import com.testborntocreate.Data.Remote.APIService
import com.testborntocreate.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()

    @Inject
    lateinit var apiService: APIService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        App.component.inject(this)

        main_recycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        getListOfPosts()
    }

    private fun getListOfPosts() {
        disposable.add(apiService.getAllPosts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { main_recycler.adapter = PostsAdapter(it.toMutableList()) }
            .doOnError { e -> Log.e("getAllPosts()", e.localizedMessage) }
            .subscribe())
    }

    override fun onStop() {
        if (!disposable.isDisposed) disposable.clear()
        super.onStop()
    }
}
