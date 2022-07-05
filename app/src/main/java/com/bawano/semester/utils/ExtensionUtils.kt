package com.bawano.semester.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore


const val RECORD_REQUEST = 1
val IS_OKAY_KEY = booleanPreferencesKey("is_okay")

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("APP_PREFERENCES")

fun Context.toast(text: CharSequence, duration: Int = Toast.LENGTH_LONG) =
    Toast.makeText(this, text, duration).show()


fun Context.isConnected(): Boolean {
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
    return caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
}

fun String.removeSpaces() = this.filter { !it.isWhitespace() }


fun Activity.requestAudioPermission() = ActivityCompat.requestPermissions(
    this,
    Array(1) { Manifest.permission.RECORD_AUDIO },
    RECORD_REQUEST
)

fun Activity.hasAudioPermission(): Boolean =
    ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED

fun View.fadeIn(duration: Long) {
    this.visibility = View.VISIBLE
    this.alpha = 0f
    ViewCompat.animate(this).alpha(1f).setDuration(duration).setListener(object : ViewPropertyAnimatorListener {
        override fun onAnimationStart(view: View) {
        }

        override fun onAnimationEnd(view: View) {
        }

        override fun onAnimationCancel(view: View) {}
    }).start()
}

fun View.fadeOut(duration: Long, delay: Long = 0) {
    this.alpha = 1f
    ViewCompat.animate(this).alpha(0f).setStartDelay(delay).setDuration(duration).setListener(object :
        ViewPropertyAnimatorListener {
        override fun onAnimationStart(view: View) {
            @Suppress("DEPRECATION")
            view.isDrawingCacheEnabled = true
        }

        override fun onAnimationEnd(view: View) {
            view.visibility = View.INVISIBLE
            view.alpha = 0f
            @Suppress("DEPRECATION")
            view.isDrawingCacheEnabled = false
        }

        override fun onAnimationCancel(view: View) {}
    })
}

