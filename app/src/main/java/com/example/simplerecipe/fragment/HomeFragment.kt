package com.example.simplerecipe.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    //call adapter
    private lateinit var recyclerView: RecyclerView
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
    }

    private fun recommendedCards() {
        //set up recyclerview layout
        recyclerView = binding.rvRecommendedCards
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.setHasFixedSize(true)

        productAdapter = ProductAdapter(ArrayList())
        recyclerView.adapter = productAdapter
    }


    private fun subscribe() {
        productViewModel.loading.observe(viewLifecycleOwner){loading ->
//            if (loading) binding.tvResult.text = "Loading"
        }

        productViewModel.error.observe(viewLifecycleOwner){error ->
//            if (error) binding.tvResult.text = "Error"
        }

        //for display data to the ui
        //viewlifecycleowner is like a lifecycle of the fragment
        productViewModel.productData.observe(viewLifecycleOwner){ data ->
            Log.d("recipe_debug", "setResultText: rating ${data?.joinToString { it.rating.toString() }}")


            //call adapter here
            if (data != null) {
                productAdapter = ProductAdapter(ArrayList(data))
                recyclerView.adapter = productAdapter
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