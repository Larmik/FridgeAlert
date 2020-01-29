package com.larmik.fridgealert.list.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.larmik.fridgealert.R
import com.larmik.fridgealert.common.callback.ListCallback
import com.larmik.fridgealert.common.model.Product
import com.larmik.fridgealert.utils.ImageSaver
import com.larmik.fridgealert.utils.deleteProduct
import kotlinx.android.synthetic.main.product_list_item.view.*


class ProductListAdapter(private val context:  Context) : RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder>() {

    private val elements = arrayListOf<Product>()
    private val images = arrayListOf<Bitmap>()
    var callback: ListCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder = ProductListViewHolder(parent.context,
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

    fun addData(products : List<Product>? = null, images: List<Bitmap>? = null) {
        products?.let {
            this.elements.addAll(it)
        }
        images?.let {
            this.images.addAll(it)
        }
        notifyDataSetChanged()
    }

    fun clearData() {
        elements.clear()
        images.clear()
        notifyDataSetChanged()
    }

    fun updateData(product: Product, position: Int) {
        elements.remove(elements[position])
        elements.add(position, product)
        notifyDataSetChanged()
    }

    fun getPosition(product: Product) : Int = elements.indexOf(product)

    fun deleteProduct(position: Int) {
        val item = elements[position]
        context.deleteProduct(item)
        elements.remove(item)
        notifyItemRemoved(position)
        if (elements.isNullOrEmpty())
            callback!!.onListEmpty()
    }


    class ProductListViewHolder(private val context: Context, itemView : View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(item : Product) {
            itemView.product_name.text = item.name
            itemView.product_iv.clipToOutline = true
            itemView.add_product_date.text = "Ajouté le ${item.createdDate}"
            val image = ImageSaver(context)
                .setFileName(item.fileName)
                .load()
            itemView.product_iv.load(image)
            val daysRemaining = item.getRemainingDays()
            if (daysRemaining >= 0) {
                when (daysRemaining) {
                    0L -> {
                        itemView.expires_lbl.text = "Périme"
                        itemView.expires_at.text = "aujourd'hui"
                        //TODO Change color why not
                    }
                    1L -> {
                        itemView.expires_lbl.text = "Périme"
                        itemView.expires_at.text = "demain"
                    }
                    else -> {
                        itemView.expires_lbl.text = "Périme dans"
                        itemView.expires_at.text = "$daysRemaining jours"
                    }
                }
            }
            else {
                itemView.expires_lbl.visibility = View.GONE
                itemView.expires_at.text = "Périmé"
            }

            setEasterEgg(item.name, itemView.expires_at)
        }



        private fun setEasterEgg(productName : String, view : TextView) {
            if (productName.trim().toLowerCase() == "Miel".toLowerCase()) {
                view.text = "10 milliards d'années"
            }
        }

    }

}