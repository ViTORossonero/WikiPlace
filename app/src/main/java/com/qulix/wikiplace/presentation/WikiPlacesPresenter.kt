package com.qulix.wikiplace.presentation

import android.location.Location
import com.github.ajalt.timberkt.Timber
import com.qulix.wikiplace.LocationProvider
import com.qulix.wikiplace.LocationProvider.Result
import com.qulix.wikiplace.PermissionRequester
import com.qulix.wikiplace.PermissionRequester.Result.DENIED
import com.qulix.wikiplace.PermissionRequester.Result.GRANTED
import com.qulix.wikiplace.presentation.WikiPlacesPresenter.View.State.Failure.Reason
import com.qulix.wikiplace.extensions.exhaustive
import com.qulix.wikiplace.extensions.takeUntil
import com.qulix.wikiplace.data.WikiPlacesRepo
import com.qulix.wikiplace.domain.entity.WikiPlace
import com.qulix.wikiplace.presentation.common.Presenter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class WikiPlacesPresenter @Inject constructor(
    private val permissionRequester: PermissionRequester,
    private val locationProvider: LocationProvider,
    private val wikiPlacesRepo: WikiPlacesRepo
) : Presenter<WikiPlacesPresenter.View>() {

    private var isLocationPermissionRequested: Boolean = false

    private val isLocationPermissionGranted: Boolean
        get() = permissionRequester.isPermissionGranted(PermissionRequester.Permission.FINE_LOCATION)

    override fun onTakeView() {
        launch {

            view.renderState(View.State.Loading())

            try {
                when (requestLocationPermission()) {
                    DENIED -> view.renderState(View.State.Failure(Reason.NO_LOCATION_PERMISSION))
                    GRANTED -> {
                        view.renderState(View.State.Loading(isWaitingForLocation = true))

                        locationProvider.getLocations()
                            .takeUntil { it is Result.LastKnowLocation && it.location != null }
                            .collect { result ->
                                when (result) {
                                    is Result.LastKnowLocation ->
                                        if (result.location != null) {
                                            view.renderState(View.State.Loading(isWaitingForLocation = false))
                                            view.renderState(loadPlaces(result.location))
                                        } else {
                                            Timber.w { "Last known location is null" }
                                        }
                                    is Result.LocationAvailabilityChange -> {
                                        val state = if (result.isLocationAvailable) {
                                            View.State.Loading(isWaitingForLocation = true)
                                        } else {
                                            View.State.Failure(Reason.NO_LOCATION_AVAILABLE)
                                        }
                                        view.renderState(state)
                                    }
                                }.exhaustive
                            }
                    }
                }.exhaustive
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    Timber.e(e)
                    view.renderState(View.State.Failure(Reason.UNEXPECTED))
                }
            }
        }
    }

    private suspend fun requestLocationPermission(): PermissionRequester.Result = when {
        isLocationPermissionGranted -> GRANTED
        isLocationPermissionRequested -> DENIED
        else -> {
            isLocationPermissionRequested = true
            permissionRequester.requestPermission(PermissionRequester.Permission.FINE_LOCATION,
                                                  REQ_CODE_FINE_LOCATION)
        }
    }

    private suspend fun loadPlaces(location: Location): View.State = try {
        val nearbyPlaces = wikiPlacesRepo.getNearbyPlaces(location)
        View.State.Success(nearbyPlaces)
    } catch (e: Exception) {
        Timber.e(e) { "Failed to load places" }
        View.State.Failure(Reason.UNEXPECTED)
    }

    interface View {
        sealed class State {
            data class Loading(val isWaitingForLocation: Boolean = false) : State()
            data class Success(val places: List<WikiPlace>) : State()
            data class Failure(val reason: Reason) : State() {
                enum class Reason { NO_LOCATION_PERMISSION, NO_LOCATION_AVAILABLE, UNEXPECTED }
            }
        }

        fun renderState(state: State)
    }

    companion object {
        private const val REQ_CODE_FINE_LOCATION = 123
    }
}