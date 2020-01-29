package com.larmik.fridgealert.utils

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Point
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.BaseColumns
import android.view.Display
import android.view.WindowManager
import android.widget.DatePicker
import com.larmik.fridgealert.common.model.Product
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


fun Context.displayHeight(): Int {
    val display: Display =
        (this.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    val size = Point()
    display.getSize(size)
    return size.y
}

fun DatePicker.getExpireDate(): String {
    val day: Int = this.dayOfMonth
    val month: Int = this.month
    val year: Int = this.year
    val calendar = Calendar.getInstance()
    calendar[year, month] = day
    return calendar.time.getString()

}

fun Context.loadProducts(): ArrayList<Product> {
    val products = arrayListOf<Product>()
    val mDbHelper = DbHelper(this)
    val db = mDbHelper.readableDatabase
    val projection = arrayOf(
        BaseColumns._ID,
        DBContract.ProductEntry.COLUMN_NAME_PRODUCTNAME,
        DBContract.ProductEntry.COLUMN_NAME_EXPIRES,
        DBContract.ProductEntry.COLUMN_NAME_IMAGE,
        DBContract.ProductEntry.COLUMN_NAME_CREATED
    )
    val cursor: Cursor = db.query(
        DBContract.ProductEntry.TABLE_NAME,
        projection,
        null, null, null, null, null
    )
    while (cursor.moveToNext()) {
        val id: Long =
            cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
        val name: String =
            cursor.getString(cursor.getColumnIndexOrThrow(DBContract.ProductEntry.COLUMN_NAME_PRODUCTNAME))
        val expires: String =
            cursor.getString(cursor.getColumnIndexOrThrow(DBContract.ProductEntry.COLUMN_NAME_EXPIRES))
        val image: String =
            cursor.getString(cursor.getColumnIndexOrThrow(DBContract.ProductEntry.COLUMN_NAME_IMAGE))
        val created: String =
            cursor.getString(cursor.getColumnIndexOrThrow(DBContract.ProductEntry.COLUMN_NAME_CREATED))
        val product = Product(name, expires, image, created)
        product.id = id
        products.add(product)
    }
    cursor.close()
    return products
}

fun Context.deleteProduct(product: Product) {
    val mDbHelper = DbHelper(this)
    val db = mDbHelper.readableDatabase
    db.delete(DBContract.ProductEntry.TABLE_NAME, BaseColumns._ID + "=" + product.id, null)
}

fun Date.getString(): String {
    val format = SimpleDateFormat("dd MMM yyyy")
    return format.format(this)
}

fun String.getDate(): Date? {
    val format = SimpleDateFormat("dd MMM yyyy")
    return format.parse(this)
}

fun Context.addProduct(item: Product) {
    val mDbHelper = DbHelper(this)
    val db = mDbHelper.writableDatabase
    val product = ContentValues()
    product.put(DBContract.ProductEntry.COLUMN_NAME_PRODUCTNAME, item.name)
    product.put(DBContract.ProductEntry.COLUMN_NAME_EXPIRES, item.expireDate)
    product.put(DBContract.ProductEntry.COLUMN_NAME_IMAGE, item.fileName)
    product.put(DBContract.ProductEntry.COLUMN_NAME_CREATED, item.createdDate)
    db.insert(DBContract.ProductEntry.TABLE_NAME, null, product)
}

fun Context.updateProduct(product: Product) {
    val mDbHelper = DbHelper(this)
    val db = mDbHelper.writableDatabase
    val cv = ContentValues()
    cv.put(DBContract.ProductEntry.COLUMN_NAME_PRODUCTNAME, product.name)
    cv.put(DBContract.ProductEntry.COLUMN_NAME_EXPIRES, product.expireDate)
    cv.put(DBContract.ProductEntry.COLUMN_NAME_IMAGE, product.fileName)
    cv.put(DBContract.ProductEntry.COLUMN_NAME_CREATED, product.createdDate)
    db.update(DBContract.ProductEntry.TABLE_NAME, cv, BaseColumns._ID + "=" + product.id, null)
}


fun Bitmap.rotateForGallery(context: Context, uri: Uri?): Bitmap? {
    val matrix = Matrix()
    return try {
        uri?.getExifAngle(context)?.let { matrix.postRotate(it) }
        Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
    } catch (e: Exception) {
        this
    }
}

fun Uri.getExifInterface(context: Context): ExifInterface? {
    try {
        if (this.toString().startsWith("file://"))
            return ExifInterface(this.toString())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && this.toString().startsWith("content://"))
            return ExifInterface(context.contentResolver.openInputStream(this)!!)
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}

fun Uri.getExifAngle(context: Context): Float {
    try {
        val exifInterface = this.getExifInterface(context) ?: return -1f
        return when (exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            ExifInterface.ORIENTATION_NORMAL -> 0f
            ExifInterface.ORIENTATION_UNDEFINED -> -1f
            else -> -1f
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return -1f
    }
}

@Throws(IOException::class)
fun Activity.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(imageFileName, ".jpg", storageDir)
}



