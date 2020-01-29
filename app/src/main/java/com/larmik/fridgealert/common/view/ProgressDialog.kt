package com.larmik.fridgealert.common.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialog
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.larmik.fridgealert.R
import kotlinx.android.synthetic.main.progress_dialog.*

class ProgressDialog(context: Context) : AppCompatDialog(context) {

    init {
        setCancelable(false)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.progress_dialog)

        ButterKnife.bind(this)
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        Glide.with(context)
            .load("https://media.giphy.com/media/3o7bu8sRnYpTOG1p8k/giphy.gif")
            .asGif()
            .error(R.drawable.ic_edit)
            .into(progressbar)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

}