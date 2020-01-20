package com.justimagine.imagine

import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

// This enum declares the type of the post
enum class PostType {
    picture,
    link,
    thought,
    repost,
    event,
    youTubeVideo,
    nothingPostedYet
}

// This enum declares how a post was marked or reported
enum class ReportType {
    normal,
    spoiler,
    opinion,
    sensationalism,
    circlejerk,
    pretentious,
    edited,
    ignorant
}

enum class VoteType {
    thanks,
    wow,
    ha,
    nice
}

class Votes {
    var thanks:Long = 0
    var wow:Long = 0
    var ha:Long = 0
    var nice:Long = 0
}

class Post {
    var title = ""
    var imageURL:String? = null
    var description = ""
    var linkURL = ""
    var type: PostType = PostType.picture
    var imageHeight = 0.0
    var imageWidth = 0.0
    var report: ReportType = ReportType.normal
    var documentID = ""
    var createTime = ""
    var OGRepostDocumentID: String? = null
    var originalPosterUID = ""
    // kann eigentlich weg weil in User Objekt - Ne wegen GetUser
    var commentCount = 0
    var createDate: Date = Date()
    var toComments = false
    // If you want to skip to comments (For now)
    var anonym = false
    var user = User()
    var votes = Votes()
   // var event = Event()
    var repost: Post? = null
    //var fact: Fact? = null
    //val handyHelper = HandyHelper()

    /*fun getRepost(returnRepost: (Post) -> Unit) {

        val db = FirebaseFirestore.getInstance()
        val repostID = OGRepostDocumentID

        if (repostID != null) {
            val postRef = db.collection("Posts").document(repostID)
            val post = Post()
            postRef.get()
                .addOnSuccessListener { document ->

                    val document = document

                    if (document != null) {
                        val docData = document.data()
                        if (docData != null) {
                            val title = docData["title"] as? String
                            val description = docData["description"] as? String
                            val report = docData["report"] as? String
                            val createTimestamp = docData["createTime"] as? Timestamp
                            val originalPoster = docData["originalPoster"] as? String
                            val thanksCount = docData["thanksCount"] as? Int
                            val wowCount = docData["wowCount"] as? Int
                            val haCount = docData["haCount"] as? Int
                            val niceCount = docData["niceCount"] as? Int
                            val postType = docData["type"] as? String
                            if (title == null || description == null || report == null || createTimestamp == null || originalPoster == null || thanksCount == null || wowCount == null || haCount == null || niceCount == null || postType == null) {
                                return@getDocument
                            }
                            val linkURL = docData["link"] as? String ?: ""
                            val imageURL = docData["imageURL"] as? String ?: ""
                            val picHeight = docData["imageHeight"] as? Double ?: 0
                            val picWidth = docData["imageWidth"] as? Double ?: 0
                            val stringDate = createTimestamp.dateValue().formatRelativeString()
                            post.title = title
                            // Sachen zuordnen
                            post.imageURL = imageURL
                            post.imageHeight = CGFloat(picHeight)
                            post.imageWidth = CGFloat(picWidth)
                            post.description = description
                            post.documentID = document.documentID
                            post.createTime = stringDate
                            post.originalPosterUID = originalPoster
                            post.votes.thanks = thanksCount
                            post.votes.wow = wowCount
                            post.votes.ha = haCount
                            post.votes.nice = niceCount
                            post.linkURL = linkURL
                            val reportType = this.handyHelper.setReportType(fetchedString = report)
                            if (reportType != null) {
                                post.report = reportType
                            }
                            val factID = docData["linkedFactID"] as? String
                            if (factID != null) {
                                val fact = Fact(addMoreDataCell = false)
                                fact.documentID = factID
                                post.fact = fact
                            }
                            val postType = this.handyHelper.setPostType(fetchedString = postType)
                            if (postType != null) {
                                post.type = postType
                            }
                            if (originalPoster == "anonym") {
                                post.anonym = true
                            } else {
                                post.getUser()
                            }
                            returnRepost(post)
                        }
                    }
                }
                if (err != null) {
                    print("Wir haben einen Error beim User: ${err?.localizedDescription ?: ""}")
                }
            })
        }
    }*/

    fun getUser() {
        val db = FirebaseFirestore.getInstance()
        // User Daten raussuchen
        val userRef = db.collection("Users").document(originalPosterUID)
        userRef.get()
            .addOnSuccessListener { document ->

                if (document != null) {

                    val docData = document.data

                    if (docData != null) {
                        val user = User()

                        user.name = docData["name"] as? String ?: ""
                        user.surname = docData["surname"] as? String ?: ""
                        user.imageURL = docData["profilePictureURL"] as? String ?: ""
                        user.userUID = this.originalPosterUID
                        user.statusQuote = docData["statusText"] as? String ?: ""
                        user.blocked = docData["blocked"] as? List<String> ?: null

                        this.user = user
                    }
                }
            }
    }
}

public class User {
    public var name = ""
    public var surname = ""
    public var imageURL: String? = null
    public var userUID = ""
    public var blocked: List<String>? = null
    public var statusQuote = ""
}
