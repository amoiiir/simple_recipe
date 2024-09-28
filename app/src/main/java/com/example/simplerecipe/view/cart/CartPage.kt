package com.example.simplerecipe.view.cart

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplerecipe.R
import com.example.simplerecipe.adapter.CartAdapter
import com.example.simplerecipe.databinding.ActivityCartPageBinding
import com.example.simplerecipe.model.CartData
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlin.math.abs
import kotlin.math.log
import kotlin.math.roundToInt


class CartPage : AppCompatActivity() {

    private lateinit var binding: ActivityCartPageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db : FirebaseFirestore
    //recyclerview adapter
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var cartList: ArrayList<CartData>
    private var totalAmount: Double? = 0.0
    private lateinit var swipeLeft : ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCartPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        //for swiping purposes
        val displayMetrics: DisplayMetrics = resources.displayMetrics
        val width = (displayMetrics.widthPixels / displayMetrics.density).toInt().dp
        val deleteIcon = resources.getDrawable(R.drawable.ic_delete, null)

        initView()
        initUserCart(width, deleteIcon)
//        initDeleteItem()
//        initCheckout()
    }

    private fun initCheckout() {
        binding.btnCheckout.setOnClickListener {
            //navigate to checkout page
        }

    }

    private fun initUserCart(width: Int, deleteIcon: Drawable) {
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

        //swipe left function
        swipeLeft = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.absoluteAdapterPosition
                val cartItem = cartList[pos]
                val cartId = cartItem.id.toString()

                cartList.removeAt(pos)
                cartAdapter.notifyItemRemoved(pos)

                //delete item from firestore
                db.collection("users/${firebaseAuth.currentUser?.uid}/cart")
                    .document(cartId)
                    .delete()
                    .addOnSuccessListener {
                        Log.d("cart_data", "DocumentSnapshot successfully deleted! ")
                    }
                    .addOnFailureListener { e ->
                        Log.w("cart_data", "Error deleting document", e)
                    }

                Snackbar.make(
                    findViewById(R.id.cart_main),
                    "Item Deleted",
                    Snackbar.LENGTH_SHORT
                ).show()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                val itemView = viewHolder.itemView
                val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2
                val cornerRadius = 10f
                val paint = Paint().apply {
                    color = resources.getColor(R.color.red, null)
                    isAntiAlias = true
                }

                if (dX < 0) {

                    val background = RectF(
                        itemView.right.toFloat() + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat()
                    )
                    c.drawRoundRect(background, cornerRadius, cornerRadius, paint)

                    deleteIcon.bounds = Rect(
                        itemView.right - iconMargin - deleteIcon.intrinsicWidth,
                        itemView.top + iconMargin,
                        itemView.right - iconMargin,
                        itemView.bottom - iconMargin
                    )
                    deleteIcon.draw(c)
                }

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        })

        eventChangeListListener()
        swipeLeft.attachToRecyclerView(cartRecyclerView)

        //this function is for clicking the cards
        cartAdapter.setOnItemClickListener(object : CartAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val currentItem = cartList[position]
                Toast.makeText(this@CartPage, "Item clicked: ${currentItem.id}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun initTotalPrice() {

        totalAmount = 0.0

        //navigate to checkout page
        for (i in cartList) {
            if (i.price != null && i.amount != null) {
                totalAmount = totalAmount!! + i.price * i.amount!!
            }else {
                Log.d("cart_data", "price is empty: $totalAmount")
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

//                    cartList.clear()
                    for (doc : DocumentChange in value?.documentChanges!!) {

                        if (cartList.isEmpty()){
                            binding.tvEmptyCart.visibility = View.VISIBLE
                            binding.totalPriceFrame.visibility = View.GONE
                        }

                        if (doc.type == DocumentChange.Type.ADDED) {
                            binding.tvEmptyCart.visibility = View.GONE
                            binding.totalPriceFrame.visibility = View.VISIBLE

                            val cartData = doc.document.toObject(CartData::class.java)
                            cartList.add(cartData)
                        }


                    }
                    cartAdapter.notifyDataSetChanged()
                    initTotalPrice()
                }

            })
    }

    private fun initView() {

        binding.btnBack.setOnClickListener {
            finish()
        }

    }


    private val Int.dp
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            toFloat(), DisplayMetrics()
        ).roundToInt()
}