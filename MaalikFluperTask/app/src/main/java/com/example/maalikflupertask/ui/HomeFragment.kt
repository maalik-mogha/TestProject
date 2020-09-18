package com.example.maalikflupertask.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.maalikflupertask.R
import com.example.maalikflupertask.db.ProductDatabase
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recycler_view_products.setHasFixedSize(true)

        recycler_view_products.layoutManager = GridLayoutManager(requireActivity(),2)


        launch {
            context?.let{
                val products = ProductDatabase(it).getProductDao().getAllProducts()
                recycler_view_products.adapter = ProductsAdapter(products)
            }
        }


        button_add.setOnClickListener {

            val action = HomeFragmentDirections.actionAddProduct()
            Navigation.findNavController(it).navigate(action)

        }
    }

}
