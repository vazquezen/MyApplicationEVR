package com.evr.tes.core_network.request

import com.evr.tes.core_network.interceptor.HttpRequestInterceptor
import com.google.gson.Gson
import okhttp3.OkHttpClient
import javax.inject.Inject

/**
 * Network client that provides the OkHttpClient and Gson instances.
 */
class NetworkClient @Inject constructor(
    private val client: OkHttpClient,
    private val gson: Gson
) {

    companion object {

        @Volatile
        private var instance: NetworkClient? = null

        fun instance(): NetworkClient = instance ?: synchronized(this) {
            instance ?: NetworkClient(
                OkHttpClient()
                    .newBuilder()
                    .followRedirects(false)
                    .addInterceptor(HttpRequestInterceptor())
                    .build(),
                Gson()
            ).also { instance = it }
        }
    }

    fun client(): OkHttpClient = client
    fun gson(): Gson = gson
}