package com.example.nullclassapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.sign

class SplashScreen : AppCompatActivity() {

    private lateinit var mauth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        mauth = FirebaseAuth.getInstance()
        val user = mauth.currentUser

        Handler(Looper.getMainLooper()).postDelayed({
            if(user != null){
                val finalIntent = Intent(this , FinalActivity::class.java)
                startActivity(finalIntent)
                finish()
            }
            else{
                val signIntent = Intent(this, MainActivity::class.java)
                startActivity(signIntent)
                finish()
            }
        },2000)
    }
}