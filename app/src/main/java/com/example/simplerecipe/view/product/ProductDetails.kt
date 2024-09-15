package com.example.simplerecipe.view.product

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.simplerecipe.R
import com.example.simplerecipe.ViewModel.ProductViewModel
import com.example.simplerecipe.databinding.ActivityProductDetailsBinding

class ProductDetails : AppCompatActivity() {

    private lateinit var binding : ActivityProductDetailsBinding
    private lateinit var productViewModel: ProductViewModel

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

        initView()
    }

    private fun initView() {

        binding.btnBack.setOnClickListener {
            finish()
        }

        //call the function inside of the viewmodel
        productViewModel = ProductViewModel()
        productViewModel.getProductById(intent.getIntExtra(PRODUCT_ID, 0))

        //hide product description frame
//        binding.productDescriptionFrame.visibility = View.GONE
//
//        binding.btnProductDetailsFrame.setOnClickListener {
//            binding.productDescriptionFrame.visibility = View.VISIBLE
//        }

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
            }

        }

    }
}