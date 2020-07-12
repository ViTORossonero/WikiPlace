package com.qulix.wikiplace.presentation.util

import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import com.qulix.wikiplace.extensions.visibleIf


class LCEHelper(private val loadingView: View,
                private val contentView: View,
                private val errorView: View) {

    private val views = listOf(loadingView, contentView, errorView)

    private fun show(viewToShow: View) {
        require(viewToShow in views)

        views.forEach { view -> view.visibleIf(view == viewToShow) }
    }

    fun showLoading() {
        show(loadingView)
    }

    fun showContent() {
        show(contentView)
    }

    fun showError() {
        show(errorView)
    }

    companion object {
        fun create(container: ViewGroup,
                   @IdRes loadingViewId: Int,
                   @IdRes contentViewId: Int,
                   @IdRes errorViewId: Int): LCEHelper =
            LCEHelper(
                container.findViewById(loadingViewId),
                container.findViewById(contentViewId),
                container.findViewById(errorViewId)
            )
    }
}