package com.larmik.fridgealert.common.model

import com.larmik.fridgealert.utils.getDate
import java.util.*
import java.util.concurrent.TimeUnit

data class Product(val name : String = "Non renseigné", val expireDate : String, val fileName : String, val createdDate : String) {
    var id: Long = 0

    fun getRemainingDays() : Long {
        var calendar = Calendar.getInstance()
        var expires = this.expireDate.getDate() as Date
        calendar.time = expires
        calendar.set(Calendar.HOUR, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        expires = calendar.time
        calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val diff: Long = expires.time - calendar.time.time
        return TimeUnit.DAYS.convert(
            diff,
            TimeUnit.MILLISECONDS
        )
    }


}