package com.justimagine.imagine.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.justimagine.imagine.R
import kotlinx.android.synthetic.main.user_activity.*
import java.io.ByteArrayOutputStream

class UserViewActivity: AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    val storage = Firebase.storage.reference
    val db = FirebaseFirestore.getInstance()

    private val GALLERY = 1
    var image_inBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_activity)

        println("UserViewActivity")


        logOut_button.setOnClickListener {
            logOut()
        }

        changeProfilePicture_button.setOnClickListener {
            choosePhotoFromGallery()
        }

        setUser()
    }

    fun setUser() {
        val user = auth.currentUser

        if (user != null) {
            name_textView.text = user.displayName
            if (user.photoUrl != null) {
                Glide.with(this).load(user.photoUrl).into(profile_imageView)
            }
        }
    }

    fun choosePhotoFromGallery() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        /* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*/

        val user = auth.currentUser

        if (user != null) {
            if (user.photoUrl != null) {
                deletePhoto(user)
            }
        }

        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data.data

                try {
                    if (Build.VERSION.SDK_INT < 28) {
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            this.contentResolver,
                            contentURI
                        )
                        image_inBitmap = bitmap
                        profile_imageView.setImageBitmap(bitmap)
                        setPhoto()
                    } else {
                        val source =
                            ImageDecoder.createSource(this.contentResolver, contentURI!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        profile_imageView.setImageBitmap(bitmap)
                        image_inBitmap = bitmap
                        setPhoto()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun deletePhoto(user: FirebaseUser) {
        var imageName = "${user.uid}.profilePicture"

        var storageRef = storage.child("profilePictures").child("${imageName}.png")

        storageRef.delete()
            .addOnSuccessListener {
            println("Image Deletion Successful")
        }.addOnFailureListener {
                println("Image Deletion Unsuccessful")
        }
    }

    fun setPhoto() {
        val user = auth.currentUser

        if (user != null) {

            var imageName = "${user.uid}.profilePicture"

            var storageRef = storage.child("profilePictures").child("${imageName}.png")

//        val bitmap = (preview_imageView.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            image_inBitmap!!.compress(Bitmap.CompressFormat.JPEG, 20, baos)
            val data = baos.toByteArray()

            var uploadTask = storageRef.putBytes(data)
            uploadTask.addOnFailureListener {
                println("Couldnt upload the image")
            }.addOnSuccessListener {
                println("Picture upload was successfull")
                // Go on with post
                val imageURL = storageRef.downloadUrl

                println("(Hier die imageURL? : ${imageURL})")
                imageURL.addOnSuccessListener {uri ->
                    val url = uri.toString()
                    println("Hier die URL? : ${url}")

                    changeDatabase(url)
                    changeUsersAuthData(user, uri)
                }
            }
        }
    }

    fun changeDatabase(url:String) {
        val user = auth.currentUser
        if (user != null) {
            val ref = db.collection("Users").document(user.uid)


            ref.update("profilePictureURL", url)
                .addOnSuccessListener { println("DocumentSnapshot successfully updated!") }
                .addOnFailureListener { println("Error updating document") }
        }
    }

    fun changeUsersAuthData(user: FirebaseUser, uri: Uri) {

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setPhotoUri(uri)
            .build()


        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("ChangeRequest successful")
                }
            }
    }

    fun logOut() {
        auth.signOut()

        Toast.makeText(this, "Erfolgreich ausgeloggt", Toast.LENGTH_SHORT).show()

        super.onBackPressed()
    }
//to do log out und bild einstellen
}