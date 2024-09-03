package com.example.simplerecipe.`interface`

import com.example.simplerecipe.model.ProductResponseItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("products")
    fun getProducts(): Call<List<ProductResponseItem>>

    @GET("products/{id}")
    fun getProductId(
        @Path("id") id: Int,
    ): Call<ProductResponseItem>

}