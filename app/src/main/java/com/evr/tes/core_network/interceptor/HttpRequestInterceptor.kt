package com.evr.tes.core_network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that intercepts the HTTP requests and responses.
 */
internal class HttpRequestInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val response = chain.proceed(originalRequest)
        return response
    }
}