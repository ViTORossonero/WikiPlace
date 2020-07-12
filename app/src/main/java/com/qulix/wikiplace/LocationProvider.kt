package com.qulix.wikiplace

import android.location.Location
import android.os.Looper
import com.github.ajalt.timberkt.Timber
import com.google.android.gms.location.*
import com.qulix.wikiplace.di.PerActivity
import com.qulix.wikiplace.presentation.common.BaseActivity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowViaChannel
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

@PerActivity
class LocationProvider @Inject constructor(
    private val activity: BaseActivity,
    private val fusedLocationClient: FusedLocationProviderClient
) {

    sealed class Result {
        data class LastKnowLocation(val location: Location?) : Result()
        data class LocationAvailabilityChange(val isLocationAvailable: Boolean) : Result()
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    fun getLocations(): Flow<Result> = flowViaChannel { channel ->

        val locationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                channel.offer(Result.LastKnowLocation(locationResult.lastLocation))
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                if (!locationAvailability.isLocationAvailable) {
                    Timber.i { "LocationCallback: Location is not available." }
                }
                channel.offer(Result.LocationAvailabilityChange(locationAvailability.isLocationAvailable))
            }
        }

        channel.invokeOnClose { fusedLocationClient.removeLocationUpdates(locationCallback) }

        try {

            /*
            // We can start with the last known location from the device (instant result almost always).
            fusedLocationClient.lastLocation.addOnSuccessListener { location -> continuation.resume(location) }
            */

            fusedLocationClient.requestLocationUpdates(LOCATION_REQUEST, locationCallback, Looper.getMainLooper())
        } catch (securityException: SecurityException) {
            Timber.e(securityException) { "Location permission need to be granted before registering location updates listener." }
            channel.close(cause = CancellationException("Missing permission", securityException))
        }
    }

    suspend fun getLastKnownLocation(): Location? {

        if (!GooglePlayServicesHelper.isGooglePlayServicesAvailable(activity)) return null

        return suspendCancellableCoroutine { continuation ->

            val locationCallback: LocationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let {
                        fusedLocationClient.removeLocationUpdates(this)
                        continuation.resume(it)
                    }
                }

                override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                    if (!locationAvailability.isLocationAvailable) {
                        Timber.i { "LocationCallback: Location is not available." }
                    }
                }
            }

            continuation.invokeOnCancellation { fusedLocationClient.removeLocationUpdates(locationCallback) }

            try {

                /*
                // We can start with the last known location from the device (instant result almost always).
                fusedLocationClient.lastLocation.addOnSuccessListener { location -> continuation.resume(location) }
                */

                fusedLocationClient.requestLocationUpdates(LOCATION_REQUEST, locationCallback, Looper.getMainLooper())
            } catch (securityException: SecurityException) {
                Timber.e(securityException) { "Location permission need to be granted before registering location updates listener." }
            }
        }
    }

    companion object {
        private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10_000L

        private val LOCATION_REQUEST = LocationRequest().apply {
            interval = UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval = UPDATE_INTERVAL_IN_MILLISECONDS / 2
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }
}