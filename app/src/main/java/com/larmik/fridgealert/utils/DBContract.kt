package com.larmik.fridgealert.utils

import android.provider.BaseColumns

object DBContract {

    object ProductEntry : BaseColumns {
        const val TABLE_NAME = "Products"
        const val COLUMN_NAME_PRODUCTNAME = "Name"
        const val COLUMN_NAME_EXPIRES = "Expires"
        const val COLUMN_NAME_CREATED = "Created"

    }

}