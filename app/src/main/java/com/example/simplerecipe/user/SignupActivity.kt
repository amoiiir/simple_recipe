package com.example.simplerecipe.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.simplerecipe.R
import com.example.simplerecipe.databinding.ActivitySignupBinding
import com.example.simplerecipe.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        singupUser()
        redirectLogin()

    }

    private fun redirectLogin() {
        //redirect user if they all ready have an account
        binding.redirectLogin.setOnClickListener{
            val login = Intent(this, LoginActivity::class.java)
            startActivity(login)
        }
    }

    private fun singupUser() {
        //click submit
        binding.btnSignup.setOnClickListener {
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()
            val username = binding.username.text.toString()

            Log.d("recipe_debug", "btnSignup: $email")
            //check not empty
            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                //check password match
                if (password == confirmPassword) {
                    //create user
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            //save details
                            saveUserData(firebaseAuth.currentUser?.uid.toString(), email, username)
                            Log.d("recipe_debug", "onCreate: username $username")

                            //redurect to login
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Error: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                            Log.d("recipe_debug", "onCreate: ${it.exception?.message}")
                        }
                    }
                } else {
                    Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserData(userId: String, email: String, username: String) {
        val userData = UserData(userId, email, username)
        val userRef = db.collection("users").document("userDetails")

        userRef.set(userData)
            .addOnCompleteListener {
                //handle success
                Log.d("recipe_debug", "saveUserData: Success signup")
                Toast.makeText(this, "Success Signup!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                //handle failure
                Log.e("simple_recipe", "saveUserData: Error signup")
                Toast.makeText(this, "Error Signup!", Toast.LENGTH_SHORT).show()
            }
    }
}