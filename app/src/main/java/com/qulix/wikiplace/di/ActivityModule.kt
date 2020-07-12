package com.qulix.wikiplace.di

import com.qulix.wikiplace.presentation.common.BaseActivity
import dagger.Module
import dagger.Provides


@Module
class ActivityModule(
    private val baseActivity: BaseActivity
) {

    @Provides
    @PerActivity
    internal fun provideBaseActivity(): BaseActivity = baseActivity
}