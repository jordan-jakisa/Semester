package com.bawano.semester.utils

import android.R
import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.lang.reflect.Field
import java.util.*


const val RECORD_REQUEST = 1

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("LAST_PAGE")

fun Context.toast(text: CharSequence, duration: Int = Toast.LENGTH_LONG) =
    Toast.makeText(this, text, duration).show()


fun Context.observeConnection(networkCallback: ConnectivityManager.NetworkCallback) {
    val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    val connectivityManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
    } else {
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
    connectivityManager.requestNetwork(networkRequest, networkCallback)
}

fun Context.errorDialog(title: String, message: String) =
    AlertDialog.Builder(this)
        .setCancelable(true)
        .setPositiveButton("dismiss") { dialog, _ ->
            dialog.dismiss()
        }
        .setTitle(title).setMessage(message)
        .create()
        .show()

fun String.onlyFirstLetters(): String{
    val words = this.split(' ')
    var s = ""
    for (w in words ) s += w.first()
    return s.uppercase(Locale.getDefault())
}

fun String.letterDrawable() =
    TextDraw(this, color = ColorGenerator.MATERIAL.getColor(this))



fun View.fadeIn(duration: Long = 400L, delay: Long = 0L) {
    this.visibility = View.VISIBLE
    this.alpha = 0f
    ViewCompat.animate(this).alpha(1f).setDuration(duration).setStartDelay(delay).start()
}

fun View.fadeOut(duration: Long = 1000L, delay: Long = 0) {
    this.alpha = 1f
    ViewCompat.animate(this).alpha(0f).setStartDelay(delay).setDuration(duration)
        .setListener(object :
            ViewPropertyAnimatorListener {
            override fun onAnimationStart(view: View) {
                @Suppress("DEPRECATION")
                view.isDrawingCacheEnabled = true
            }

            override fun onAnimationEnd(view: View) {
                view.visibility = View.GONE
                view.alpha = 0f
                @Suppress("DEPRECATION")
                view.isDrawingCacheEnabled = false
            }

            override fun onAnimationCancel(view: View) {}
        })
}

fun Context.getPdfImageFile(name: String): File? {
    val folder = File(this.cacheDir, "/PDF")
    if (folder.exists()) {
        val ff = File(folder, "/images")
        if (ff.exists()) return File(ff, "$name.jpg")
    }
    return null
}

fun TextView.addLinksToText(vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
                // use this to change the link color
                textPaint.color = textPaint.linkColor
                // toggle below value to enable/disable
                // the underline shown below the clickable text
                textPaint.isUnderlineText = true
            }

            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
//      if(startIndexOfLink == -1) continue // todo if you want to verify your texts contains links text
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod =
        LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    this.setText(spannableString, TextView.BufferType.SPANNABLE)


}

fun View.slideInFromLeft(duration: Long = 1000L, delay: Long = 0L) {
    this.visibility = View.VISIBLE
    this.translationX = 800F
    this.alpha = 0F
    this.animate().translationX(0F).alpha(1F).setDuration(duration).setStartDelay(delay).start()
}

fun View.slideInFromDown(duration: Long = 1000L, delay: Long = 0L) {
    this.visibility = View.VISIBLE
    this.translationY = 800F
    this.alpha = 0F
    this.animate().translationY(0F).alpha(1F).setDuration(duration).setStartDelay(delay).start()
}

fun View.pulsate(scope: CoroutineScope) {
    scope.launch {
        var t = true
        this@pulsate.post {
            this@pulsate.animate().alpha(if (t) 1f else 0.01f).setDuration(1500)
                .start()
        }
        t = !t
    }
    // TODO: finish this
}


fun View.translateYFromTo(
    duration: Long = 1000L,
    delay: Long = 0L,
    from: Float,
    to: Float,
    alpha: Float = 0f
) {
    this.visibility = View.VISIBLE
    this.translationY = from
    this.alpha = alpha
    this.animate().alpha(1F).translationY(to).setDuration(duration).setStartDelay(delay).start()
}
