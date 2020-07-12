package com.qulix.wikiplace.presentation

import android.os.Bundle
import android.view.ViewGroup
import com.qulix.wikiplace.presentation.common.BaseActivity
import com.qulix.wikiplace.R
import com.qulix.wikiplace.di.ActivityComponent
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject lateinit var presenter: WikiPlacesPresenter

    private lateinit var placesView: WikiPlacesPresenter.View

    private lateinit var activityComponent: ActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activityComponent = ActivityComponent.create(this)
        activityComponent.inject(this)

        val root = findViewById<ViewGroup>(R.id.container)

        placesView = WikiPlacesViewImpl(root)
    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(placesView)
    }

    override fun onPause() {
        presenter.dropView()
        super.onPause()
    }
}
