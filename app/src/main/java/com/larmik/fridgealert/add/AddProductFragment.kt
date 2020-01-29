package com.larmik.fridgealert.add

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.FileProvider
import coil.api.load
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.larmik.fridgealert.BuildConfig
import com.larmik.fridgealert.R
import com.larmik.fridgealert.common.callback.ProductCallback
import com.larmik.fridgealert.common.model.Product
import com.larmik.fridgealert.common.view.ProgressDialog
import com.larmik.fridgealert.utils.*
import kotlinx.android.synthetic.main.fragment_add_product.view.*
import java.io.File
import java.io.IOException
import java.util.*

class AddProductFragment : BottomSheetDialogFragment(), ProductCallback {

    lateinit var rootView: View
    lateinit var progressDialog: ProgressDialog
    var callback: ProductCallback? = null
    var year = 0
    var month = 0
    var day = 0
    var uri: Uri? = null
    var productImage: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_add_product, container, false)
        setCurrentDateOnView()
        progressDialog = ProgressDialog(requireContext())
        rootView.dpResult.minDate = System.currentTimeMillis()
        rootView.add_image_iv.clipToOutline = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            rootView.dpResult.setOnDateChangedListener { _, i, i2, i3 ->
                year = i
                month = i2
                day = i3

                rootView.tvDate.text = rootView.dpResult.getExpireDate()
            }
        }
        rootView.add_image_iv.setOnClickListener {
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

        rootView.validate_btn.setOnClickListener {
            var name = "Non renseigné"
            val fileName = "${System.currentTimeMillis()}.png"
            if (rootView.name_et.text.toString().isNotEmpty())
                name = rootView.name_et.text.toString()

            progressDialog.show()
            val product = Product(name, rootView.dpResult.getExpireDate(), fileName, "")
            SaveTask(
                ProductAction.ADD,
                requireContext(),
                product,
                productImage,
                this
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
                if (productImage != null) {
                    rootView.add_image_iv.load(productImage)
                    rootView.add_tv.visibility = View.INVISIBLE
                    rootView.ic_plus.visibility = View.INVISIBLE
                }
            }
        }
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
        rootView.dpResult.init(year, month, day, null)
        rootView.tvDate.text = rootView.dpResult.getExpireDate()
    }

    override fun onProductAdded(product: Product) {
        progressDialog.hide()
        dismiss()
        callback?.onProductAdded(product)
    }

    override fun onProductEdited(product: Product, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}