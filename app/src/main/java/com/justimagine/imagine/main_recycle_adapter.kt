package com.justimagine.imagine

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.justimagine.imagine.ui.PostViewActivity
import kotlinx.android.synthetic.main.picture_post_cell.view.*

interface FirebaseUser {
    fun getSingleUser(value: User)
}

class MainAdapter(val postList: List<Post>): RecyclerView.Adapter<CustomViewHolder>() {

    var posts = postList
    var whatever = false

    fun setItems(newPosts: List<Post>) {

        println("Das ist whatever: ${whatever}")

        posts = newPosts
        this.notifyDataSetChanged()

        whatever = true

    }

    companion object {
        const val type_picture = 0
        const val type_thought = 1
        const val type_link = 2
        const val type_youTubeVideo = 3
        const val type_repost = 4
    }

    //Number of items(rows)
    override fun getItemCount(): Int {

        return posts.count()
    }

    override fun getItemViewType(position: Int): Int {
        val post = posts[position]

        when (post.type) {
            PostType.picture -> return type_picture
            PostType.thought -> return type_thought
            PostType.link -> return type_link
            PostType.youTubeVideo -> return type_youTubeVideo
            else -> return type_thought
        }
//        return super.getItemViewType(position)
    }

    //cellforrowat
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)

        //val viewHolder: RecyclerView.ViewHolder =
        when (viewType) {
            type_picture -> {
                val view = layoutInflater.inflate(R.layout.picture_post_cell, parent, false)

                return PictureViewHolder(view)
            }
            type_thought -> {
                val view = layoutInflater.inflate(R.layout.thought_post_cell, parent, false)

                return ThoughtViewHolder(view)
            }
            type_link -> {
                val view = layoutInflater.inflate(R.layout.link_post_cell, parent, false)

                return LinkViewHolder(view)
            }
            else -> {
                val view = layoutInflater.inflate(R.layout.thought_post_cell, parent, false)

                return ThoughtViewHolder(view)
            }
        }
    }


    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

        // Call on itemViewType to get the current case
        val post = posts[position]

        when (holder) {
            is PictureViewHolder -> holder.bind(post)
            is ThoughtViewHolder -> holder.bind(post)
            is LinkViewHolder -> holder.bind(post)
        }

    }



    inner class PictureViewHolder(itemView: View) : CustomViewHolder(itemView) {

        fun bind(post: Post) {

            init(post)

            //Do your view assignment here from the data model
            itemView.title_textView.text = post.title
            val imageView = itemView.picture_imageView
            itemView.createDate_textView.text = post.createTime

            if (post.imageURL.isNullOrEmpty()) {
                println("No image: ${post.title}")
            } else {
                Glide.with(itemView).load(post.imageURL).into(imageView)
            }

            itemView.picture_imageView.clipToOutline = true


            if (post.anonym) {
                itemView.name_textView.text = anonym_string
                Glide.with(itemView).load(R.drawable.default_user).into(itemView.profile_picture_imageView)
            } else {
                if (post.user.name != "") {
                    setName(post)
                } else {
                    this.getUser(post.originalPosterUID, object: FirebaseUser{
                        override fun getSingleUser(value: User) {
                            post.user = value
                            setName(post)
                        }

                    })
                }
            }

            itemView.setOnClickListener {


                val jsonObject = Gson().toJson(post)
                val intent = Intent(itemView.context, PostViewActivity::class.java)

                intent.putExtra(post_string, jsonObject)
                itemView.context.startActivity(intent)
            }
        }

        fun setName(post: Post) {
            itemView.name_textView.text = post.user.name

            if (post.user.imageURL.isNullOrEmpty()) {
                Glide.with(itemView).load(R.drawable.default_user).into(itemView.profile_picture_imageView)
            } else {
                Glide.with(itemView).load(post.user.imageURL).into(itemView.profile_picture_imageView)
            }
        }
    }

    inner class ThoughtViewHolder(itemView: View) : CustomViewHolder(itemView) {

        fun bind(post: Post) {

            init(post)

            itemView.title_textView.text = post.title
            itemView.createDate_textView.text = post.createTime

            //User
            if (post.anonym) {
                itemView.name_textView.text = anonym_string
                Glide.with(itemView).load(R.drawable.default_user).into(itemView.profile_picture_imageView)
            } else {
                if (post.user.name != "") {
                    setName(post)
                } else {
                    this.getUser(post.originalPosterUID, object: FirebaseUser{
                        override fun getSingleUser(value: User) {
                            post.user = value
                            setName(post)
                        }
                    })
                }
            }


            itemView.setOnClickListener {


                val jsonObject = Gson().toJson(post)
                val intent = Intent(itemView.context, PostViewActivity::class.java)

                intent.putExtra(post_string, jsonObject)
                itemView.context.startActivity(intent)
            }
        }

        fun setName(post: Post) {
            itemView.name_textView.text = post.user.name

            if (post.user.imageURL.isNullOrEmpty()) {
                Glide.with(itemView).load(R.drawable.default_user).into(itemView.profile_picture_imageView)
            } else {
                Glide.with(itemView).load(post.user.imageURL).into(itemView.profile_picture_imageView)
            }
        }
    }


    inner class LinkViewHolder(itemView: View) : CustomViewHolder(itemView) {

        fun bind(post: Post) {

            init(post)

            itemView.profile_picture_imageView

            itemView.title_textView.text = post.title
            itemView.createDate_textView.text = post.createTime


            //User
            if (post.anonym) {
                itemView.name_textView.text = anonym_string
                Glide.with(itemView).load(R.drawable.default_user).into(itemView.profile_picture_imageView)
            } else {
                if (post.user.name != "") {
                    setName(post)
                } else {
                    this.getUser(post.originalPosterUID, object: FirebaseUser{
                        override fun getSingleUser(value: User) {
                            post.user = value
                            setName(post)
                        }
                    })
                }
            }

            //Click
            itemView.setOnClickListener {


                val jsonObject = Gson().toJson(post)
                val intent = Intent(itemView.context, PostViewActivity::class.java)

                intent.putExtra(post_string, jsonObject)
                itemView.context.startActivity(intent)
            }
        }

        fun setName(post: Post) {
            itemView.name_textView.text = post.user.name

            if (post.user.imageURL.isNullOrEmpty()) {
                Glide.with(itemView).load(R.drawable.default_user).into(itemView.profile_picture_imageView)
            } else {
                Glide.with(itemView).load(post.user.imageURL).into(itemView.profile_picture_imageView)
            }
        }
    }

}

