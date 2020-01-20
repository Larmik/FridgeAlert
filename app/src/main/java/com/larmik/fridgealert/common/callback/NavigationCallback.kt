package com.larmik.fridgealert.common.callback

import com.larmik.fridgealert.utils.FragmentToShow

interface NavigationCallback {

    fun onNavigation(fragmentToShow: FragmentToShow)

}