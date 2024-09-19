package com.example.simplerecipe.view.cart

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.simplerecipe.R
import com.example.simplerecipe.databinding.ActivityCartPageBinding

class CartPage : AppCompatActivity() {

    private lateinit var binding: ActivityCartPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCartPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

    }

    private fun initView() {

        binding.btnBack.setOnClickListener {
            finish()
        }

    }
}