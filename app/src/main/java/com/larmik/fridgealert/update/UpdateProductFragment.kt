package com.larmik.fridgealert.update

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.larmik.fridgealert.R
import com.larmik.fridgealert.common.callback.ListCallback
import com.larmik.fridgealert.common.model.Product
import com.larmik.fridgealert.utils.getDate
import com.larmik.fridgealert.utils.getExpireDate
import com.larmik.fridgealert.utils.getString
import com.larmik.fridgealert.utils.updateProduct
import kotlinx.android.synthetic.main.update_product_fragment.view.*
import java.util.*


class UpdateProductFragment(private val product : Product, private val position : Int) : DialogFragment() {

    var callback: ListCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.update_product_fragment, container, false)
        view.name_et.setText(product.name)
        view.tvDate.text = product.expireDate.getDate()?.getString()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            view.edit_date.setOnClickListener {
                val dialogPicker = DatePickerDialog(requireContext(), R.style.MyDatePickerDialogTheme)
                dialogPicker.datePicker.minDate = System.currentTimeMillis()
                val oldDate = Calendar.getInstance()
                oldDate.time = product.expireDate.getDate()!!
                dialogPicker.updateDate(oldDate.get(Calendar.YEAR),oldDate.get(Calendar.MONTH),oldDate.get(Calendar.DAY_OF_MONTH))
                dialogPicker.setOnDateSetListener { datePicker, i, i2, i3 ->
                    view.tvDate.text = datePicker.getExpireDate()
                    dialogPicker.dismiss()
                }
                dialogPicker.show()
            }
        }
        view.validate_btn.setOnClickListener {
            val product = Product(
                    view.name_et.text.toString(),
                    view.tvDate.text.toString(),
                    product.createdDate
            )
            product.id = this.product.id
            callback!!.onEditValidated(product, position)
            dismiss()
        }

        return view
    }

}