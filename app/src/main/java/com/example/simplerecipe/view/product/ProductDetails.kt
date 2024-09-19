package com.example.simplerecipe.view.product

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.simplerecipe.R
import com.example.simplerecipe.ViewModel.ProductViewModel
import com.example.simplerecipe.databinding.ActivityProductDetailsBinding
import com.example.simplerecipe.model.CartData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProductDetails : AppCompatActivity() {

    private lateinit var binding : ActivityProductDetailsBinding
    private lateinit var productViewModel: ProductViewModel
    private lateinit var db : FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    companion object {
        const val PRODUCT_ID = "product_id"

        //start activity
        fun productIntent(context: Context, productId: Int): Intent{
            val intent = Intent(context, ProductDetails::class.java)
            intent.putExtra(PRODUCT_ID, productId)
            Log.d("recipe_debug", "productIntent: $productId")
            return intent
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //inflate layout
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        initView()
        displayProduct()
    }

    private fun initView() {

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun displayProduct() {
        //call the function inside of the viewmodel
        productViewModel = ProductViewModel()
        productViewModel.getProductById(intent.getIntExtra(PRODUCT_ID, 0))


        //observe the data
        productViewModel.productDataById.observe(this){data ->
            binding.let {
                it.productTitle.text = data?.title
                it.productPrice.text = "MYR " + data?.price.toString()
                it.productDescription.text = data?.description ?: "No Description"
                it.productCategory.text = data?.category
                it.productRating.text = data?.rating?.rate.toString() + " rating from " + data?.rating?.count.toString() + " users"

                //image
                Glide.with(this)
                    .load(data?.image)
                    .into(it.productImage)
                
                //add to cart function
                binding.btnAddToCart.setOnClickListener {
                    val initAmount = 1
                    addToCart(data?.id!!, data.title!!, data.price!!, data.description!!, data.category!!, data.image!!, initAmount)
                }
                
            }

        }
        
    }

    private fun addToCart(productId: Int, title: String, price: Any, description: String, category: String, image: String, amount: Int) {
        //get current user
        val currentUser = firebaseAuth.currentUser?.uid
        Log.d("recipe_debug", "addToCart: $currentUser")


        val cartData = CartData(productId, title, price.toString().toDouble(), description, category, image, amount)
        val cartRef = db.collection("users/$currentUser/cart").document(productId.toString())

        cartRef.set(cartData)
            .addOnSuccessListener {
                Toast.makeText(this, cartData.title + "has been added to the cart!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add item!", Toast.LENGTH_SHORT).show()
            }
    }

}