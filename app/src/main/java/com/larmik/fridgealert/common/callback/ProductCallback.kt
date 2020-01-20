package com.larmik.fridgealert.common.callback

import com.larmik.fridgealert.common.model.Product

interface ProductCallback {
    fun onProductAdded(product : Product)
}