package com.example.simplerecipe.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.simplerecipe.R
import com.example.simplerecipe.model.ProductResponseItem
import kotlin.math.log

class ProductAdapter(private val productList: ArrayList<ProductResponseItem>, private val layoutResId: Int): RecyclerView.Adapter<ProductAdapter.ViewHolderClass> (){

    private var listener: OnItemClickListener? = null

    class ViewHolderClass(itemView: View): RecyclerView.ViewHolder(itemView){
        val prodImg: ImageView? = itemView.findViewById(R.id.product_image)
        val prodTitle: TextView? = itemView.findViewById(R.id.product_title)
        val prodRating: TextView? = itemView.findViewById(R.id.product_rating)
        val prodPrice: TextView? = itemView.findViewById(R.id.product_price)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        //where we inflate layouts
        val itemView = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        //return the size of the list
        return productList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        //bind with the necessary variables
        val currentItem: ProductResponseItem = productList[position]
        val itemSize: Int = productList.size
//        Log.d("recipe_debug", "size: ${productList.size}")
        holder.let {
            it.prodTitle?.text = currentItem.title
            it.prodRating?.text = currentItem.rating?.rate.toString()
            it.prodPrice?.text = "MYR ${currentItem.price.toString()}"
            it.itemView.setOnClickListener {
                listener?.onItemClick(position)
            }
        }

        //glide
        holder.prodImg?.let {
            Glide.with(holder.itemView.context)
                .load(currentItem.image)
                .into(it)
        }

    }

    fun setOnItemClickListener(clickListener: OnItemClickListener){
        listener = clickListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateProductList(newProductList: List<ProductResponseItem>){
        productList.clear()
        productList.addAll(newProductList)
        notifyDataSetChanged()
    }
}