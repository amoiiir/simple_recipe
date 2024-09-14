package com.example.simplerecipe.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.simplerecipe.view.MainActivity
import com.example.simplerecipe.R
import com.example.simplerecipe.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    //check current user
    val user = firebaseAuth.currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        //login
        binding.btnLogin.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {


                    if (it.isSuccessful){

                        //check the current user
                        if(user != null){
                            Log.d("recipe_debug", "onCreate: current user ${user.email}")
                        }

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        Log.d("recipe_debug", "onCreate: success")
                    }else {
                        Toast.makeText(this, "Email is not signed up yet!", Toast.LENGTH_SHORT)
                            .show()
                        Log.d("recipe_debug", "onCreate: ${it.exception?.message}")
                    }
                }

            }else{
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                Log.d("recipe_debug", "onCreate: Please fill all the fields")

            }
        }

        //redirect user if they don't have an account
        binding.redirectSignup.setOnClickListener{
            val signup = Intent(this, SignupActivity::class.java)
            startActivity(signup)
        }

    }
}