package com.evr.tes.core_network.recaptcha

import com.google.gson.annotations.SerializedName


data class RecaptchaEnterpriseRequest(
    @SerializedName("event")
    val event: RecaptchaEvent
)

data class RecaptchaEvent(
    @SerializedName("token")
    val token: String = "",
    @SerializedName("siteKey")
    val siteKey: String = "",
    @SerializedName("userAgent")
    val userAgent: String = "",
    @SerializedName("userIpAddress")
    val userIpAddress: String = "",
    @SerializedName("ja3")
    val ja3: String = "JA3",
    @SerializedName("expectedAction")
    val expectedAction: String = ""
)