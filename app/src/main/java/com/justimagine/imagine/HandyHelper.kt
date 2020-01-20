package com.justimagine.imagine

import com.google.firebase.firestore.FirebaseFirestore

class HandyHelper {

    val db = FirebaseFirestore.getInstance()

    fun updateVote(type: VoteType, post: Post) {

        var ref = db.collection("Posts").document(post.documentID)

        when (type) {
            VoteType.thanks -> {
                ref.update("thanksCount", post.votes.thanks + 1)
                    .addOnSuccessListener { println("DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { println("Error updating document") }
                post.votes.thanks = post.votes.thanks + 1
            }

            VoteType.wow -> {
                ref.update("wowCount", post.votes.wow + 1)
                    .addOnSuccessListener { println("DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { println("Error updating document") }
                post.votes.wow = post.votes.wow + 1
            }
            VoteType.ha -> {
                ref.update("haCount", post.votes.ha + 1)
                    .addOnSuccessListener { println("DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { println("Error updating document") }
                post.votes.ha = post.votes.ha + 1
            }
            VoteType.nice -> {
                ref.update("niceCount", post.votes.nice + 1)
                    .addOnSuccessListener { println("DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { println("Error updating document") }
                post.votes.nice = post.votes.nice + 1
            }
        }

    }

}