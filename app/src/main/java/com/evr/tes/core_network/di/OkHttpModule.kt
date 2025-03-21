package com.evr.tes.core_network.di

import com.evr.tes.core_network.interceptor.HttpRequestInterceptor
import com.evr.tes.core_network.request.NetworkClient
import com.evr.tes.core_network.utils.Constant
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Module that provides the and Retrofit client for the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object OkHttpModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideNetworkClient(
        okHttpClientFactory: OkHttpClientFactory
    ): NetworkClient {
        return NetworkClient(
            okHttpClientFactory.create().build(),
            provideGson()
        )
    }

    @AssistedFactory
    fun interface OkHttpClientFactory {
        fun create(): OkHttpClientBuilder
    }

    open class OkHttpClientBuilder @AssistedInject constructor(
    ) {
        fun build(): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(HttpRequestInterceptor())
                .connectTimeout(Constant.TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .readTimeout(Constant.TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .writeTimeout(Constant.TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .build()
        }
    }

    @AssistedFactory
    fun interface RetrofitFactory {
        fun create(baseUrl: String): RetrofitBuilder
    }

    open class RetrofitBuilder @AssistedInject constructor(
        @Assisted val baseUrl: String,
        val okHttpClientFactory: OkHttpClientFactory
    ) {
        inline fun <reified T> build(): T {
            val gson = GsonBuilder()
                .setLenient()
                .create()
            return Retrofit.Builder()
                .client(okHttpClientFactory.create().build())
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create()
        }
    }
}