package com.larmik.fridgealert.common.model

data class Product(val name : String = "Non renseigné", val expireDate : String, val createdDate : String) {
    var id: Long = 0
}