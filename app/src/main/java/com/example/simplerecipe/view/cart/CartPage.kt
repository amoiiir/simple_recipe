package com.example.simplerecipe.view.cart

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplerecipe.R
import com.example.simplerecipe.adapter.CartAdapter
import com.example.simplerecipe.databinding.ActivityCartPageBinding
import com.example.simplerecipe.model.CartData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class CartPage : AppCompatActivity() {

    private lateinit var binding: ActivityCartPageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db : FirebaseFirestore
    //recyclerview adapter
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var cartList: ArrayList<CartData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCartPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            //initialize firestore
            firebaseAuth = FirebaseAuth.getInstance()
            db = FirebaseFirestore.getInstance()

            //initialize recyclerview
            cartRecyclerView = binding.rvCart
            cartRecyclerView.layoutManager = LinearLayoutManager(this)
            cartRecyclerView.setHasFixedSize(true)

            //initialize arraylist
            cartList = arrayListOf()

            //initialize adapter
            cartAdapter = CartAdapter(cartList)

            //set adapter
            cartRecyclerView.adapter = cartAdapter

            eventChangeListListener()
        }catch (e:Exception){
            Log.e("document_firebase", "Error getting documents ${e.message}")
        }


        initView()
        initUserCart()

    }

    private fun eventChangeListListener() {
        db.collection("users/${firebaseAuth.currentUser?.uid}/cart")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("document_firebase", "Listen failed.", error)
                        return
                    }

                    cartList.clear()
                    for (doc : DocumentChange in value?.documentChanges!!) {

                        if (doc.type == DocumentChange.Type.ADDED) {
                            val cartData = doc.document.toObject(CartData::class.java)
                            cartList.add(cartData)
                        }

                    }
                    cartAdapter.notifyDataSetChanged()
                }

            })
    }

    private fun initUserCart() {
        db.collection("users/${firebaseAuth.currentUser?.uid}/cart")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("document_firebase", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener {
                Log.e("document_firebase", "Error getting documents ${it.message}")
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