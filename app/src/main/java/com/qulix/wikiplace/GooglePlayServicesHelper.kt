package com.qulix.wikiplace

import android.app.Activity
import com.github.ajalt.timberkt.Timber
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability


object GooglePlayServicesHelper {

    private const val PLAY_SERVICES_RESOLUTION_REQUEST_CODE: Int = 1000

    fun isGooglePlayServicesAvailable(activity: Activity): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(activity.applicationContext)

        return (resultCode == ConnectionResult.SUCCESS).also { isAvailable ->
            if (!isAvailable) {
                if (apiAvailability.isUserResolvableError(resultCode)) {
                    apiAvailability.showErrorDialog(activity, resultCode)
                } else {
                    val errorString = apiAvailability.getErrorString(resultCode)
                    Timber.e { "This device does not support Google Play Services. GoogleApiAvailability code: $resultCode. $errorString" }
                }
            }
        }
    }


    private fun GoogleApiAvailability.showErrorDialog(activity: Activity, resultCode: Int) {
        getErrorDialog(activity,
                       resultCode,
                       PLAY_SERVICES_RESOLUTION_REQUEST_CODE)
            .show()
    }
}