package com.evr.tes.core_data.recaptcha

import android.app.Application
import android.content.Context
import com.google.android.recaptcha.Recaptcha
import com.google.android.recaptcha.RecaptchaAction
import com.google.android.recaptcha.RecaptchaClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object RecaptchaRepository {

    private lateinit var recaptchaClient: RecaptchaClient

    const val SITE_KEY = "6LcyYacrAAAAABbe4ljbNKAl63JxLrX8UV0L_e3O"

    fun initializeClient(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            recaptchaClient = Recaptcha.fetchClient(context.applicationContext as Application, SITE_KEY)
        }
    }

    suspend fun retrieveToken(action: RecaptchaAction): Result<String> {
        return recaptchaClient.execute(action)
    }
}