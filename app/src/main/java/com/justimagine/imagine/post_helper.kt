package com.justimagine.imagine

import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

enum class PostList {
    postsFromUser,
    savedPosts
}

interface FirebaseCallback {
    fun onCallback(value: List<Post>)
}

class PostHelper {
    var posts = ArrayList<Post>()
    val db = FirebaseFirestore.getInstance()
    //    val handyHelper = HandyHelper()
    var initialFetch = true
    var lastSnap: DocumentSnapshot? = null
    var lastEventSnap: QueryDocumentSnapshot? = null
    var lastSavedPostsSnap: QueryDocumentSnapshot? = null
    val factJSONString = "linkedFactID"
    // These two variables are here to make sure that we just fetch as many as there are documents and dont start at the beginning again
    var morePostsToFetch = true
    var alreadyFetchedCount = 0

//getMore: Boolean
    fun getPostsForMainFeed(getMore: Boolean, callback: FirebaseCallback) {
//        posts.removeAll()

        var postRef = db.collection("Posts").orderBy("createTime", Query.Direction.DESCENDING)
            .limit(20)

        if (getMore) {
            // If you want to get More Posts
            val lastSnap = lastSnap
            if (lastSnap != null) {
                // For the next loading batch of 20, that will start after this snapshot
                postRef = postRef.startAfter(lastSnap)
                this.initialFetch = false
            } else {
                println("Snap ist nicht da")
            }
        } else {
            // Else you want to refresh the feed
            this.initialFetch = true
        }
        postRef.get()
            .addOnSuccessListener { querySnapshot ->

                println("Start the loop: ${querySnapshot.documents.count()}##")
                val lastVisible = querySnapshot.documents[querySnapshot.size() - 1]
                this.lastSnap =lastVisible

//                this.lastSnap = querySnapshot?.documents?.lastOrNull()
                // Last document for the next fetch cycle
                for (document in querySnapshot.documents) {
                    this.addThePost(document = document)
                    println("Add Post to Array##")
                }
                println("Call callback##")
                callback.onCallback(this.posts)

//                this.getCommentCount(completion = {
//
//                    returnPosts(this.posts)
//                })
            }
    }

//    //MARK: - Saved and UserPosts
//    fun getPostList(getMore: Boolean, whichPostList: PostList, userUID: String, returnPosts: (List<Post>?, _ InitialFetch: Boolean) -> Unit) {
//        // check if there are more posts to fetch
//        if (morePostsToFetch) {
//            posts.removeAll()
//            var postListReference: String? = null
//            when (whichPostList) {
//                    .pos tsFromUser -> postListReference = "posts"
//                .savedPosts -> postListReference = "saved"
//            }
//            print("Hier wird jetzt gearbeitet. InitialFetch: ", initialFetch)
//            var documentIDsOfPosts = listOf<String>()
//            val ref = postListReference
//            if (ref != null) {
//                var userPostRef = db.collection("Users").document(userUID).collection(ref).order(by = "createTime", descending = true).limit(to = 20)
//                // Check if the Feed has been refreshed or the next batch is ordered
//                if (getMore) {
//                    // For the next loading batch of 20, that will start after this snapshot if it is there
//                    val lastSnap = lastSavedPostsSnap
//                    if (lastSnap != null) {
//                        // I think I have an issue with createDate + .start(afterDocument:) because there are some without date
//                        userPostRef = userPostRef.start(afterDocument = lastSnap)
//                        this.initialFetch = false
//                    }
//                } else {
//                    // Else you want to refresh the feed
//                    this.initialFetch = true
//                }
//                userPostRef.getDocuments { querySnapshot, err  ->
//                    val error = err
//                    if (error != null) {
//                        print("Wir haben einen Error bei den Userposts: ${error.localizedDescription}")
//                    } else {
//                        if (querySnapshot!!.documents.size == 0) {
//                            // Hasnt posted or saved anything yet
//                            val post = Post()
//                            post.type = .nothingPostedYet
//                                    returnPosts(listOf<post>, this.initialFetch)
//                        }
//                        val fetchedDocsCount = querySnapshot!!.documents.size
//                        this.alreadyFetchedCount = this.alreadyFetchedCount + fetchedDocsCount
//                        val fullCollectionRef = this.db.collection("Users").document(userUID).collection(ref)
//                        this.checkHowManyDocumentsThereAre(ref = fullCollectionRef)
//                        this.lastSavedPostsSnap = querySnapshot?.documents?.lastOrNull()
//                        // For the next batch
//                        when (whichPostList) {
//                                .postsFromUser -> {
//                            for (document in querySnapshot!!.documents) {
//                                val documentID = document.documentID
//                                documentIDsOfPosts.append(documentID)
//                            }
//                            this.getPostsFromDocumentIDs(documentIDs = documentIDsOfPosts, done = { _  ->
//                                // Needs to be sorted because the posts are fetched without the date that they were added
//                                this.posts.sort(by = { it.createDate?.compare($1.createDate ?: Date()) == .orderedDescending })
//                                returnPosts(this.posts, this.initialFetch)
//                            })
//                        }
//                                .savedPosts -> {
//                            for (document in querySnapshot!!.documents) {
//                                val docData = document.data()
//                                val documentID = docData["documentID"] as? String
//                                if (documentID != null) {
//                                    print("DocID: ", documentID)
//                                    documentIDsOfPosts.append(documentID)
//                                }
//                            }
//                            this.getPostsFromDocumentIDs(documentIDs = documentIDsOfPosts, done = { _  ->
//                                // Needs to be sorted because the posts are fetched without the date that they were added
//                                this.posts.sort(by = { it.createDate?.compare($1.createDate ?: Date()) == .orderedDescending })
//                                returnPosts(this.posts, this.initialFetch)
//                            })
//                        }
//                        }
//                    }
//                }
//            }
//        } else {
//            // No more Posts to fetch = End of list
//            print("We already have all posts fetched")
//            returnPosts(null, this.initialFetch)
//        }
//    }

//    fun getPostsForFact(factID: String, posts: (List<Post>) -> Unit) {
//        val ref = db.collection("Facts").document(factID).collection("posts")
//        var documentIDsOfPosts = listOf<String>()
//        ref.getDocuments { snap, err  ->
//            val error = err
//            if (error != null) {
//                print("We have an error: ${error.localizedDescription}")
//            } else {
//                if (snap!!.documents.size == 0) {
//                    // Hasnt posted or saved anything yet
//                    val post = Post()
//                    post.type = .nothingPostedYet
//                            posts(listOf<post>)
//                } else {
//                    for (document in snap!!.documents) {
//                        val documentID = document.documentID
//                        documentIDsOfPosts.append(documentID)
//                    }
//                    this.getPostsFromDocumentIDs(documentIDs = documentIDsOfPosts, done = { _  ->
//                        // Needs to be sorted because the posts are fetched without the date that they were added
//                        this.posts.sort(by = { it.createDate?.compare($1.createDate ?: Date()) == .orderedDescending })
//                        posts(this.posts)
//                    })
//                }
//            }
//        }
//    }

