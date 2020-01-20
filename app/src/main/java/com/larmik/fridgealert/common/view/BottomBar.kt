package com.larmik.fridgealert.common.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.larmik.fridgealert.R
import com.larmik.fridgealert.common.callback.NavigationCallback
import com.larmik.fridgealert.utils.FragmentToShow
import kotlinx.android.synthetic.main.bottombar.view.*

class BottomBar @JvmOverloads constructor(context : Context, attrs : AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    var view: View = LayoutInflater.from(context).inflate(R.layout.bottombar, null, false)
    lateinit var callback: NavigationCallback

    init {
        addView(view)
        view.goto_list.setOnClickListener {
            callback.onNavigation(FragmentToShow.PRODUCT_LIST)
        }
        view.goto_home.setOnClickListener {
            callback.onNavigation(FragmentToShow.HOME)
        }
        view.add_product_btn.setOnClickListener {
            callback.onNavigation(FragmentToShow.ADD_PRODUCT)
        }
    }




}