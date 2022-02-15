package com.example.nullclassapp

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_forgot_pswrd.*
import kotlinx.android.synthetic.main.dialog_forgot_pswrd.view.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val RC_SIGN_IN = 120
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSigninClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //firebase instance initialisation
        auth = FirebaseAuth.getInstance()
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSigninClient = GoogleSignIn.getClient(this, gso)

        //on clicking Log in button
        Logbtn.setOnClickListener {
            //storing values
            val userName: String = username.text?.trim().toString()
            val password: String = password.text?.trim().toString()
            if (userName != null || password != null) {
                Toast.makeText(this, " Enter Credentials", Toast.LENGTH_SHORT).show()
            } else {
                signinUser(userName, password)
            }
        }
        //when create account is clicked
        createAccount.setOnClickListener { createAccount() }
        //when forgot password is clicked
        forgotPassword.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Forgot Password")
            val view = layoutInflater.inflate(R.layout.dialog_forgot_pswrd, null)
            val editText = view.findViewById<EditText>(R.id.userId)
            builder.setView(view)
            builder.setPositiveButton("Reset", DialogInterface.OnClickListener { _, i ->
                forgotPassword(editText)
            })
            builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { _, i ->
            })
            builder.show()
        }
        //on clicking google icon button
        google.setOnClickListener { signIn() }
    }

    private fun forgotPassword(editText: EditText) {
        if (editText.text.toString().isEmpty()) {
            Toast.makeText(this, "Enter an Email Id ", Toast.LENGTH_SHORT).show()
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(editText.text.toString()).matches()) {
            Toast.makeText(this, "Enter a Valid Email Id ", Toast.LENGTH_SHORT).show()
        }
       else{ auth.sendPasswordResetEmail(editText.text.trim().toString()).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Email sent", Toast.LENGTH_SHORT).show()
            }
        }}
    }

    private fun signIn() {
        val signInIntent = googleSigninClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.e("msg", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.e("msg", "Google sign in failed", e)
                }
            } else {
                Log.e("msg", exception.toString())
            }

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("msg", "signInWithCredential:success")
                    val intent = Intent(this, FinalActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("msg", "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun createAccount() {
        Logbtn.setText(R.string.register)
        forgotPassword.isInvisible=true
        createAccount.isInvisible = true
        //action on clicking register button
        Logbtn.setOnClickListener {
            //storing values
            val userName: String = username.text!!.trim().toString()
            val password: String = password.text!!.trim().toString()

            if (userName.isNotEmpty() && password.isNotEmpty()) {
                createUser(userName, password)
            } else {
                Toast.makeText(this, "Input Required!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun signinUser(userName: String, password: String) {
        auth.signInWithEmailAndPassword(userName, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.e("msg", "SignInwithemail:successfull")
                val intent = Intent(this, FinalActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Log.e("msg", "Error Occurred ", task.exception)
                Toast.makeText(this, "" + task.exception, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun createUser(user: String, password: String) {
        auth.createUserWithEmailAndPassword(user, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.e("msg", " Successfull")
                val intent = Intent(this, FinalActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "" + task.exception, Toast.LENGTH_LONG).show()
            }
        }
    }
}