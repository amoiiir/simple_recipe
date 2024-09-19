package com.example.simplerecipe.view.cart

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.simplerecipe.R
import com.example.simplerecipe.databinding.ActivityCartPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CartPage : AppCompatActivity() {

    private lateinit var binding: ActivityCartPageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCartPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialize firestore
        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        initView()
        initUserCart()

    }

    private fun initUserCart() {
//        checkCurrentUser()
        val docRef = db.collection("cart").document("5")
        docRef.get()
            .addOnFailureListener { document ->
                if (document != null){
                        Log.d("document_firebase", "initUserCart: ${document.message}")
                }else{
                    Log.e("document_firebase", "initUserCart: ${document.message}")
                }
            }
            .addOnFailureListener {
                Log.e("document_firebase", "initUserCart: ${it.message}")
            }
    }

    private fun checkCurrentUser() {
    }

    private fun initView() {

        binding.btnBack.setOnClickListener {
            finish()
        }


    }
}