    fun checkHowManyDocumentsThereAre(ref: CollectionReference) {
        ref.get()
            .addOnSuccessListener { querySnap ->

                val wholeCollectionDocumentCount = querySnap!!.documents.size
                if (wholeCollectionDocumentCount <= this.alreadyFetchedCount) {
                    this.morePostsToFetch = false
                }

            }
    }

    // MARK: Get Posts from DocumentIDs
    fun getPostsFromDocumentIDs(documentIDs: List<String>, done: (List<Post>?) -> Unit) {
        print("Get posts")
        val endIndex = documentIDs.size
        var startIndex = 0
        // The function has to be here for the right order
        for (documentIDofPost in documentIDs) {
            this.db.collection("Posts").document(documentIDofPost)
                .get()
                .addOnSuccessListener { document ->

                    val document = document
                    if (document != null) {
                        this.addThePost(document = document)
                        startIndex = startIndex + 1
                        if (startIndex == endIndex) {
                            this.getCommentCount(completion = { done(this.posts) })
                        }
                    }

                }
        }
    }

    //MARK: -add Post
    fun addThePost(document: DocumentSnapshot) {
        val documentID = document.id
        val documentData = document.data
        if (documentData != null) {
            val postType = documentData["type"] as? String
            if (postType != null) {
                // Werte die alle haben
                val title = documentData["title"] as? String
                val description = documentData["description"] as? String
                val reportString = documentData["report"] as? String
                val createTimestamp = documentData["createTime"] as? Timestamp
                val originalPoster = documentData["originalPoster"] as? String
                val thanksCount = documentData["thanksCount"] as Long
                val wowCount = documentData["wowCount"] as Long
                val haCount = documentData["haCount"] as Long
                val niceCount = documentData["niceCount"] as Long

                // Weggenommen unter dem hier: || thanksCount == null || wowCount == null || haCount == null || niceCount == null

                if (title == null || description == null || reportString == null || createTimestamp == null || originalPoster == null ) {
                    println("Return because not all data##")
                    return
                }
                val dateToSort = createTimestamp.toDate()
                val stringDate = createTimestamp.toDate().formatToViewDateDefaults() //ToDo.formatForFeed()
                // Thought
                if (postType == "thought") {





                    val post = Post()
                    // Erst neuen Post erstellen
                    post.title = title
                    // Dann die Sachen zuordnen
                    post.description = description
                    post.type = PostType.thought
                    post.documentID = documentID
                    post.createTime = stringDate
                    post.originalPosterUID = originalPoster
                    post.votes.thanks = thanksCount
                    post.votes.wow = wowCount
                    post.votes.ha = haCount
                    post.votes.nice = niceCount
                    post.createDate = dateToSort
//                    val report = this.handyHelper.setReportType(fetchedString = reportString)
//                    if (report != null) {
//                        post.report = report
//                    }
                    val factID = documentData[factJSONString] as? String
                    if (factID != null) {
//                        val fact = Fact(addMoreDataCell = false)
//                        fact.documentID = factID
//                        post.fact = fact
                    }
                    if (originalPoster == "anonym") {
                        post.anonym = true
                    } else {
                        post.getUser()
                    }
                    this.posts.add(post)
                } else // Picture
                    if (postType == "picture") {
                        val imageURL = documentData["imageURL"] as? String
                        val picHeight = documentData["imageHeight"] as? Double
                        val picWidth = documentData["imageWidth"] as? Double
                        if (imageURL == null || picHeight == null || picWidth == null) {
                            return
                        }
                        val post = Post()
                        post.title = title
                        post.imageURL = imageURL
                        post.imageHeight = picHeight
                        post.imageWidth = picWidth
                        post.description = description
                        post.type = PostType.picture
                        post.documentID = documentID
                        post.createTime = stringDate
                        post.originalPosterUID = originalPoster
                        post.votes.thanks = thanksCount
                        post.votes.wow = wowCount
                        post.votes.ha = haCount
                        post.votes.nice = niceCount
                        post.createDate = dateToSort
//                        val report = this.handyHelper.setReportType(fetchedString = reportString)
//                        if (report != null) {
//                            post.report = report
//                        }
                        val factID = documentData[factJSONString] as? String
                        if (factID != null) {
//                            val fact = Fact(addMoreDataCell = false)
//                            fact.documentID = factID
//                            post.fact = fact
                        }
                        if (originalPoster == "anonym") {
                            post.anonym = true
                            print("Set Post as anonym")
                        } else {
                            post.getUser()
                        }
                        this.posts.add(post)
                        println("Add Picture Post ##")
                    } else if (postType == "youTubeVideo") {


                            return


                            val linkURL = documentData["link"] as? String ?: return
                            val post = Post()
                            post.title = title
                            post.linkURL = linkURL
                            post.description = description
                            post.type = PostType.youTubeVideo
                            post.documentID = documentID
                            post.createTime = stringDate
                            post.originalPosterUID = originalPoster
                            post.votes.thanks = thanksCount
                            post.votes.wow = wowCount
                            post.votes.ha = haCount
                            post.votes.nice = niceCount
                            post.createDate = dateToSort
//                            val report = this.handyHelper.setReportType(fetchedString = reportString)
//                            if (report != null) {
//                                post.report = report
//                            }
                            val factID = documentData[factJSONString] as? String
                            if (factID != null) {
//                                val fact = Fact(addMoreDataCell = false)
//                                fact.documentID = factID
//                                post.fact = fact
                            }
                            if (originalPoster == "anonym") {
                                post.anonym = true
                            } else {
                                post.getUser()
                            }
                            this.posts.add(post)
                        } else if (postType == "link") {
                                val linkURL = documentData["link"] as? String ?: return
                                // Falls er das nicht als (String) zuordnen kann
                                val post = Post()
                                // Erst neuen Post erstellen
                                post.title = title
                                // Dann die Sachen zuordnen
                                post.linkURL = linkURL
                                post.description = description
                                post.type = PostType.link
                                post.documentID = documentID
                                post.createTime = stringDate
                                post.originalPosterUID = originalPoster
                                post.votes.thanks = thanksCount
                                post.votes.wow = wowCount
                                post.votes.ha = haCount
                                post.votes.nice = niceCount
                                post.createDate = dateToSort
//                                val report = this.handyHelper.setReportType(fetchedString = reportString)
//                                if (report != null) {
//                                    post.report = report
//                                }
                                val factID = documentData[factJSONString] as? String
                                if (factID != null) {
//                                    val fact = Fact(addMoreDataCell = false)
//                                    fact.documentID = factID
//                                    post.fact = fact
                                }
                                if (originalPoster == "anonym") {
                                    post.anonym = true
                                } else {
                                    post.getUser()
                                }
                                this.posts.add(post)
                            } else // Repost


                                return


                                if (postType == "repost" || postType == "translation") {
                                    val postDocumentID =
                                        documentData["OGpostDocumentID"] as? String ?: return
                                    val post = Post()
                                    post.type = PostType.repost
                                    post.title = title
                                    post.description = description
                                    post.createTime = stringDate
                                    post.OGRepostDocumentID = postDocumentID
                                    post.documentID = documentID
                                    post.originalPosterUID = originalPoster
                                    post.votes.thanks = thanksCount
                                    post.votes.wow = wowCount
                                    post.votes.ha = haCount
                                    post.votes.nice = niceCount
                                    post.createDate = dateToSort
//                                    val report = this.handyHelper.setReportType(fetchedString = reportString)
//                                    if (report != null) {
//                                        post.report = report
//                                    }
                                    val factID = documentData[factJSONString] as? String
                                    if (factID != null) {
//                                        val fact = Fact(addMoreDataCell = false)
//                                        fact.documentID = factID
//                                        post.fact = fact
                                    }
                                    if (originalPoster == "anonym") {
                                        post.anonym = true
                                    } else {
                                        post.getUser()
                                    }
//                                    post.getRepost(returnRepost = { repost  ->
//                                        post.repost = repost
//                                    })
                                    this.posts.add(post)
                                }
            }
        }
    }

