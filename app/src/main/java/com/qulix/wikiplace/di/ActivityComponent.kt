package com.qulix.wikiplace.di

import com.qulix.wikiplace.presentation.common.BaseActivity
import com.qulix.wikiplace.presentation.MainActivity
import dagger.Subcomponent

@PerActivity
@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {

    fun inject(activity: MainActivity)

    companion object {
        fun create(baseActivity: BaseActivity): ActivityComponent =
                AppComponent.from(baseActivity).plus(ActivityModule(baseActivity))
    }

}