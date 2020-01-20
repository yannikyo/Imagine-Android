package com.justimagine.imagine

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.justimagine.imagine.ui.LogInActivity
import com.justimagine.imagine.ui.UserViewActivity

class MainActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {    // ViewDidLoad
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // Set Layout

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

//        actionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        supportActionBar?.setTitle("Imagine")
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setTitle("Imagine")
        supportActionBar?.setIcon(R.drawable.hippy_sign_low)
        supportActionBar?.setTitle("Imagine")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_menu, menu)
        var userItem = menu!!.findItem(R.id.user_button)

//        val user = auth.currentUser
//
//        if (user != null) {
//            val uri = user.photoUrl
//
//            if (uri != null) {
//
//                println("Das ist URI: ${uri}")
//                if (Build.VERSION.SDK_INT < 28) {
//                    println("Das ist URI unter 28")
//                    Bitmap
////                    val bitmap = MediaStore.Images.Media.getBitmap(
////                        this.contentResolver,
////                        uri
////                    )
//                    println("Das ist URI am ende")
//                    val draw = BitmapDrawable(bitmap)
//                    userItem.setIcon(draw)
//                } else {
//                    println("Das ist URI später")
//
//                    val source =
//                        ImageDecoder.createSource(this.contentResolver, uri)
//                    val bitmap = ImageDecoder.decodeBitmap(source)
//
//                    println("Das ist bitmap : ${bitmap}")
//                    userItem.setIcon(BitmapDrawable(getResources(), bitmap))
//                }
//            }
//        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.user_button) { // do something here

            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val intent = Intent(this, UserViewActivity::class.java)

                this.startActivity(intent)
            } else {
                val intent = Intent(this, LogInActivity::class.java)

//            intent.putExtra(CustomViewHolder.post_string, jsonObject)
                this.startActivity(intent)
            }


        }
        return super.onOptionsItemSelected(item)
    }

}
