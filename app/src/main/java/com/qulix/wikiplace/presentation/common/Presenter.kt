package com.qulix.wikiplace.presentation.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

abstract class Presenter<View : Any> : CoroutineScope {

    private lateinit var pendingJobs: Job

    override val coroutineContext: CoroutineContext
        get() = (Dispatchers.Main + pendingJobs)

    private var _view: View? = null

    protected val view: View
        get() = checkNotNull(_view) { "Attempt to access view after it was dropped" }

    abstract fun onTakeView()

    fun takeView(view: View) {
        _view = view
        pendingJobs = SupervisorJob()
        onTakeView()
    }

    protected open fun onDropView() = Unit

    fun dropView() {
        onDropView()
        _view = null
        pendingJobs.cancel()
    }

}