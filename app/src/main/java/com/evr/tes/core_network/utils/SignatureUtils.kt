package com.evr.tes.core_network.utils

import android.util.Log
import com.evr.tes.core_model.request.RequestDto
import okhttp3.Headers.Companion.toHeaders
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.TreeMap


fun RequestDto.getSignedRequest(): Request {
    val url = getUrl()
    val headers = getHeader()
    val bodyRequest = when {
        sendBody() && fileByte != null -> fileByte?.toRequestBody()
        sendBody() -> parBody.toRequestBody()
        else -> null
    }
    return Request.Builder()
        .url(url)
        .method(method, body = bodyRequest)
        .headers(headers.toHeaders())
        .build()
}

private fun RequestDto.sendBody() =
    method == HttpMethods.POST || method == HttpMethods.PUT || method == HttpMethods.DELETE

private fun RequestDto.getUrl(): String {
    val urlBase = "https://test_api.com/"
    var url = "$urlBase$resource$instance$subResource"
    try {
        url = setParams(url)
    } catch (e: UnsupportedEncodingException) {
        Log.e("Request", e.message, e.cause)
    }

    return url
}

fun RequestDto.getHeader(): TreeMap<String, String> {
    return TreeMap<String, String>().apply {
        put("appkey", "ONE_KEY")
        put("appver", "1.0.0")
        put("date", "1989-10-02")
        put("Content-Type", "application/json")

        if (headers.isNotEmpty()) {
            putAll(headers)
        }
    }
}

private fun RequestDto.setParams(url: String): String {
    val queryParams = args.toSortedMap().map { (key, value) ->
        "${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value.toString(), "UTF-8")}"
    }.joinToString("&")
    return if (url.contains("?")) {
        if (queryParams.isEmpty()) {
            url
        } else {
            "$url&$queryParams"
        }
    } else {
        if (queryParams.isEmpty()) {
            url
        } else {
            "$url?$queryParams"
        }
    }
}