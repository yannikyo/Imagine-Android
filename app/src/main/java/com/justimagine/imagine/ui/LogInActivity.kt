package com.justimagine.imagine.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.justimagine.imagine.R
import kotlinx.android.synthetic.main.login_activity.*
import java.util.*

class LogInActivity: AppCompatActivity() {

    val halfAlpha = 0.5.toFloat()
    val fullAlpha = 1.toFloat()

    val db = FirebaseFirestore.getInstance()
    var auth = FirebaseAuth.getInstance()

    var login = true

    var duration = Toast.LENGTH_SHORT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        setLogIn()

        logIn_radioButton.setOnClickListener {
            setLogIn()
        }

        signUp_radioButton.setOnClickListener {
            setSignUp()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.login_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.login_button) {
            if (login) {
                println("try to log in")
              tryToLogIn()
            } else {
                tryToSignUp()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    fun tryToLogIn() {
        if (password_editText.text.isNullOrEmpty() || email_editText.text.isNullOrEmpty()) {
            Toast.makeText(this,"Fülle die Felder aus", duration).show()
        } else {
            startLogIn()
        }
    }

    fun startLogIn() {
        var email = email_editText.text.toString()
        var password = password_editText.text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    println("signInWithEmail:success")
                    val user = auth.currentUser
                    dismissAfterSuccess()
                } else {
                    // If sign in fails, display a message to the user.
                    println("signInWithEmail:failure: ${task.exception}")
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }


    fun tryToSignUp() {

        if (name_editText.text.isNullOrEmpty() || surname_editText.text.isNullOrEmpty() || password_editText.text.isNullOrEmpty() || repeatPassword_editText.text.isNullOrEmpty() || email_editText.text.isNullOrEmpty() ) {
            val toast = Toast.makeText(this, "Bitte fülle alle Informationen aus", duration)
            toast.show()

        } else {
            if (password_editText.text.toString() != repeatPassword_editText.text.toString()) {
                val toast = Toast.makeText(this, "Die Passwörter stimmen nicht überein", duration)
                toast.show()
            } else {

                // Initialize a new instance of

                val builder = AlertDialog.Builder(this)

                // Set the alert dialog title
                builder.setTitle("Datenschutzerklärung")

                // Display a message on alert dialog
                builder.setMessage("Mit der Registrierung stimmst du unseren Datenschutzbestimmungen zu. Ist das okay für dich?")

                // Set a positive button and its click listener on alert dialog
                builder.setPositiveButton("Ja"){dialog, which ->
                    // Do something when user press the positive butto
                    startSignUp()
                }


                // Display a negative button on alert dialog
                builder.setNegativeButton("Nein"){dialog,which ->
                    Toast.makeText(applicationContext,"Alright then...",Toast.LENGTH_SHORT).show()
                }


                // Display a neutral button on alert dialog
                builder.setNeutralButton("Datenschutzerklärung"){_,_ ->
                    val openURL = Intent(Intent.ACTION_VIEW)
                    openURL.data = Uri.parse("https://donmalte.github.io")
                    startActivity(openURL)
                }

                // Finally, make the alert dialog using builder
                val dialog: AlertDialog = builder.create()

                // Display the alert dialog on app interface
                dialog.show()


            }
        }
    }

    fun startSignUp() {

        auth.createUserWithEmailAndPassword(email_editText.text.toString(), password_editText.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        loadUserToDatabase(user)
                        changeUsersAuthData(user)
                    }
                } else {
                    Toast.makeText(baseContext, "SignUp hat leider nicht funktioniert",
                        duration).show()
                }
            }
    }

    fun changeUsersAuthData(user: FirebaseUser) {

        val name = name_editText.text.toString()
        val surname = surname_editText.text.toString()

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName("${name} ${surname}")  //.setPhotoUri() for profilepicture
            .build()


        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("ChangeRequest successful")
                }
            }
    }

    fun loadUserToDatabase(user: FirebaseUser) {

        uploadToMaltesDatabase()

        var userRef = db.collection("Users").document(user.uid)

        val name = name_editText.text.toString()
        val surname = surname_editText.text.toString()

        val data = hashMapOf(
            "name" to name,
            "surname" to surname,
            "full_name" to "${name} ${surname}",
            "createDate" to Timestamp(Date())
        )

        userRef.set(data)
            .addOnSuccessListener {
                println("User creation successfull")
                dismissAfterSuccess()
            }
            .addOnFailureListener{error ->
                println("User creation failed: ${error.localizedMessage}")}
    }

    fun uploadToMaltesDatabase() {
        var userRef = db.collection("Users").document("CZOcL3VIwMemWwEfutKXGAfdlLy1").collection("notifications").document()
        val name = name_editText.text.toString()

        val data = hashMapOf(
            "type" to "message",
            "message" to "Neuer Android User: ${name}",
            "chatID" to "egal",
            "sentAt" to Timestamp(Date()),
            "messageID" to "auch egal"
        )

        userRef.set(data)
            .addOnSuccessListener {
                println("Malte berichten successfull")
                dismissAfterSuccess()
            }
            .addOnFailureListener{error ->
                println("Malte benachrichtigen failed: ${error.localizedMessage}")}
    }

    fun dismissAfterSuccess() {
        Toast.makeText(baseContext, "Willkommen bei Imagine!",
            duration).show()
        super.onBackPressed()
    }

    //UI
    fun setLogIn() {
        signUp_radioButton.alpha = halfAlpha
        logIn_radioButton.alpha = fullAlpha

        login = true

        name_editText.isInvisible = true
        name_label.isInvisible = true
        surname_editText.isInvisible = true
        surname_label.isInvisible = true
        repeatPassword_editText.isInvisible = true
        repeatPassword_label.isInvisible = true
    }

    fun setSignUp() {
        signUp_radioButton.alpha = fullAlpha
        logIn_radioButton.alpha = halfAlpha

        login = false

        name_editText.isInvisible = false
        name_label.isInvisible = false
        surname_editText.isInvisible = false
        surname_label.isInvisible = false
        repeatPassword_editText.isInvisible = false
        repeatPassword_label.isInvisible = false
    }
}