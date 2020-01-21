package com.larmik.fridgealert.list.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.larmik.fridgealert.R
import com.larmik.fridgealert.common.callback.ListCallback
import com.larmik.fridgealert.common.model.Product
import com.larmik.fridgealert.list.adapter.ProductListAdapter
import com.larmik.fridgealert.list.callback.SwipeToDeleteCallback
import com.larmik.fridgealert.update.UpdateProductFragment
import com.larmik.fridgealert.utils.updateProduct
import kotlinx.android.synthetic.main.fragment_product_list.view.*


class ProductListFragment : Fragment(), ListCallback {

    var products : ArrayList<Product>? = null
    lateinit var rootView : View
    private var adapter: ProductListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_product_list, container, false)
        rootView.recyclerview.layoutManager = LinearLayoutManager(activity)
        adapter = ProductListAdapter(requireContext())
        adapter!!.callback = this
        rootView.recyclerview.adapter = adapter
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapter!!, ColorDrawable(Color.RED), ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)!!))
        itemTouchHelper.attachToRecyclerView(rootView.recyclerview)
        products?.let {
            adapter!!.addData(it)
        }

        if (products.isNullOrEmpty()) {
            rootView.recyclerview.visibility = View.GONE
            rootView.empty_view.visibility = View.VISIBLE
        }
        return rootView
    }

    override fun onListEmpty() {
        rootView.recyclerview.visibility = View.GONE
        rootView.empty_view.visibility = View.VISIBLE
    }

    override fun onEditClick(product: Product) {
        val fragment = UpdateProductFragment(product, adapter!!.getPosition(product))
        fragment.callback = this
        if (!fragment.isAdded)
            fragment.show(childFragmentManager, "UpdateProductFragment.tag")
    }

    override fun onEditValidated(product: Product, position: Int) {
       adapter!!.updateData(product, position)
        requireContext().updateProduct(product)
    }


}