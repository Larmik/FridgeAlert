package com.larmik.fridgealert.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import com.larmik.fridgealert.common.callback.ListCallback
import com.larmik.fridgealert.common.model.Product

class LoadTask(private val context: Context,
               private val list : List<Product>,
               private val callback : ListCallback?) : AsyncTask<Unit, Unit, List<Bitmap>>() {

    override fun doInBackground(vararg p0: Unit?): List<Bitmap>? {
        val images = arrayListOf<Bitmap>()
        for (item in list) {
            val image = ImageSaver(context)
                .setFileName(item.fileName)
                .load()
            image?.let {
                images.add(it)
            }
        }
        return images
    }

    override fun onPostExecute(result: List<Bitmap>?) {
        result?.let {
            callback?.onImagesReady(result)
        }
        super.onPostExecute(result)
    }

}