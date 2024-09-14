package com.example.simplerecipe.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplerecipe.R
import com.example.simplerecipe.ViewModel.ProductViewModel
import com.example.simplerecipe.adapter.ProductAdapter
import com.example.simplerecipe.databinding.FragmentHomeBinding
import com.example.simplerecipe.model.ProductResponseItem
import com.example.simplerecipe.view.product.ProductDetails
import com.google.firebase.firestore.FirebaseFirestore

/*
Available functions:
- onCreate
- onCreateView
- onViewCreated
- newInstance
*/

class HomeFragment : Fragment() {

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var productViewModel: ProductViewModel
    //seperate recyclerview for recommended and product
    private lateinit var recommendedRecyclerView: RecyclerView
    private lateinit var productRecyclerView: RecyclerView

    private lateinit var recommendedAdapter: ProductAdapter
    private lateinit var productAdapter: ProductAdapter

    //call db
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        //will create the fragment and will be used to initialize variables
        super.onCreate(savedInstanceState)

        productViewModel = ProductViewModel()
        db = FirebaseFirestore.getInstance()

    }

    override fun onCreateView(
        // create the ui into the application.
        // Inflate the layout of the fragment
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // where we interact with the UI Components, for example, setting up the recycler view, setting up the adapter, etc.
        //will display the data inside of the fragment
        super.onViewCreated(view, savedInstanceState)

        productViewModel.getAllProduct()

        greetings()
        subscribe()
        recommendedCards()
        gridProducts()
    }

    private fun greetings() {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    binding.homeUser.text = "Hello, ${document.data["username"].toString()}!"
                }
            }
            .addOnFailureListener { exception ->
                binding.homeUser.text = "Welcome Back!"
            }
    }

    private fun gridProducts() {
        productRecyclerView = binding.rvProductCards
        productRecyclerView.layoutManager = GridLayoutManager(context, 2)
        productRecyclerView.setHasFixedSize(true)

        productAdapter = ProductAdapter(ArrayList(), R.layout.rv_product_cards)
        productRecyclerView.adapter = productAdapter
    }

    private fun recommendedCards() {
        //set up recyclerview layout
        recommendedRecyclerView = binding.rvRecommendedCards
        recommendedRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recommendedRecyclerView.setHasFixedSize(true)

        recommendedAdapter = ProductAdapter(ArrayList(), R.layout.rv_recommended_cards)
        recommendedRecyclerView.adapter = recommendedAdapter
    }

    private fun subscribe() {
        //for display data to the ui
        //viewlifecycleowner is like a lifecycle of the fragment
        productViewModel.productData.observe(viewLifecycleOwner){ data ->

            if (data != null) {
                //call recommended cards
                getRecommendedCards(data)
                getProductCards(data)

                recommendedAdapter.setOnItemClickListener(object: ProductAdapter.OnItemClickListener{
                    override fun onItemClick(position: Int){
                        val product = data[position]
                        Log.d("recipe_debug", "onItemClick: recommendedAdapter ${product.title}")
                        Log.d("recipe_debug", "onItemClick: recommendedAdapter ${product.id}")

                    }
                })

                productAdapter.setOnItemClickListener(object: ProductAdapter.OnItemClickListener{
                    override fun onItemClick(position: Int){
                        val product = data[position]
                        Log.d("recipe_debug", "onItemClick: productAdapter ${product.title}")
                        Log.d("recipe_debug", "onItemClick: productAdapter ${product.id}")

                        //redirect user to product detail
                        startActivity(ProductDetails.productIntent(requireContext(), product.id.toString()))
                    }
                })
            }else{
                Log.d("recipe_debug", "setResultText: data is null")
            }
        }
    }

    private fun getProductCards(data: List<ProductResponseItem>) {
        productAdapter = ProductAdapter(ArrayList(data), R.layout.rv_product_cards)
        productRecyclerView.adapter = productAdapter
        productAdapter.notifyDataSetChanged()
    }

    private fun getRecommendedCards(data: List<ProductResponseItem>) {
        recommendedAdapter = ProductAdapter(ArrayList(data), R.layout.rv_recommended_cards)
        recommendedRecyclerView.adapter = recommendedAdapter
        recommendedAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}