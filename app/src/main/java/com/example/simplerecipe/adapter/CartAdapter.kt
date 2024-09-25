package com.example.simplerecipe.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.simplerecipe.R
import com.example.simplerecipe.model.CartData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class CartAdapter(
    private val cartList: ArrayList<CartData>
): RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    val addedAmount: Int = 1
    private lateinit var db : FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        val prodImg: ImageView? = itemView.findViewById(R.id.product_image)
        val prodTitle: TextView? = itemView.findViewById(R.id.product_title)
        val prodPrice: TextView? = itemView.findViewById(R.id.product_price)
        val prodAmount: TextView? = itemView.findViewById(R.id.product_amount)
        val prodCategory: TextView? = itemView.findViewById(R.id.product_category)
        val btnAdd: ImageView? = itemView.findViewById(R.id.btn_add)
        val btnSubtract: ImageView? = itemView.findViewById(R.id.btn_subtract)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.rv_cart_cards, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = cartList[position]
        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        holder.let {data->
            data.prodTitle?.text = currentItem.title
            data.prodPrice?.text = "MYR ${currentItem.price}"
            data.prodAmount?.text = currentItem.amount.toString()
            data.prodCategory?.text = currentItem.category
        }

        holder.prodImg?.let {
            Glide.with(holder.itemView.context)
                .load(currentItem.image)
                .into(it)
        }

        initAddItem(holder, currentItem, position)
        initSubtractItem(holder, currentItem, position)
    }

    private fun initSubtractItem(holder: ViewHolder, currentItem: CartData, position: Int) {
        holder.btnSubtract?.setOnClickListener {
            if (currentItem.amount!! > 1) {
                currentItem.amount = currentItem.amount?.minus(addedAmount)

                //update firestore
                val docRef = db.collection("users/${firebaseAuth.currentUser?.uid}/cart").document(currentItem.id.toString())
                Log.d("cart_data", "initSubtractItem: $docRef")
                docRef.update("amount", FieldValue.increment(-1))
                notifyItemChanged(position)


            }else{
                Toast.makeText(holder.itemView.context, "Minimum amount is 1", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initAddItem(holder: ViewHolder, currentItem: CartData, position: Int) {
        holder.btnAdd?.setOnClickListener {
            currentItem.amount = currentItem.amount?.plus(addedAmount)

            //update firestore
            val docRef = db.collection("users/${firebaseAuth.currentUser?.uid}/cart").document(currentItem.id.toString())
            Log.d("cart_data", "initAddItem: $docRef")
            docRef.update("amount", FieldValue.increment(1))

            notifyItemChanged(position)
        }
    }

    fun totalItem(): Int {
        var total = 0
        for (i in cartList) {
            total += i.amount!! * i.price!!.toInt()
            Log.d("cart_data", "totalItem: $total")
        }
        return total
    }
}