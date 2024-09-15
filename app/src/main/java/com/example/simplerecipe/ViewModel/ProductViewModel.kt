package com.example.simplerecipe.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplerecipe.`interface`.ApiConfig
import com.example.simplerecipe.model.ProductResponseItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductViewModel : ViewModel() {

    // MutableLiveData to hold a list of ProductResponseItem
    // we allow _productData to expect null values
    private val _productData = MutableLiveData<List<ProductResponseItem>?>()
    val productData: LiveData<List<ProductResponseItem>?> get() = _productData

    //to retrieve single product by id
    private val _productDataById = MutableLiveData<ProductResponseItem?>()
    val productDataById: LiveData<ProductResponseItem?> get() = _productDataById

    // Loading state
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    // Error state
    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean>
        get() = _error

    var errorMessage: String = ""
        private set

    // Function to fetch all products
    fun getAllProduct() {
        _loading.value = true
        _error.value = false

        val client = ApiConfig.getApiService().getProducts()

        client.enqueue(object : Callback<List<ProductResponseItem>> {
            override fun onResponse(
                call: Call<List<ProductResponseItem>>,
                response: Response<List<ProductResponseItem>>
            ) {
                val responseBody = response.body()
                if (!response.isSuccessful || responseBody.isNullOrEmpty()) {
                    onError("Error Fetching Data")
                    return
                }

                _loading.value = false
                _productData.postValue(responseBody)
            }

            override fun onFailure(
                call: Call<List<ProductResponseItem>>,
                t: Throwable
            ) {
                onError("Error Fetching Data")
            }
        })
    }

    fun getProductById (id : Int){
        _loading.value = true
        _error.value = false

        val client = ApiConfig.getApiService().getProductId(id = id)

        client.enqueue(object : Callback<ProductResponseItem>{
            override fun onResponse(
                call: Call<ProductResponseItem>,
                response: Response<ProductResponseItem>
            ) {
                val responseBody = response.body()
                if (!response.isSuccessful || responseBody == null) {
                    onError("Error Fetching Data")
                    return
                }

                _loading.value = false
                _productDataById.postValue(responseBody)
            }

            override fun onFailure(
                call: Call<ProductResponseItem>,
                t: Throwable
            ) {
                onError("Error Fetching Data")
            }
        })
    }

    // Error handling method
    private fun onError(inputMessage: String) {
        val message = if (inputMessage.isNullOrBlank()) "Unknown Error"
        else inputMessage

        errorMessage = StringBuilder("Error: ").append(message).toString()

        _loading.value = false
        _error.value = true
    }
}