    fun Date.formatToViewDateDefaults(): String{
        val sdf= SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(this)
    }

//    fun getEvent(completion: (Post) -> Unit) {
//        var eventRef = db.collection("Events").limit(to = 1)
//        val lastEventSnap = lastEventSnap
//        if (lastEventSnap != null) {
//            // For the next loading batch of 20, there will be one event
//            eventRef = eventRef.start(afterDocument = lastEventSnap)
//        }
//        eventRef.getDocuments { eventSnap, err  ->
//            val err = err
//            if (err != null) {
//                print("Wir haben einen Error beim Event: ${err.localizedDescription}")
//            }
//            for (event in eventSnap!!.documents) {
//                val documentID = event.documentID
//                val documentData = event.data()
//                val title = documentData["title"] as? String
//                val description = documentData["description"] as? String
//                val location = documentData["location"] as? String
//                val type = documentData["type"] as? String
//                val imageURL = documentData["imageURL"] as? String
//                val imageHeight = documentData["imageHeight"] as? CGFloat
//                val imageWidth = documentData["imageWidth"] as? CGFloat
//                val participants = documentData["participants"] as? List<String>
//                val admin = documentData["admin"] as? String
//                val createDate = documentData["createDate"] as? Timestamp
//                val eventDate = documentData["time"] as? Timestamp
//                if (title == null || description == null || location == null || type == null || imageURL == null || imageHeight == null || imageWidth == null || participants == null || admin == null || createDate == null || eventDate == null) {
//                    continue
//                }
//                val eventTime = this.handyHelper.getStringDate(timestamp = eventDate)
//                val createDateString = this.handyHelper.getStringDate(timestamp = createDate)
//                val post = Post()
//                val event = Event()
//                event.title = title
//                event.description = description
//                event.location = location
//                event.type = type
//                event.imageURL = imageURL
//                event.imageWidth = imageWidth
//                event.imageHeight = imageHeight
//                event.participants = participants
//                event.createDate = createDateString
//                event.time = eventTime
//                post.originalPosterUID = admin
//                post.documentID = documentID
//                post.type = .event
//                        post.event = event
//                completion(post)
//            }
//        }
//    }

//    fun getUsers(postList: List<Post>, completion: (List<Post>) -> Unit) {
//        //Wenn die Funktion fertig ist soll returnPosts bei der anderen losgehen
//        for (post in postList) {
//            // Vorläufig Daten hinzufügen
//            //              print("postID::::::" , post.documentID)
//            //            if post.type == "repost" || post.type == "translation" {
//            //                let postRef = db.collection("Posts").document(post.documentID)
//            //                let documentData : [String:Any] = ["thanksCount": 8, "wowCount": 4, "haCount": 3, "niceCount": 2]
//            //
//            //                postRef.setData(documentData, merge: true)
//            //            }
//            // User Daten raussuchen
//            val userRef = db.collection("Users").document(post.originalPosterUID)
//            userRef.getDocument(completion = { document, err  ->
//                val document = document
//                if (document != null) {
//                    val docData = document.data()
//                    if (docData != null) {
//                        val user = User()
//                        user.name = docData["name"] as? String ?: ""
//                        user.surname = docData["surname"] as? String ?: ""
//                        user.imageURL = docData["profilePictureURL"] as? String ?: ""
//                        user.userUID = post.originalPosterUID
//                        user.blocked = docData["blocked"] as? List<String> ?: null
//                        post.user = user
//                    }
//                }
//                val err = err
//                if (err != null) {
//                    print("Wir haben einen Error beim User: ${err.localizedDescription}")
//                }
//            })
//        }
//        completion(postList)
//    }

