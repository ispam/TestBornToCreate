package com.testborntocreate.Activities

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.testborntocreate.Adapters.CommentsAdapter
import com.testborntocreate.Data.Local.CommentWithName
import com.testborntocreate.Data.Local.Post
import com.testborntocreate.Data.Remote.APIService
import com.testborntocreate.R
import com.testborntocreate.Utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_details.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.random.Random


class DetailsActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()
    private var menuItem: MenuItem? = null

    @Inject
    lateinit var apiService: APIService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        App.component.inject(this)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.details_title)

        details_recycler.layoutManager = LinearLayoutManager(this@DetailsActivity, RecyclerView.VERTICAL, false)
        getDetailsFromExtra()

    }

    private fun getDetailsFromExtra() {

        if (intent.extras != null) {
            val post = intent.getParcelableExtra<Post>("post")

            details_author.text = post.author
            details_content.text = post.content
            details_title.text = post.title
            Picasso.get()
                .load(post.image)
                .into(details_image)

            connectivyManager({ loadCommentsOfPost(post.post_id) }, {
                noInternetConnection(this@DetailsActivity)
                details_loading.startAnim()
                details_loading.visibility = View.VISIBLE
            }, this@DetailsActivity)

        } else {
            Toast.makeText(
                this@DetailsActivity,
                getString(R.string.details_no_info),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun loadCommentsOfPost(post_id: Int) {

        disposable.add(apiService.getAllCommentsOfPost(post_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { details_loading.startAnim() }
            .delay(550, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                details_loading.visibility = View.INVISIBLE
                details_card1.visibility = View.VISIBLE
                details_card2.visibility = View.VISIBLE
                details_recycler.visibility = View.VISIBLE
                details_comment_placeholder.visibility = View.VISIBLE
                details_fab.visibility = View.VISIBLE
                val adapter = CommentsAdapter(it.toMutableList())
                details_recycler.adapter = adapter

                details_fab.setOnClickListener { createDialogForNewComment(adapter) }
            }
            .doOnError { e -> Log.e("getAllCommentsOfPost", e.localizedMessage) }
            .subscribe())

    }

    private fun createDialogForNewComment(adapter: CommentsAdapter) {
        val builder = AlertDialog.Builder(this@DetailsActivity)
        val inflater = this@DetailsActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_comment, null, true)
        val name = view.findViewById<EditText>(R.id.dialog_edit_name)
        val email = view.findViewById<EditText>(R.id.dialog_edit_email)
        val comment = view.findViewById<EditText>(R.id.dialog_edit_comment)

        val dialog: AlertDialog = builder
            .setView(view)
            .setNegativeButton(view.context.getString(R.string.dialog_cancel), null)
            .setPositiveButton(view.context.getString(R.string.dialog_create)) { _, _ -> }
            .setCancelable(false)
            .create()

        dialog.show()

        val positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        positive.setTextColor(Color.BLACK)
        negative.setTextColor(Color.BLACK)

        positive.setOnClickListener {
            var nameBoolean = false
            var emailBoolean = false
            var commentBoolean = false

            when {
                email.text.length > 5 && isEmailValid(email.text.toString()) -> emailBoolean = true
                email.text.isNullOrEmpty() -> email.error = getString(R.string.dialog_email_empty)
                else -> email.error = getString(R.string.dialog_email_invalid)
            }

            when {
                isNameValid(name.text.toString()) -> nameBoolean = true
                name.text.isNullOrEmpty() -> name.error = getString(R.string.dialog_name_empty)
                else -> name.error = getString(R.string.dialog_name_invalid)
            }

            when {
                isCommentValid(comment.text.toString()) -> commentBoolean = true
                comment.text.isNullOrEmpty() -> comment.error = getString(R.string.dialog_comment_empty)
                else -> comment.error = getString(R.string.dialog_comment_range)
            }
            if (emailBoolean && nameBoolean && commentBoolean) {
                createComment(name.text.toString(), email.text.toString(), comment.text.toString(), adapter, view)
                dialog.dismiss()
            }
        }
    }

    private fun createComment(name: String, email: String, comment: String, adapter: CommentsAdapter, view: View) {
        val random = Random.nextInt(9999)
        val post = intent.getParcelableExtra<Post>("post")

        val commentWithName = CommentWithName(random, post.post_id, name, email, comment)
        connectivyManager({
            disposable.add(apiService.createNewCommentForPost(post.post_id, commentWithName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgressDialog(view.context) }
                .delay(350, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .map { adapter.appendComment(commentWithName) }
                .doAfterTerminate { hideProgressDialog() }
                .subscribe())
        },
            {
                noInternetConnection(this@DetailsActivity)
            }, this@DetailsActivity
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        this.menuItem = menu?.findItem(R.id.refresh)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.refresh -> {
                getDetailsFromExtra()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        hideProgressDialog()
        if (!disposable.isDisposed) disposable.clear()
        super.onStop()
    }
}
