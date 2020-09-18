package com.example.maalikflupertask.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.maalikflupertask.R
import com.example.maalikflupertask.db.Product
import kotlinx.android.synthetic.main.product_layout.view.*

class ProductsAdapter(private val Products: List<Product>) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.product_layout, parent, false)
        )
    }

    override fun getItemCount() = Products.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.view.text_view_name.text = Products[position].name
        holder.view.text_view_price.text = Products[position].price



        Glide.with(holder.view)  //2
            .load(Products[position].image) //3
            .placeholder(R.drawable.ic_image_placeholder) //5
            .into(holder.view.image_item) //8


        holder.view.setOnClickListener {
            val action = HomeFragmentDirections.actionAddProduct()
            action.product = Products[position]
            Navigation.findNavController(it).navigate(action)
        }
    }

    class ProductViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}