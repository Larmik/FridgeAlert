package com.larmik.fridgealert.common.callback

import android.graphics.Bitmap
import com.larmik.fridgealert.common.model.Product

interface ListCallback {

    fun onListEmpty()

    fun onEditClick(product: Product)

    fun onEditValidated(product: Product, position: Int)

    fun onImagesReady(list: List<Bitmap>)

}