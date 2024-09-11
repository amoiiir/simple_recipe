package com.example.simplerecipe.fragment

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


    override fun onCreate(savedInstanceState: Bundle?) {
        //will create the fragment and will be used to initialize variables
        super.onCreate(savedInstanceState)

        productViewModel = ProductViewModel()

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

        subscribe()
        recommendedCards()
        gridProducts()
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

            //call recommended cards    
            if (data != null) {
                recommendedAdapter = ProductAdapter(ArrayList(data), R.layout.rv_recommended_cards)
                recommendedRecyclerView.adapter = recommendedAdapter
                recommendedAdapter.notifyDataSetChanged()
            }else{
                Log.d("recipe_debug", "setResultText: data is null")
            }

            //call product adapter
            if (data != null) {
                productAdapter = ProductAdapter(ArrayList(data), R.layout.rv_product_cards)
                productRecyclerView.adapter = productAdapter
                productAdapter.notifyDataSetChanged()
            }else{
                Log.d("recipe_debug", "setResultText: data is null")
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}