abstract class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        val post_string = "postForDetail"

        val anonym_string = "Ein anonymer User"
    }


    fun init(post:Post) {

        val handyHelper = HandyHelper()

        itemView.thanks_button.setOnClickListener {
            handyHelper.updateVote(VoteType.thanks,post)
            updateButtonUI(VoteType.thanks,post)
        }
        itemView.wow_button.setOnClickListener {
            handyHelper.updateVote(VoteType.wow,post)
            updateButtonUI(VoteType.wow,post)
        }
        itemView.ha_button.setOnClickListener {
            handyHelper.updateVote(VoteType.ha,post)
            updateButtonUI(VoteType.ha,post)
        }
        itemView.nice_button.setOnClickListener {
            handyHelper.updateVote(VoteType.nice,post)
            updateButtonUI(VoteType.nice,post)
        }
    }

    fun updateButtonUI(type:VoteType, post: Post) {
        when (type) {
            VoteType.thanks -> {itemView.thanks_button.background = null
                itemView.thanks_button.text = post.votes.thanks.toString()}
            VoteType.wow -> {itemView.wow_button.background = null
                itemView.wow_button.text = post.votes.wow.toString()}
            VoteType.ha -> {itemView.ha_button.background = null
                    itemView.ha_button.text = post.votes.ha.toString()}
            VoteType.nice -> {itemView.nice_button.background = null
                itemView.nice_button.text = post.votes.nice.toString()}
        }

    }

    fun getUser(userID: String, callback: FirebaseUser) {
        val db = FirebaseFirestore.getInstance()
        // User Daten raussuchen

        if (userID != "") {
            val userRef = db.collection("Users").document(userID)
            userRef.get()
                .addOnSuccessListener { document ->

                    if (document != null) {

                        val docData = document.data

                        if (docData != null) {
                            val user = User()

                            user.name = docData["name"] as? String ?: ""
//                            user.surname = docData["surname"] as? String ?: ""
                            user.imageURL = docData["profilePictureURL"] as? String ?: ""
                            user.userUID = document.id
                            user.statusQuote = docData["statusText"] as? String ?: ""
                            user.blocked = docData["blocked"] as? List<String> ?: null

                            callback.getSingleUser(user)
                        }
                    }
                }
        } else {
            println("No UserID")
        }
    }
}
