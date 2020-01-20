package com.justimagine.imagine.ui.dashboard

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.justimagine.imagine.PostType
import com.justimagine.imagine.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_dashboard.*
import java.io.ByteArrayOutputStream
import java.util.*


class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel

    val storageRef = Firebase.storage.reference
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    val duration = Toast.LENGTH_SHORT

    var type:PostType = PostType.thought

    var image_inBitmap: Bitmap? = null
    var imageHeigth:Float = 0.toFloat()
    var imageWidth:Float = 0.toFloat()

    val alphaValue:Float = 0.5.toFloat()
    val fullAlpha: Float = 1.toFloat()
    private val GALLERY = 1
    private val CAMERA = 2

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        dashboardViewModel.text.observe(this, Observer {

        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postType_radioGroup.setOnCheckedChangeListener{group, checkedId ->

            when (checkedId) {
                R.id.thought_radioButton -> setThought()
                R.id.picture_radioButton -> showPicture()
                R.id.link_radioButton -> showLink()
                R.id.youTubeVideo_radioButton -> showYouTube()
                else -> setThought()
            }
        }

        //For now because it doesnt work
        pictureCamera_button.isEnabled = false
        pictureCamera_button.alpha = alphaValue
        pictureCamera_button.setOnClickListener {
            takePhotoFromCamera()
        }

        pictureFolder_Button.setOnClickListener {
            choosePhotoFromGallery()
        }

        share_button.setOnClickListener {
            shareTapped()
        }

    }

//    private fun showPictureDialog() {
//
//        val pictureDialog = AlertDialog.Builder(context)
//        pictureDialog.setTitle("Select Action")
//        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
//        pictureDialog.setItems(pictureDialogItems
//        ) { dialog, which ->
//            when (which) {
//                0 ->
//                1 ->
//            }
//        }
//        pictureDialog.show()
//    }

    fun choosePhotoFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        /* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*/
        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data.data

                try {
                    if (Build.VERSION.SDK_INT < 28) {
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            context?.contentResolver,
                            contentURI
                        )
                        image_inBitmap = bitmap
                        imageWidth = bitmap.width.toFloat()
                        imageHeigth = bitmap.height.toFloat()
                        preview_imageView.setImageBitmap(bitmap)
                    } else {
                        val source =
                            ImageDecoder.createSource(context!!.contentResolver, contentURI!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        preview_imageView.setImageBitmap(bitmap)
                        image_inBitmap = bitmap
                        imageWidth = bitmap.width.toFloat()
                        imageHeigth = bitmap.height.toFloat()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

//                try {
//                    val bitmap =
//                        MediaStore.Images.Media.getBitmap(context?.contentResolver, contentURI)
//                    imageHeigth = bitmap.height.toFloat()
//                    imageWidth = bitmap.width.toFloat()
////                    val path = saveImage(bitmap)
////                    Toast.makeText(context, "Image Saved!", Toast.LENGTH_SHORT).show()
//                    Glide.with(this).load(bitmap).into(preview_imageView)
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                    Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show()
//                }

            }

        }
        else if (requestCode == CAMERA) {
//Doesnt work for the camera
            if (data != null) {
                val contentURI = data.data

                try {
                    if (Build.VERSION.SDK_INT < 28) {
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            context?.contentResolver,
                            contentURI
                        )
                        image_inBitmap = bitmap
                        imageWidth = bitmap.width.toFloat()
                        imageHeigth = bitmap.height.toFloat()
                        preview_imageView.setImageBitmap(bitmap)
                    } else {
                        val source =
                            ImageDecoder.createSource(context!!.contentResolver, contentURI!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        preview_imageView.setImageBitmap(bitmap)
                        image_inBitmap = bitmap
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            //This worked but got bad quality
//            val thumbnail = data!!.extras!!.get("data") as Bitmap
//            imageHeigth = thumbnail.height.toFloat()
//            imageWidth = thumbnail.width.toFloat()
//            Glide.with(this).load(thumbnail).into(preview_imageView)
//            saveImage(thumbnail)
//            Toast.makeText(context, "Image Saved!", Toast.LENGTH_SHORT).show()
        }
    }

//    fun saveImage(myBitmap: Bitmap):String {
//        val bytes = ByteArrayOutputStream()
//        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
//        val wallpaperDirectory = File(
//            (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
//        // have the object build the directory structure, if needed.
//        Log.d("fee",wallpaperDirectory.toString())
//        if (!wallpaperDirectory.exists())
//        {
//
//            wallpaperDirectory.mkdirs()
//        }
//
//        try
//        {
//            Log.d("heel",wallpaperDirectory.toString())
//            val f = File(wallpaperDirectory, ((Calendar.getInstance()
//                .getTimeInMillis()).toString() + ".jpg"))
//            f.createNewFile()
//            val fo = FileOutputStream(f)
//            fo.write(bytes.toByteArray())
//            MediaScannerConnection.scanFile(context,
//                arrayOf(f.getPath()),
//                arrayOf("image/jpeg"), null)
//            fo.close()
//            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath())
//
//            return f.getAbsolutePath()
//        }
//        catch (e1: IOException) {
//            e1.printStackTrace()
//        }
//
//        return ""
//    }

    companion object {
        private val IMAGE_DIRECTORY = "/imagine"
    }


    private fun shareTapped() {

        val user = auth.currentUser

        if (user != null) {

            if (title_editText.text.isNullOrEmpty()) {
                Toast.makeText(context, "Gib einen Titel ein!", duration).show()
            } else {

                var postRef = db.collection("Posts").document()
                println("Das ist die post reference: ${postRef.id}")

                val zero = 0.toFloat()
                when (type) {
                    PostType.picture ->
                        if (preview_imageView.drawable == null || imageHeigth == zero || imageWidth == zero) {
                            Toast.makeText(context, "Bitte lade ein Bild hoch", duration)
                                .show()
                        } else {
                            loadPictureToFirebase(postRef)
                        }
                    PostType.thought -> postThought(postRef)
                    PostType.link ->
                        if (link_editText.text.isNullOrEmpty()) {
                            Toast.makeText(context, "Gib bitte einen Link ein", duration).show()
                        } else {
                            postLink(postRef)
                        }
                    PostType.youTubeVideo ->
                        if (link_editText.text.isNullOrEmpty()) {
                            Toast.makeText(context, "Gib bitte einen Link ein", duration).show()
                        } else {
                            postyouTubeVideo(postRef)
                        }
                }
            }
        } else {
            Toast.makeText(context, "Log dich ein um zu posten", duration).show()
        }

    }

    fun loadPictureToFirebase(postRef: DocumentReference) {

        var pictureRef = storageRef.child("postPictures").child("${postRef.id}.png")

//        val bitmap = (preview_imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        image_inBitmap!!.compress(Bitmap.CompressFormat.JPEG, 10, baos)
        val data = baos.toByteArray()

        var uploadTask = pictureRef.putBytes(data)
        uploadTask.addOnFailureListener {
            println("Couldnt upload the image")
        }.addOnSuccessListener {
            println("Picture upload was successfull")
            // Go on with post
            val imageURL = pictureRef.downloadUrl

            println("(Hier die imageURL? : ${imageURL})")
            imageURL.addOnSuccessListener {uri ->
                val url = uri.toString()
                println("Hier die URL? : ${url}")

                postPicture(postRef,url)
            }
        }
    }

    fun postPicture(postRef: DocumentReference, imageURL:String) {
        println("Das ist die URL: ${imageURL}")

        var user = auth.currentUser

        if (user != null) {

            val data = hashMapOf(
                "title" to title_editText.text.toString(),
                "description" to description_editText.text.toString(),
                "originalPoster" to user.uid,
                "createTime" to Timestamp(Date()),
                "thanksCount" to 0,
                "wowCount" to 0,
                "haCount" to 0,
                "niceCount" to 0,
                "type" to "picture",
                "report" to "normal",
                "imageURL" to imageURL,
                "imageHeight" to imageHeigth,
                "imageWidth" to imageWidth
            )

            uploadData(postRef,data)
        }
    }

    fun postThought(postRef: DocumentReference) {

        var user = auth.currentUser

        if (user != null) {

            val data = hashMapOf(
                "title" to title_editText.text.toString(),
                "description" to description_editText.text.toString(),
                "originalPoster" to user.uid,
                "createTime" to Timestamp(Date()),
                "thanksCount" to 0,
                "wowCount" to 0,
                "haCount" to 0,
                "niceCount" to 0,
                "type" to "thought",
                "report" to "normal"
            )

            uploadData(postRef,data)
        }
    }

    fun postLink(postRef: DocumentReference) {

        var user = auth.currentUser

        if (user != null) {

            val data = hashMapOf(
                "title" to title_editText.text.toString(),
                "description" to description_editText.text.toString(),
                "originalPoster" to user.uid,
                "createTime" to Timestamp(Date()),
                "thanksCount" to 0,
                "wowCount" to 0,
                "haCount" to 0,
                "niceCount" to 0,
                "type" to "link",
                "report" to "normal",
                "link" to link_editText.text.toString()
            )

            uploadData(postRef,data)
        }
    }

    fun postyouTubeVideo(postRef: DocumentReference) {

        var user = auth.currentUser

        if (user != null) {

            val data = hashMapOf(
                "title" to title_editText.text.toString(),
                "description" to description_editText.text.toString(),
                "originalPoster" to user.uid,
                "createTime" to Timestamp(Date()),
                "thanksCount" to 0,
                "wowCount" to 0,
                "haCount" to 0,
                "niceCount" to 0,
                "type" to "youTubeVideo",
                "report" to "normal",
                "link" to link_editText.text.toString()
            )

            uploadData(postRef,data)
        }
    }

    fun uploadData(postRef: DocumentReference, data: kotlin.collections.HashMap<String, Comparable<*>>) {
        postRef.set(data)
            .addOnSuccessListener {
                println("Post creation successfull")
                postedSuccessful()
            }
            .addOnFailureListener { error ->
                println("Post creation failed: ${error.localizedMessage}")
            }
    }

    fun postedSuccessful() {
        Toast.makeText(context, "Dein Post wurde erfolgreich erstellt", duration)
            .show()
        title_editText.text.clear()
        description_editText.text.clear()
        link_editText.text.clear()
        preview_imageView.setImageResource(0)
    }

    //UILayout

    fun setThought() {
        hidePicture()
        hideLink()

        type = PostType.thought
    }

    fun hidePicture() {
        pictureCamera_button.alpha = alphaValue
        pictureFolder_Button.alpha = alphaValue
        pictureFolder_Button.isEnabled = false
        pictureCamera_button.isEnabled = false
        picture_label.alpha = alphaValue
//        picture_imageView.alpha = alphaValue
    }

    fun showPicture() {
        type = PostType.picture

        hideLink()

//        pictureCamera_button.alpha = fullAlpha
//        pictureCamera_button.isEnabled = true

        pictureFolder_Button.alpha = fullAlpha
        pictureFolder_Button.isEnabled = true
        picture_label.alpha = fullAlpha
    }

    fun hideLink() {
        link_label.alpha = alphaValue
        link_editText.isEnabled = false
    }

    fun showLink() {

        type = PostType.link

        hidePicture()

        link_editText.isEnabled = true
        link_label.alpha = fullAlpha
    }

    fun showYouTube() {
        type = PostType.youTubeVideo

        hidePicture()

        link_editText.isEnabled = true
        link_label.alpha = fullAlpha
    }

}