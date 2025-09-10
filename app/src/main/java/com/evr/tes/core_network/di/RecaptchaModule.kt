package com.evr.tes.core_network.di

import com.evr.tes.core_network.recaptcha.RecaptchaService
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RecaptchaModule {

    private const val GOOGLE_RECAPTCHA_BASE_URL = "https://recaptchaenterprise.googleapis.com/v1/"
    private const val TIMEOUT_SECONDS = 30L
    
    @Provides
    @Singleton
    @Named("recaptcha")
    fun provideRecaptchaOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    @Named("recaptcha")
    fun provideRecaptchaRetrofit(
        @Named("recaptcha") okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(GOOGLE_RECAPTCHA_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setLenient()
                        .create()
                )
            )
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRecaptchaService(
        @Named("recaptcha") retrofit: Retrofit
    ): RecaptchaService {
        return retrofit.create(RecaptchaService::class.java)
    }
}