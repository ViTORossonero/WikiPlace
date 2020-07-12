package com.qulix.wikiplace

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.ajalt.timberkt.Timber
import com.qulix.wikiplace.di.PerActivity
import com.qulix.wikiplace.presentation.common.BaseActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject

@PerActivity
class PermissionRequester @Inject constructor(
    private val activity: BaseActivity,
    private val dispatchersProvider: DispatchersProvider
) {

    enum class Permission(val asString: String) {
        FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    enum class Result { GRANTED, DENIED }

    fun isPermissionGranted(permission: Permission): Boolean = activity.isPermissionGranted(permission)

    suspend fun requestPermission(permission: Permission,
                                  requestCode: Int): Result = withContext(dispatchersProvider.main) {
        if (activity.isPermissionGranted(permission)) {
            Result.GRANTED
        } else {
            // TODO: implement show rationale logic.
            if (activity.shouldShowRationale(permission)) Timber.w { "We'd rather show rationale" }

            activity.requestPermission(permission, requestCode)
        }
    }

    companion object {
        private fun Activity.isPermissionGranted(permission: Permission): Boolean =
            ContextCompat.checkSelfPermission(this, permission.asString) == PackageManager.PERMISSION_GRANTED

        private fun Activity.shouldShowRationale(permission: Permission): Boolean =
            ActivityCompat.shouldShowRequestPermissionRationale(this, permission.asString)

        private suspend fun BaseActivity.requestPermission(permission: Permission,
                                                           requestCode: Int): Result = suspendCancellableCoroutine { continuation ->

            val listener = object : ActivityCompat.OnRequestPermissionsResultCallback {
                override fun onRequestPermissionsResult(code: Int,
                                                        permissions: Array<out String>,
                                                        grantResults: IntArray) {
                    if (code == requestCode) {
                        val result = if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED)
                            Result.GRANTED
                        else
                            Result.DENIED

                        removeOnRequestPermissionsResultListener(this)

                        continuation.resumeWith(kotlin.Result.success(result))
                    }
                }
            }

            continuation.invokeOnCancellation { removeOnRequestPermissionsResultListener(listener) }

            addOnRequestPermissionsResultListener(listener)

            ActivityCompat.requestPermissions(this, arrayOf(permission.asString), requestCode)
        }
    }
}