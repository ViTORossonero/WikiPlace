package com.qulix.wikiplace.presentation.common

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.github.ajalt.timberkt.Timber

abstract class BaseActivity : AppCompatActivity() {

    private val onRequestPermissionsResultListeners = mutableSetOf<ActivityCompat.OnRequestPermissionsResultCallback>()

    fun addOnRequestPermissionsResultListener(listener: ActivityCompat.OnRequestPermissionsResultCallback) {
        onRequestPermissionsResultListeners += listener
    }

    fun removeOnRequestPermissionsResultListener(listener: ActivityCompat.OnRequestPermissionsResultCallback) {
        onRequestPermissionsResultListeners -= listener
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResultListeners.forEach { listener ->
            listener.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (onRequestPermissionsResultListeners.isNotEmpty()) {
            Timber.e { "Activity is about to be destroyed but onRequestPermissionsResultListeners is not empty" }
        }
    }
}