package com.larmik.fridgealert.common.callback

import com.larmik.fridgealert.common.model.Product

interface ListCallback {

    fun onListEmpty()

    fun onEditClick(product: Product)

    fun onEditValidated(product: Product, position: Int)
}