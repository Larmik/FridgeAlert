package com.larmik.fridgealert.add

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.larmik.fridgealert.R
import com.larmik.fridgealert.common.callback.ProductCallback
import com.larmik.fridgealert.common.model.Product
import com.larmik.fridgealert.utils.displayHeight
import com.larmik.fridgealert.utils.getExpireDate
import com.larmik.fridgealert.utils.getString
import kotlinx.android.synthetic.main.fragment_add_product.view.*
import java.util.*

class AddProductFragment : BottomSheetDialogFragment() {

    lateinit var rootView: View
    var callback: ProductCallback? = null
    var year = 0
    var month = 0
    var day = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_add_product, container, false)
        setCurrentDateOnView()
        rootView.dpResult.minDate = System.currentTimeMillis()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            rootView.dpResult.setOnDateChangedListener { _, i, i2, i3 ->
                year = i
                month = i2
                day = i3

                rootView.tvDate.text = rootView.dpResult.getExpireDate()
            }
        }

        rootView.validate_btn.setOnClickListener {
            var name = "Non renseigné"
            if (rootView.name_et.text.toString().isNotEmpty())
                name = rootView.name_et.text.toString()
            callback?.onProductAdded(
                Product(
                    name,
                    rootView.dpResult.getExpireDate(),
                    Calendar.getInstance().time.getString()
                )
            )
            dismiss()
        }
        return rootView
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setOnShowListener {
            val bottomSheet =
                (it as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout

            context?.displayHeight()?.let { height ->
                bottomSheet.minimumHeight = height
            }

            BottomSheetBehavior.from(bottomSheet).run {
                state = BottomSheetBehavior.STATE_EXPANDED
                peekHeight = bottomSheet.minimumHeight
                isHideable = false
            }
        }
    }

    private fun setCurrentDateOnView() {
        val c: Calendar = Calendar.getInstance()
        year = c.get(Calendar.YEAR)
        month = c.get(Calendar.MONTH)
        day = c.get(Calendar.DAY_OF_MONTH)
        // set current date into textview
        rootView.dpResult.init(year, month, day, null)
        rootView.tvDate.text = rootView.dpResult.getExpireDate()
    }

}