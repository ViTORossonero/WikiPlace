package com.qulix.wikiplace.presentation

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qulix.wikiplace.R
import com.qulix.wikiplace.presentation.WikiPlacesPresenter.View.State
import com.qulix.wikiplace.extensions.exhaustive
import com.qulix.wikiplace.extensions.visibleIf
import com.qulix.wikiplace.presentation.util.LCEHelper


class WikiPlacesViewImpl(root: ViewGroup) : WikiPlacesPresenter.View {

    init {
        View.inflate(root.context, R.layout.layout_places, root)
    }

    private val toolbar: Toolbar = root.findViewById(R.id.toolbar_places)
    private val emptyResultsTextView: TextView = root.findViewById(R.id.textview_places_empty)
    private val errorTextView: TextView = root.findViewById(R.id.textview_places_error)
    private val gettingLocationTextView: TextView = root.findViewById(R.id.textview_places_gettingLocation)
    private val recyclerView: RecyclerView = root.findViewById(R.id.recyclerview_places)

    private val placesAdapter = PlacesAdapter(initialItems = emptyList())

    private val lceHelper = LCEHelper.create(
            root,
            loadingViewId = R.id.framelayout_places_loading,
            contentViewId = R.id.framelayout_places_content,
            errorViewId = R.id.framelayout_places_error
    )

    init {
        toolbar.setTitle(R.string.places_title)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = placesAdapter
            setHasFixedSize(true)
        }
    }

    override fun renderState(state: State) {

        fun showError(failureReason: State.Failure.Reason) {
            lceHelper.showError()

            val errorStringRes = when (failureReason) {
                State.Failure.Reason.UNEXPECTED -> R.string.general_error
                State.Failure.Reason.NO_LOCATION_PERMISSION -> R.string.general_error_noLocationPermission
                State.Failure.Reason.NO_LOCATION_AVAILABLE -> R.string.general_error_noLocationAvailable
            }
            errorTextView.setText(errorStringRes)
        }

        when (state) {
            is State.Loading -> {
                lceHelper.showLoading()
                gettingLocationTextView.visibleIf(state.isWaitingForLocation)
            }
            is State.Failure -> showError(state.reason)
            is State.Success -> {
                lceHelper.showContent()

                placesAdapter.items = state.places

                val isPlacesEmpty = state.places.isEmpty()
                emptyResultsTextView.visibleIf(isPlacesEmpty)
                recyclerView.visibleIf(!isPlacesEmpty)
            }
        }.exhaustive
    }
}