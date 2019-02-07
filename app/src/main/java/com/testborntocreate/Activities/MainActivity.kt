package com.testborntocreate.Activities

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.testborntocreate.Adapters.PostsAdapter
import com.testborntocreate.Data.Remote.APIService
import com.testborntocreate.R
import com.testborntocreate.Utils.connectivyManager
import com.testborntocreate.Utils.noInternetConnection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()
    private var menuItem: MenuItem ?= null

    @Inject
    lateinit var apiService: APIService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        App.component.inject(this)

        main_recycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        connectivyManager({
                getListOfPosts()
            }, {
                noInternetConnection(this)
                main_loading.visibility = View.VISIBLE
                main_loading.startAnim()
            }, this@MainActivity)
    }

    private fun getListOfPosts() {
        disposable.add(apiService.getAllPosts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                main_loading.visibility = View.VISIBLE
                main_loading.startAnim()
            }
            .map { main_recycler.adapter = PostsAdapter(it.toMutableList(), this@MainActivity) }
            .doOnError { e -> Log.e("getAllPosts()", e.localizedMessage) }
            .doAfterTerminate {
                main_loading.stopAnim()
                main_loading.visibility = View.INVISIBLE
            }
            .subscribe())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        this.menuItem = menu?.findItem(R.id.refresh)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.refresh -> {
                getListOfPosts()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        if (!disposable.isDisposed) disposable.clear()
        super.onStop()
    }
}
