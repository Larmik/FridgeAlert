package com.larmik.fridgealert.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import com.larmik.fridgealert.common.callback.ProductCallback
import com.larmik.fridgealert.common.model.Product
import java.util.*

class SaveTask(private val context: Context,
               private val fileName: String,
               private val name : String,
               private val expires : String,
               private val bitmap: Bitmap?,
               private val callback : ProductCallback?) : AsyncTask<Unit, Unit, Unit>() {

    override fun doInBackground(vararg p0: Unit?) {
        bitmap?.let {
            ImageSaver(context)
                .setFileName(fileName)
                .save(it)
        }
    }

    override fun onPostExecute(result: Unit?) {
        callback?.onProductAdded(
            Product(
                name,
                expires,
                fileName,
                Calendar.getInstance().time.getString()
            )
        )
        super.onPostExecute(result)
    }

}