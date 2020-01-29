package com.larmik.fridgealert.common.ui

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.larmik.fridgealert.R
import com.larmik.fridgealert.TestService
import com.larmik.fridgealert.TestService.RunServiceBinder
import com.larmik.fridgealert.add.AddProductFragment
import com.larmik.fridgealert.common.callback.NavigationCallback
import com.larmik.fridgealert.common.callback.ProductCallback
import com.larmik.fridgealert.common.model.Product
import com.larmik.fridgealert.common.view.ProgressDialog
import com.larmik.fridgealert.home.HomeFragment
import com.larmik.fridgealert.list.adapter.ProductListAdapter
import com.larmik.fridgealert.list.ui.ProductListFragment
import com.larmik.fridgealert.utils.FragmentToShow
import com.larmik.fridgealert.utils.addProduct
import com.larmik.fridgealert.utils.loadProducts
import com.larmik.fridgealert.utils.updateProduct
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), NavigationCallback, ProductCallback {

    private var fragmentToShow = FragmentToShow.HOME
    lateinit var notificationManager : NotificationManager
    lateinit var progressDialog: ProgressDialog
    private var fragment: Fragment? = null
    private var adapter: ProductListAdapter? = null

    var mServiceBound = false
    var mService = TestService()
    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as RunServiceBinder
            mService = binder.service
            mServiceBound = true
            mService.background()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mService.context = this
        adapter = ProductListAdapter(this)
        progressDialog = ProgressDialog(this)
        mService.startTimer(true)
        if (intent.hasExtra(Settings.EXTRA_CHANNEL_ID)) {
            val intent = Intent()
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("app_package", packageName)
            intent.putExtra("app_uid", applicationInfo.uid)
            intent.putExtra("android.provider.extra.APP_PACKAGE", packageName)
            startActivity(intent)
        }
        add_product_btn.setOnClickListener {
            onNavigation(FragmentToShow.ADD_PRODUCT)
        }
        bottombar.callback = this

        showFragment(fragmentToShow)
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mService.foreground()
        }
    }

    override fun onStart() {
        super.onStart()
        val i = Intent(this, TestService::class.java)
        startService(i)
        bindService(i, mConnection, 0)
        mServiceBound = true
    }

    private fun showFragment(fragmentToShow: FragmentToShow) {
        if (fragmentToShow == FragmentToShow.ADD_PRODUCT)
            showAddProductFragment()
        else {
            fragment = when (fragmentToShow) {
                FragmentToShow.HOME -> showHomeFragment()
                else -> showProductListFragment()
            }
            fragment?.let {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.container, it, it.tag)
                transaction.commitAllowingStateLoss()
            }

        }
    }

    private fun showHomeFragment(): Fragment {
        appbar_title.text = "Accueil"
        fragmentToShow = FragmentToShow.HOME
        var fragment: HomeFragment? =
            supportFragmentManager.findFragmentByTag(HomeFragment().tag) as? HomeFragment
        if (fragment == null)
            fragment = HomeFragment()
        return fragment
    }

    private fun showProductListFragment(): Fragment {
        appbar_title.text = "Mes produits"
        fragmentToShow = FragmentToShow.PRODUCT_LIST
        var fragment: ProductListFragment? =
            supportFragmentManager.findFragmentByTag(ProductListFragment().tag) as? ProductListFragment
        if (fragment == null)
            fragment = ProductListFragment()
        fragment.callback = this
        fragment.products = loadProducts()
        return fragment
    }

    private fun showAddProductFragment() {
        var fragment: AddProductFragment? =
            supportFragmentManager.findFragmentByTag(AddProductFragment().tag) as? AddProductFragment
        if (fragment == null)
            fragment = AddProductFragment()
        fragment.callback = this
        if (!fragment.isAdded)
            fragment.show(supportFragmentManager, AddProductFragment().tag)
    }

    override fun onNavigation(fragmentToShow: FragmentToShow) {
        showFragment(fragmentToShow)
    }

    override fun onProductAdded(product: Product) {
        Toast.makeText(this, "Produit ajouté au frigo", Toast.LENGTH_SHORT).show()
        addProduct(product)
        if (fragmentToShow == FragmentToShow.PRODUCT_LIST) {
            (fragment as ProductListFragment).adapter?.addItem(product)
        }
    }

    override fun onProductEdited(product: Product, position: Int) {
        Toast.makeText(this, "Produit modifié avec succès", Toast.LENGTH_SHORT).show()
        updateProduct(product)
    }

}
