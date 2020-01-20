package com.larmik.fridgealert.list.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.larmik.fridgealert.R
import com.larmik.fridgealert.common.callback.ListCallback
import com.larmik.fridgealert.common.model.Product
import com.larmik.fridgealert.utils.deleteProduct
import com.larmik.fridgealert.utils.getDate
import kotlinx.android.synthetic.main.product_list_item.view.*
import java.util.*

class ProductListAdapter(private val context:  Context) : RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder>() {

    private val elements = arrayListOf<Product>()
    var callback: ListCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder = ProductListViewHolder(
        LayoutInflater.from(parent.context).inflate(
        R.layout.product_list_item, parent, false))

    override fun getItemCount(): Int = elements.size

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {
        val item = elements[position]
        holder.bindData(item)
        holder.itemView.edit_btn.setOnClickListener {
            callback!!.onEditClick(item)
        }

    }

    fun addData(elements : List<Product>) {
        this.elements.addAll(elements)
        notifyDataSetChanged()
    }
    fun clearData() {
        elements.clear()
        notifyDataSetChanged()
    }

    fun deleteProduct(position: Int) {
        val item = elements[position]
        context.deleteProduct(item)
        elements.remove(item)
        notifyItemRemoved(position)
        if (elements.isNullOrEmpty())
            callback!!.onListEmpty()
    }


    class ProductListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(item : Product) {
            itemView.product_name.text = item.name
            itemView.add_product_date.text = "Ajouté le ${item.createdDate}"
            val daysRemaining = getRemainingDays(item)
            itemView.expires_at.text = "$daysRemaining jours"
        }

        private fun getRemainingDays(item: Product) : Int {
            val calendar = Calendar.getInstance()
            val today = calendar.get(Calendar.DAY_OF_MONTH)
            calendar.time = item.expireDate.getDate() as Date
            val expires = calendar.get(Calendar.DAY_OF_MONTH)
            return expires - today

        }

    }

}