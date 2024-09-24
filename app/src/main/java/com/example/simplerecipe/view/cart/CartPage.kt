package com.example.simplerecipe.view.cart

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplerecipe.adapter.CartAdapter
import com.example.simplerecipe.databinding.ActivityCartPageBinding
import com.example.simplerecipe.model.CartData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlin.math.log


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
//        initCheckout()
    }

    private fun initDeleteItem() {
        //get current item
        val position = cartList.size
        Log.d("cart_data", "initDeleteItem: $position")

        binding.deleteItem.setOnClickListener {data ->
            db.collection("users/${firebaseAuth.currentUser?.uid}/cart")
                .document()
                .delete()
                .addOnSuccessListener {
                    Log.d("cart_data", " deleted  data: ${cartList[0].id}")
                    Log.d("cart_data", "DocumentSnapshot successfully deleted! ")
                }
                .addOnFailureListener { e ->
                    Log.w("cart_data", "Error deleting document", e)
                }
        }
    }

    private fun initCheckout() {
        binding.btnCheckout.setOnClickListener {
            //navigate to checkout page
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
                }else {
                    Log.d("cart_data", "price is empty: $totalAmount")
                }
            }
        }
        Log.d("cart_data", "total amount : $totalAmount")

        //bind
        binding.tvTotal.text = "MYR " + "%.2f".format(totalAmount)
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

                        //hide contents
                        binding.tvEmptyCart.visibility = View.GONE
                        binding.totalPriceFrame.visibility = View.VISIBLE

                        //
                        if (doc.type == DocumentChange.Type.ADDED) {
                            val cartData = doc.document.toObject(CartData::class.java)
                            cartList.add(cartData)
                        }

                    }
                    cartAdapter.notifyDataSetChanged()
                    initDeleteItem()
                    initTotalPrice()
                }

            })
    }

    private fun initView() {

        binding.btnBack.setOnClickListener {
            finish()
        }

    }
}