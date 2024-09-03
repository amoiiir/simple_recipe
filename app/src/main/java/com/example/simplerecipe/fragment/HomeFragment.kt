package com.example.simplerecipe.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.simplerecipe.R
import com.example.simplerecipe.ViewModel.ProductViewModel
import com.example.simplerecipe.databinding.FragmentHomeBinding
import com.example.simplerecipe.model.ProductResponseItem

/*
Available functions:
- onCreate
- onCreateView
- onViewCreated
- newInstance
*/

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var productViewModel: ProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        productViewModel = ProductViewModel()
    }

    private fun subscribe() {
        productViewModel.loading.observe(viewLifecycleOwner){loading ->
            if (loading) binding.tvResult.text = "Loading"
        }

        productViewModel.error.observe(viewLifecycleOwner){error ->
            if (error) binding.tvResult.text = "Error"
        }

        //for display data to the ui
        //viewlifecycleowner is like a lifecycle of the fragment
        //tak faham lagi
        productViewModel.productData.observe(viewLifecycleOwner){ data ->
            setResultText(data)

            //update ui
            binding.tvResult.text = data?.get(1)?.title.toString()
        }
    }

    private fun setResultText(data: List<ProductResponseItem>?) {
        Log.d("recipe_debug", "setResultText: data $data")
        Log.d("recipe_debug", "setResultText: data size ${data?.size}")
        Log.d("recipe_debug", "setResultText: title ${data?.get(0)?.title}")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //will display the data inside of the fragment
        super.onViewCreated(view, savedInstanceState)

        subscribe()
        productViewModel.getAllProduct()
    }

    companion object {
    }
}