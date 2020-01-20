package com.justimagine.imagine.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.justimagine.imagine.CustomViewHolder
import com.justimagine.imagine.Post
import com.justimagine.imagine.R
import kotlinx.android.synthetic.main.post_view_activity.*

class PostViewActivity: AppCompatActivity() {

    var post:Post? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_view_activity)

        val jsonString = intent.getStringExtra(CustomViewHolder.post_string)
        val post = Gson().fromJson(jsonString, Post::class.java)

        this.post = post

        actionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
//        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
//        supportActionBar?.title = title

        setPostValues(post)
    }

    fun setPostValues(post: Post) {
        name_textView.text = "${post.user.name} ${post.user.surname}"
        date_textView.text = post.createTime
        title_textView.text = post.title

        if (post.imageURL.isNullOrEmpty()) {
            post_imageView.setBackgroundResource(R.drawable.ic_launcher_foreground)
        } else {
            Glide.with(post_imageView).load(post.imageURL).into(post_imageView)

            val ratio = post.imageWidth/post.imageHeight

            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            var width = displayMetrics.widthPixels

            var newHeight = width/ratio

            post_imageView.layoutParams.height = newHeight.toInt()
            post_imageView.requestLayout()
        }

        if (post.user.imageURL.isNullOrEmpty()) {
            profilePicture_imageView.setBackgroundResource(R.drawable.ic_launcher_foreground)
        } else {
            Glide.with(profilePicture_imageView).load(post.user.imageURL).into(profilePicture_imageView)
        }
    }

}