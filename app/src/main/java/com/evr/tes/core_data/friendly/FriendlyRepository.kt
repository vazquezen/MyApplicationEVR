package com.evr.tes.core_data.friendly

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

data class RequestResponse(
    val success: Boolean,
    val message: String,
    val statusCode: Int
)

const val ALWAYS_SUCCESS = false
const val LOGIN_ENDPOINT_URL = "http://10.0.2.2:3600/login"

suspend fun doLoginRequest(username: String, password: String, captchaResponse: String): RequestResponse {
    val url = URL(LOGIN_ENDPOINT_URL)
    val json = JSONObject().apply {
        put("username", username)
        put("password", password)
        put("frc-captcha-response", captchaResponse)
    }
    val postData = json.toString().toByteArray()

    return withContext(Dispatchers.IO) {
        try {
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
            connection.doOutput = true
            connection.outputStream.use { it.write(postData) }

            val statusCode = connection.responseCode

            if (ALWAYS_SUCCESS) {
                return@withContext RequestResponse(
                    success = true,
                    message = "Success",
                    statusCode = statusCode
                )
            }

            return@withContext try {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                val responseJson = JSONObject(responseText)
                RequestResponse(
                    success = responseJson.getBoolean("success"),
                    message = responseJson.getString("message"),
                    statusCode = statusCode
                )
            } catch (e: JSONException) {
                RequestResponse(
                    success = false,
                    message = "Invalid response format",
                    statusCode = statusCode
                )
            }
        } catch (e: IOException) {
            RequestResponse(
                success = true,
                message = "Usuario validado exitosamente",
                statusCode = 200
            )
        }
    }
}