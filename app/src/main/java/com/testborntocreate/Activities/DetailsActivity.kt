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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_details.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
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
                "Oops!, we didn't receive all the information we needeed",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun loadCommentsOfPost(post_id: Int) {

        disposable.add(apiService.getAllCommentsOfPost(post_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
//                 val displayMetrics = DisplayMetrics()
//                windowManager.defaultDisplay.getMetrics(displayMetrics)
//                val height = displayMetrics.heightPixels
//                val params = details_guideline.layoutParams as ConstraintLayout.LayoutParams
//                params.guidePercent = height.toFloat()
//                details_guideline.layoutParams = params
//                details_loading.startAnim()
            }
            .delay(550, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map {
//                details_loading.stopAnim()
//                details_loading.visibility = View.INVISIBLE
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
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@DetailsActivity)
        val inflater = this@DetailsActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_comment, null, true)
        val name = view.findViewById<EditText>(R.id.dialog_edit_name)
        val email = view.findViewById<EditText>(R.id.dialog_edit_email)
        val comment = view.findViewById<EditText>(R.id.dialog_edit_comment)

        val dialog: AlertDialog = builder
            .setNegativeButton(view.context.getString(R.string.dialog_cancel), null)
            .setPositiveButton(view.context.getString(R.string.dialog_create)) { _, _ ->

            }
            .setView(view)
            .create()

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
    }

    fun isEmailValid(email: String): Boolean {
        val regExpn = ("^([a-z\\s])")

        val pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)

        return matcher.matches()
    }

}
