package com.example.reminderapp.generalMenus

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.reminderapp.authMenus.LoginActivity
import com.example.reminderapp.dataClasses.Constants
import com.example.reminderapp.models.UserModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject


class InitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        splash.setKeepOnScreenCondition{true}
    }

    override fun onResume() {
        super.onResume()
        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val user: String? = mAuth.uid

        Handler(Looper.getMainLooper()).postDelayed({
            if (user == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                val userDoc: DocumentReference = db.collection("users").document(user)
                userDoc.get().addOnCompleteListener { task: Task<DocumentSnapshot> ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document.exists()) {
                            val userModel : UserModel? = document.toObject()
                            startActivity(Intent(this, MainMenuActivity::class.java).putExtra(Constants.PutExUser, userModel))
                            finish()
                        } else {
                            mAuth.signOut()
                            Log.d(ContentValues.TAG, "get failed with " + task.exception)
                            Toast.makeText(this, "Algo salió mal!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                    } else {
                        Log.d(ContentValues.TAG, "get failed with ", task.exception)
                        Toast.makeText(this, "Algo salió mal!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                }
            }
        }, 1000)
    }
}