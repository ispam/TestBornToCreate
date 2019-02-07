package com.testborntocreate.Activities

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.testborntocreate.Adapters.CommentsAdapter
import com.testborntocreate.Data.Local.Post
import com.testborntocreate.Data.Remote.APIService
import com.testborntocreate.R
import com.testborntocreate.Utils.isCommentValid
import com.testborntocreate.Utils.isEmailValid
import com.testborntocreate.Utils.isNameValid
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_details.*
import java.util.concurrent.TimeUnit
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
        title = getString(R.string.details_title)

        details_recycler.layoutManager = LinearLayoutManager(this@DetailsActivity, RecyclerView.VERTICAL, false)
        getDetailsFromExtra()
        details_fab.setOnClickListener { createDialogForNewComment() }
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

            loadCommentsOfPost(post.post_id)
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
            .delay(550, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                details_card1.visibility = View.VISIBLE
                details_card2.visibility = View.VISIBLE
                details_recycler.visibility = View.VISIBLE
                details_comment_placeholder.visibility = View.VISIBLE
                details_fab.visibility = View.VISIBLE
                details_recycler.adapter = CommentsAdapter(it.toMutableList())
            }
            .doOnError { e -> Log.e("getAllCommentsOfPost", e.localizedMessage) }
            .subscribe())

    }

    private fun createDialogForNewComment() {
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
                dialog.dismiss()
            }
        }
    }
}
