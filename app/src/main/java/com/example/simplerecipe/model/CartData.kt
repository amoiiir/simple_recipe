package com.example.simplerecipe.model

data class CartData(
    val id: Int? = 0,
    val title: String? = "",
    val price: Double? = 0.0,
    val description: String? = "",
    val category: String? = "",
    val image: String? = "",
    var amount: Int? = 0,
//    var totalAmount: Double? = 0.0,
)
