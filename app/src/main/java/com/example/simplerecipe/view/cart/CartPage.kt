package com.example.simplerecipe.view.cart

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
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
    private var totalAmount: Double? = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCartPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initUserCart()
        initCheckout()
//        initTotalPrice()
    }

    private fun initCheckout() {
        binding.btnCheckout.setOnClickListener {
            //navigate to checkout page
            if (totalAmount != null) {
                for (i in cartList) {
                    totalAmount = totalAmount!! + i.price!! * i.amount!!
                }
            }
            Log.d("cart_data", "total amount: $totalAmount")
            binding.tvTotal.text = "MYR $totalAmount"
        }

    }

    private fun initUserCart() {
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
    }

    @SuppressLint("SetTextI18n")
    private fun initTotalPrice() {
        //navigate to checkout page
        if (totalAmount != null) {
            for (i in cartList) {
                if (i.price != null && i.amount != null) {
                    totalAmount = totalAmount!! + i.price * i.amount!!
                    Log.d("cart_data", "total amount condition: $totalAmount")
                }else {
                    Log.d("cart_data", "price is empty: $totalAmount")
                }
            }
        }
        Log.d("cart_data", "total amount initTotalPrice: $totalAmount")

        //bind
        binding.tvTotal.text = "MYR $totalAmount"
    }

    private fun eventChangeListListener() {
        db.collection("users/${firebaseAuth.currentUser?.uid}/cart")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("document_firebase", "Listen failed.", error)
                        return
                    }

                    cartList.clear()
                    for (doc : DocumentChange in value?.documentChanges!!) {
                        binding.tvEmptyCart.visibility = View.GONE
                        if (doc.type == DocumentChange.Type.ADDED) {
                            val cartData = doc.document.toObject(CartData::class.java)
                            cartList.add(cartData)
                        }

                    }
                    cartAdapter.notifyDataSetChanged()
                }

            })
    }

    private fun initView() {

        binding.btnBack.setOnClickListener {
            finish()
        }

    }
}