    fun getCommentCount(completion: () -> Unit) {
        //Wenn die Funktion fertig ist soll returnPosts bei der anderen losgehen
        for (post in this.posts) {
            // Comment Count raussuchen wenn Post
            if (post.type != PostType.event) {
                // Wenn kein Event
                val commentRef =
                    db.collection("Comments").document(post.documentID).collection("threads")
                commentRef.get()
                    .addOnSuccessListener { snapshot ->
                        val snapshot = snapshot
                        if (snapshot != null) {
                            val number = snapshot.size()
                            post.commentCount = number
                        }
                    }
            }
        }
        print("Set Completed")
        completion()
    }

//    fun getChatUser(uid: String, sender: Boolean, user: (ChatUser) -> Unit) {
//        val userRef = db.collection("Users").document(uid)
//        var chatUser: ChatUser? = null
//        userRef.getDocument(completion = { document, err  ->
//            val document = document
//            if (document != null) {
//                val docData = document.data()
//                if (docData != null) {
//                    val name = docData["name"] as? String
//                    val surname = docData["surname"] as? String
//                    if (name == null || surname == null) {
//                        return@getDocument
//                    }
//                    val // Hier Avatar als UIImage einführen
//                            imageURL = docData["profilePictureURL"] as? String
//                    if (imageURL != null) {
//                        val url = URL(string = imageURL)
//                        if (url != null) {
//                            val defchatUser = ChatUser(displayName = "${name} ${surname}", avatar = null, avatarURL = url, isSender = sender)
//                            //                        let imageView = UIImageView()
//                            //                        var image = UIImage()
//                            //                        imageView.sd_setImage(with: url, completed: { (newImage, _, _, _) in
//                            //
//                            //                        })
//                            //                        if let data = try? Data(contentsOf: url) {
//                            //                            let image:UIImage = UIImage.sd_image(with: data)
//                            //                        }
//                            chatUser = defchatUser
//                        }
//                    } else {
//                        val defchatUser = ChatUser(displayName = "${name} ${surname}", avatar = null, avatarURL = null, isSender = sender)
//                        chatUser = defchatUser
//                    }
//                }
//            }
//            val err = err
//            if (err != null) {
//                print("Wir haben einen Error beim User: ${err.localizedDescription}")
//            }
//            val daChatUser = chatUser
//            if (daChatUser != null) {
//                user(daChatUser)
//            }
//        })
//    }
//}

// Return User
//class ReportOptions {
//    // Optical change
//    val opticOptionArray = listOf("Spoiler", NSLocalizedString("Opinion, not a fact", comment = "When it seems like the post is presenting a fact, but is just an opinion"), NSLocalizedString("Sensationalism", comment = "When the given facts are presented more important, than they are in reality"), "Circlejerk", NSLocalizedString("Pretentious", comment = "When the poster is just posting to sell themself"), NSLocalizedString("Edited Content", comment = "If the person shares something that is corrected or changed with photoshop or whatever"), NSLocalizedString("Ignorant Thinking", comment = "If the poster is just looking at one side of the matter or problem"), NSLocalizedString("Not listed", comment = "Something besides the given options"))
//    // Bad Intentions
//    val badIntentionArray = listOf(NSLocalizedString("Hate against...", comment = "Expressing hat against a certain type of people"), NSLocalizedString("Disrespectful", comment = "If a person thinks he shouldnt care about another person's opinion"), NSLocalizedString("Offensive", comment = "Using slurs"), NSLocalizedString("Harassment", comment = "Keep on asking for something, even if the other person is knowingly annoyed"), NSLocalizedString("Racist", comment = "Not accepting another persons heritage"), NSLocalizedString("Homophobic", comment = "Not accepting another persons gender or sexual preferences"), NSLocalizedString("Violance Supporting", comment = "support the use of violance"), NSLocalizedString("Belittlement of suicide", comment = "Tough topic, no belittlement of suicide or joking about it"), NSLocalizedString("Disrespectful against religions", comment = "Disrespectful against religions"), NSLocalizedString("Not listed", comment = "Something besides the given options"))
//    // Lie & Deception
//    val lieDeceptionArray = listOf("Fake News", NSLocalizedString("Denying of facts", comment = "Ignore proven facts and live with the lie"), NSLocalizedString("Conspiracy theory", comment = "Conspiracy theory"), NSLocalizedString("Not listed", comment = "Something besides the given options"))
//    // Content
//    val contentArray = listOf(NSLocalizedString("Pornography", comment = "You know what it means"), NSLocalizedString("Pedophilia", comment = "sexual display of minors"), NSLocalizedString("Presentation of violance", comment = "Presentation of violance"), NSLocalizedString("Not listed", comment = "Something besides the given options"))
//}

    class Event {
        var title = ""
        var description = ""
        var time = ""
        var location = ""
        var type = ""
        var imageURL = ""
        var createDate = ""
        var imageHeight = 0
        var imageWidth = 0
        var participants = listOf<String>()
        var admin = User()
    }
}
