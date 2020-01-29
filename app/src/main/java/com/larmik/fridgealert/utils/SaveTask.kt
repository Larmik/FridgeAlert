package com.larmik.fridgealert.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import com.larmik.fridgealert.common.callback.ProductCallback
import com.larmik.fridgealert.common.model.Product
import java.util.*

class SaveTask(
    private val action: ProductAction,
    private val context: Context,
    private val product: Product,
    private val bitmap: Bitmap?,
    private val callback: ProductCallback?,
    private val position: Int = 0
) : AsyncTask<Unit, Unit, Unit>() {

    override fun doInBackground(vararg p0: Unit?) {
        bitmap?.let {
            ImageSaver(context)
                .setFileName(product.fileName)
                .save(it)
        }
    }

    override fun onPostExecute(result: Unit?) {
        val createdDate = when (action) {
            ProductAction.ADD -> Calendar.getInstance().time.getString()
            else -> product.createdDate
        }
        val product = Product(
            product.name,
            product.expireDate,
            product.fileName,
            createdDate
        )
        product.id = this.product.id
        when (action) {
            ProductAction.ADD -> callback?.onProductAdded(product)
            else -> callback?.onProductEdited(product, position)
        }
        super.onPostExecute(result)
    }

}