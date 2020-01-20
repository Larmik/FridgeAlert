package com.larmik.fridgealert.utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Point
import android.provider.BaseColumns
import android.view.Display
import android.view.WindowManager
import android.widget.DatePicker
import com.larmik.fridgealert.common.model.Product
import kotlinx.android.synthetic.main.fragment_add_product.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


fun Context.displayHeight(): Int {
    val display: Display =
        (this.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    val size = Point()
    display.getSize(size)
    return size.y
}

fun DatePicker.getExpireDate() : String {
        val day: Int = this.dayOfMonth
        val month: Int = this.month
        val year: Int = this.year
        val calendar = Calendar.getInstance()
        calendar[year, month] = day
        return calendar.time.getString()

}

fun Context.loadProducts() : ArrayList<Product> {
    val products = arrayListOf<Product>()
    val mDbHelper = DbHelper(this)
    val db = mDbHelper.readableDatabase
    val projection = arrayOf(
        BaseColumns._ID,
        DBContract.ProductEntry.COLUMN_NAME_PRODUCTNAME,
        DBContract.ProductEntry.COLUMN_NAME_EXPIRES,
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
        val created: String =
            cursor.getString(cursor.getColumnIndexOrThrow(DBContract.ProductEntry.COLUMN_NAME_CREATED))
        val product = Product(name, expires, created)
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
    val format =  SimpleDateFormat("dd MMM yyyy")
    return format.format(this)
}

fun String.getDate(): Date? {
    val format =  SimpleDateFormat("dd MMM yyyy")
    return format.parse(this)
}

fun Context.addProduct(item: Product) {
    val mDbHelper = DbHelper(this)
    val db = mDbHelper.writableDatabase
    val product = ContentValues()
    product.put(DBContract.ProductEntry.COLUMN_NAME_PRODUCTNAME, item.name)
    product.put(DBContract.ProductEntry.COLUMN_NAME_EXPIRES, item.expireDate)
    product.put(DBContract.ProductEntry.COLUMN_NAME_CREATED, item.createdDate)
    db.insert(DBContract.ProductEntry.TABLE_NAME, null, product)
}

fun Context.updateProduct(product: Product) {
    val mDbHelper = DbHelper(this)
    val db = mDbHelper.writableDatabase
    val cv = ContentValues()
    cv.put(DBContract.ProductEntry.COLUMN_NAME_PRODUCTNAME, product.name)
    cv.put(DBContract.ProductEntry.COLUMN_NAME_EXPIRES, product.expireDate)
    db.update(DBContract.ProductEntry.TABLE_NAME, cv, BaseColumns._ID + "=" + product.id, null)
}