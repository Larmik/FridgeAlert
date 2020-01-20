package com.larmik.fridgealert.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase

import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns


class DbHelper(context: Context?) :
    SQLiteOpenHelper(
        context,
        DATABASE_NAME,
        null,
        DATABASE_VERSION
    ) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_PRODUCT_ENTRIES)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL(SQL_DELETE_PRODUCT_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        const val SQL_CREATE_PRODUCT_ENTRIES =
            ("CREATE TABLE " + DBContract.ProductEntry.TABLE_NAME + "("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + DBContract.ProductEntry.COLUMN_NAME_PRODUCTNAME + " TEXT,"
                    + DBContract.ProductEntry.COLUMN_NAME_EXPIRES + " TEXT,"
                    + DBContract.ProductEntry.COLUMN_NAME_CREATED + " TEXT);")
        const val SQL_DELETE_PRODUCT_ENTRIES =
            "DROP TABLE IF EXISTS " + DBContract.ProductEntry.TABLE_NAME
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "database.db"
    }
}