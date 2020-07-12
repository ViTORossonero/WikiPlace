package com.qulix.wikiplace.extensions

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

@FlowPreview
fun <T> Flow<T>.takeUntil(predicate: suspend (T) -> Boolean): Flow<T> = flow {
    try {
        collect { value ->
            emit(value)
            if (predicate(value)) throw TakeLimitException()
        }
    } catch (e: TakeLimitException) {
        // Nothing, bail out
    }
}

class TakeLimitException : CancellationException("Flow limit is reached, cancelling") {
    // TODO expect/actual
    // override fun fillInStackTrace(): Throwable = this
}