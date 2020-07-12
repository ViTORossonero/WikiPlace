package com.qulix.wikiplace.extensions

import retrofit2.Retrofit

inline fun <reified T : Any> Retrofit.create(): T = create(T::class.java)