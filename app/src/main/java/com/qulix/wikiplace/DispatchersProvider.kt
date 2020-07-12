package com.qulix.wikiplace

import kotlinx.coroutines.CoroutineDispatcher

class DispatchersProvider(
    val main: CoroutineDispatcher,
    val default: CoroutineDispatcher,
    val io: CoroutineDispatcher
)