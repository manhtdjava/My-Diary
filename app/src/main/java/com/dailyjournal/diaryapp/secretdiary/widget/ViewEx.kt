package com.dailyjournal.diaryapp.secretdiary.widget

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.ui.splash.SplashActivity


fun View.tap(action: (view: View?) -> Unit) {
    setOnClickListener {
        if (this.context.isNetworkAvailable()) {
            it.isEnabled = false
            it.postDelayed({ it.isEnabled = true }, 1500)
            action(it)
        } else {
            showNoInternetAlert(this.context)
        }
    }

}

fun View.tapPage(action: (view: View?) -> Unit) {
    setOnClickListener {
        if (this.context.isNetworkAvailable()) {
            it.isEnabled = false
            it.postDelayed({ it.isEnabled = true }, 200)
            action(it)
        } else {
            showNoInternetAlert(this.context)
        }
    }

}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnected
}

fun showNoInternetAlert(context: Context) {
    val dialog = Dialog(context)
    dialog.setContentView(R.layout.dialog_warning_network)

    val window = dialog.window
    if (window != null) {
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.show()

        val okButton = dialog.findViewById<TextView>(R.id.iv_ok_image)
        okButton.setOnClickListener {
            if (context.isNetworkAvailable()) {
                val intent = Intent(context, SplashActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                context.startActivity(intent)
                dialog.dismiss()
            } else {
                // Toast.makeText(context, "Vẫn chưa có kết nối mạng", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


