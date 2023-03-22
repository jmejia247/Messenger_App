package com.example.harmonic

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.harmonic.login.LoginActivity
import com.example.harmonic.messages.HomeActivity
import com.example.harmonic.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.UUID

// data layer that handles logic for main activity/view
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//      event listener for register button on the registration page
        register_button.setOnClickListener {
            registerUser()
        }

//        event listener for switching to the account login page
        signUp_edittext_register.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        img_selector_button.setOnClickListener {
            Log.d("Main", "functionality for img selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhoto: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("Main", "photo was selected")

            selectedPhoto = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhoto)

            profile_img_view.setImageBitmap(bitmap)
            img_selector_button.alpha = 0f


//            val bitmapDrawable = BitmapDrawable(this.resources, bitmap)
//            Log.d("Main", "$uri")
//
//
//            img_selector_button.background = bitmapDrawable

//            img_selector_button.setImageDrawable(bitmapDrawable)
//            img_selector_button.setImageURI(uri)
//            img_selector_button.background = null
        }
    }

    private fun registerUser() {
        val email = email_register.text.toString()
        val password = password_register.text.toString()
        val auth = Firebase.auth

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please Enter an Email and/or Password", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("Main", "Email: $email, Password: $password")

//            Create user with firebase
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("Main", "User successfully Created")
                    saveImageToFirebase()
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    return@addOnCompleteListener
                }
            }.addOnFailureListener {
                Log.d("Main", "The user was not created: ${it.message}")
                Toast.makeText(this, "The user was not created: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun saveImageToFirebase() {
        if (selectedPhoto == null) return

        val filename = UUID.randomUUID().toString()
        val storage = FirebaseStorage.getInstance().getReference("/images/$filename")

        storage.putFile(selectedPhoto!!)
            .addOnSuccessListener {
                Log.d("Main", "successfully uploaded img to storage with a path: ${it.metadata?.path}")

                storage.downloadUrl.addOnSuccessListener {
                    saveUserToFirebase(it.toString())
                }
            }.addOnFailureListener {
                Toast.makeText(this, "The img could not be saved: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun saveUserToFirebase(img_url: String) {
        Log.d("Main", "Saving user to db")
        val uid = FirebaseAuth.getInstance().uid
        val db = FirebaseDatabase.getInstance().getReference("/users/$uid")

        if (uid == null) return

        val user = User(uid, email_register.text.toString(), img_url)
        db.setValue(user)
            .addOnCompleteListener {
                Log.d("Main", "The user was successfully saved to the db")
            }.addOnFailureListener {
                Log.d("Main", "The user was not saved to the db: ${it.message}")
                Toast.makeText(this, "The user could not be saved to the database, Please retry registration", Toast.LENGTH_SHORT).show()
            }
    }

}




















