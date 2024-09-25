package com.example.simplerecipe.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplerecipe.R
import com.example.simplerecipe.ViewModel.ProductViewModel
import com.example.simplerecipe.adapter.ProductAdapter
import com.example.simplerecipe.databinding.FragmentHomeBinding
import com.example.simplerecipe.model.ProductResponseItem
import com.example.simplerecipe.view.cart.CartPage
import com.example.simplerecipe.view.loader.LoadingUtils
import com.example.simplerecipe.view.product.ProductDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

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
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        //will create the fragment and will be used to initialize variables
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        productViewModel = ProductViewModel()
        firebaseAuth = FirebaseAuth.getInstance()
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

        //call loading animation
//        LoadingUtils.showDialog(requireContext(), true)

        greetings()
        subscribe()
        recommendedCards()
        gridProducts()
        directAddToCart()

    }

    private fun directAddToCart() {
        binding.btnCart.setOnClickListener {
            startActivity(Intent(requireContext(), CartPage::class.java))
        }
    }

    private fun greetings() {
        val users = firebaseAuth.currentUser?.uid
        if (users != null) {
            db.collection("users")
                .document(users)
                .get()
                .addOnSuccessListener {
                val username = it.getString("username")
                binding.homeUser.text = "Hello, $username!"
                }
                .addOnFailureListener {
                    binding.homeUser.text = "Hello, User!"
                }
        }else{
            binding.homeUser.text = "Hello, User!"
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

        productViewModel.loading.observe(viewLifecycleOwner){ loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.progressBar2.visibility = if (loading) View.VISIBLE else View.GONE
        }

        productViewModel.error.observe(viewLifecycleOwner){error ->
            binding.errorFrame.visibility = if (error) View.VISIBLE else View.GONE
            binding.tvError.text = (if (error) resources.getString(R.string.error) else "").toString()
        }

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
                        Log.d("recipe_debug", "onItemClick: recommendedAdapter ${product.id}")

                    }
                })

                productAdapter.setOnItemClickListener(object: ProductAdapter.OnItemClickListener{
                    override fun onItemClick(position: Int){
                        val product = data[position]
                        Log.d("recipe_debug", "onItemClick: productAdapter ${product.id!!}")

                        //redirect user to product detail
                        startActivity(ProductDetails.productIntent(requireContext(), product.id))
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