package com.larmik.fridgealert.update

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.larmik.fridgealert.R
import com.larmik.fridgealert.common.model.Product
import com.larmik.fridgealert.utils.getDate
import com.larmik.fridgealert.utils.getExpireDate
import com.larmik.fridgealert.utils.getString
import kotlinx.android.synthetic.main.update_product_fragment.view.*
import java.util.*


class UpdateProductFragment(private val product : Product) : DialogFragment(){

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
                val dialogPicker = DatePickerDialog(requireContext())
                dialogPicker.datePicker.minDate = System.currentTimeMillis()
                val oldDate = Calendar.getInstance()
                oldDate.time = product.expireDate.getDate()!!
                dialogPicker.setOnDateSetListener { datePicker, i, i2, i3 ->
                    view.tvDate.text = datePicker.getExpireDate()
                    dialogPicker.dismiss()
                }
                dialogPicker.show()
            }
        }
        view.validate_btn.setOnClickListener {

        }

        return view
    }

}