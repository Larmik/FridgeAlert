package com.larmik.fridgealert.update

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import coil.api.load
import com.larmik.fridgealert.BuildConfig
import com.larmik.fridgealert.R
import com.larmik.fridgealert.common.callback.ListCallback
import com.larmik.fridgealert.common.callback.ProductCallback
import com.larmik.fridgealert.common.model.Product
import com.larmik.fridgealert.utils.*
import kotlinx.android.synthetic.main.update_product_fragment.view.*
import java.io.File
import java.io.IOException
import java.util.*

class UpdateProductFragment(private val product: Product, private val position: Int) :
    DialogFragment(), ProductCallback {

    var callback: ListCallback? = null
    var uri: Uri? = null
    var productImage: Bitmap? = null

    lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.update_product_fragment, container, false)
        rootView.name_et.setText(product.name)
        rootView.tvDate.text = product.expireDate.getDate()?.getString()
        rootView.update_iv.clipToOutline = true
        rootView.update_iv.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            var imageFile: File? = null
            try {
                imageFile = activity?.createImageFile()
            } catch (e: IOException) {

            }
            if (imageFile != null) {
                uri = FileProvider.getUriForFile(
                    context!!,
                    BuildConfig.APPLICATION_ID + ".provider",
                    imageFile
                )
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            }
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            val chooser = Intent.createChooser(galleryIntent, "Choisissez une source")
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))
            startActivityForResult(chooser, 123)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            rootView.edit_date.setOnClickListener {
                val dialogPicker =
                    DatePickerDialog(requireContext(), R.style.MyDatePickerDialogTheme)
                dialogPicker.datePicker.minDate = System.currentTimeMillis()
                val oldDate = Calendar.getInstance()
                oldDate.time = product.expireDate.getDate()!!
                dialogPicker.updateDate(
                    oldDate.get(Calendar.YEAR),
                    oldDate.get(Calendar.MONTH),
                    oldDate.get(Calendar.DAY_OF_MONTH)
                )
                dialogPicker.setOnDateSetListener { datePicker, i, i2, i3 ->
                    rootView.tvDate.text = datePicker.getExpireDate()
                    dialogPicker.dismiss()
                }
                dialogPicker.show()
            }
        }
        //TODO Replace with load task
        val image = ImageSaver(requireContext())
            .setFileName(product.fileName)
            .load()

        rootView.update_iv.load(image)
        rootView.validate_btn.setOnClickListener {
            val product = Product(
                rootView.name_et.text.toString(),
                rootView.tvDate.text.toString(),
                product.fileName,
                product.createdDate
            )
            product.id = this.product.id
            SaveTask(
                ProductAction.EDIT,
                requireContext(),
                product,
                productImage,
                this,
                position
            ).execute()
        }

        return rootView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 123) {
                val bitmap: Bitmap?
                try {
                    if (data != null && data.data != null)
                        uri = data.data
                    bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, uri)
                    productImage = bitmap?.rotateForGallery(context!!, uri!!)
                } catch (e: IOException) {
                }

                productImage?.let {
                    rootView.update_iv.load(it)
                }
            }
        }
    }

    override fun onProductAdded(product: Product) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProductEdited(product: Product, position: Int) {
        callback!!.onEditValidated(product, position)
        dismiss()
    }

}