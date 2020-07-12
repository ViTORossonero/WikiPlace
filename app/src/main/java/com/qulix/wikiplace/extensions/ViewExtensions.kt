package com.qulix.wikiplace.extensions

import android.view.View

inline fun View.visibleIf(visible: Boolean, visibilityWhenFalse: Int = View.GONE) {
    visibility = if (visible) View.VISIBLE else visibilityWhenFalse
}