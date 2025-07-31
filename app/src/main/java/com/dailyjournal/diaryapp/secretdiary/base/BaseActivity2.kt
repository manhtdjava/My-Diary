package com.dailyjournal.diaryapp.secretdiary.base

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewbinding.ViewBinding
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.dialog.PermissionDialog
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefUtils
import com.dailyjournal.diaryapp.secretdiary.utils.SystemUtil
import com.dailyjournal.diaryapp.secretdiary.widget.isNetworkAvailable
import com.dailyjournal.diaryapp.secretdiary.widget.showNoInternetAlert


abstract class BaseActivity2<T : ViewBinding> : AppCompatActivity() {
    private var _binding: T? = null
    lateinit var binding: T
    private var TAG = ""
    private var currentApiVersion = 0
    private var isServiceBound = false
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            isServiceBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isServiceBound = false
        }
    }

    // yêu cầu cấp quyền với ActivityResult API
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { callback ->
        if (callback.containsValue(false)) {
            onPermissionDenied()
        } else {
            onPermissionGranted()
        }
    }

    private val requestPermissionActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { callback ->
        if (callback.resultCode == RESULT_OK) onPermissionGranted()
        else onPermissionDenied()
    }

    protected abstract fun setViewBinding(): T
    protected abstract fun initView()
    protected abstract fun viewListener()
    protected abstract fun dataObservable()
    open fun onPermissionGranted() {}
    open fun onPermissionDenied() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        currentApiVersion = Build.VERSION.SDK_INT
//        val flags =
//            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
//        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {
//            window.decorView.systemUiVisibility = flags
//            val decorView = window.decorView
//            decorView.setOnSystemUiVisibilityChangeListener { visibility ->
//                if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
//                    decorView.systemUiVisibility = flags
//                }
//            }
//        }
        super.onCreate(savedInstanceState)
        try {
            SystemUtil.setLocale(this)
            _binding = setViewBinding()
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            TAG = "$localClassName check"
            _binding?.let {
                binding = it
                setContentView(binding.root)
                initView()
                dataObservable()
                viewListener()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

//    override fun onWindowFocusChanged(hasFocus: Boolean) {
//        super.onWindowFocusChanged(hasFocus)
//        if (currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus) {
//            window.decorView.systemUiVisibility =
//                (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
//        }
//    }

    override fun onResume() {
        super.onResume()
        if (!isNetworkAvailable()) {
            showNoInternetAlert(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            unbindService(connection)
            isServiceBound = false
        }
    }

    private fun fullScreen() {
        val windowInsetsController: WindowInsetsControllerCompat? =
            if (Build.VERSION.SDK_INT >= 30) {
                ViewCompat.getWindowInsetsController(window.decorView)
            } else {
                WindowInsetsControllerCompat(window, binding.root)
            }

        if (windowInsetsController == null) {
            return
        }
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
        windowInsetsController.hide(WindowInsetsCompat.Type.systemGestures())
    }

    fun showActivity(activity: Class<*>, bundle: Bundle?) {
        val intent = Intent(this, activity)
        intent.putExtras(bundle ?: Bundle())
        startActivity(intent)
    }

    fun showActivity(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }

    open fun showDialogPermission(permission: Array<String>) {
        for (per in permission) {
            if (!checkPermission(per)) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, per)) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts(
                        "package", applicationContext.packageName, null
                    )
                    intent.data = uri
                    val dialogPermission = PermissionDialog(this) {
                        requestPermissionActivity.launch(intent)
                    }
                    dialogPermission.show()
                } else {
                    requestPermissionLauncher.launch(permission)
                }
                return
            }
        }
        onPermissionGranted()
    }

    override fun onPause() {
        super.onPause()
    }

    protected open fun checkPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    //kiem tra nhieu quyen
    protected open fun checkPermission(permission: Array<String>): Boolean {
        for (permisson in permission) {
            val allow = ActivityCompat.checkSelfPermission(
                this, permisson
            ) == PackageManager.PERMISSION_GRANTED
            if (!allow) return false
        }
        return true
    }

    fun getBackground(type: Int): Int {
        when (type) {
            0 -> {
                return R.drawable.bg_theme_0
            }

            1 -> {
                return R.drawable.bg_theme_1
            }

            2 -> {
                return R.drawable.bg_theme_2
            }

            3 -> {
                return R.drawable.bg_theme_3
            }

            4 -> {
                return R.drawable.bg_theme_4
            }

            5 -> {
                return R.drawable.bg_theme_5
            }


        }
        return R.drawable.bg_theme_0
    }

}