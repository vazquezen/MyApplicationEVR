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
    @SerializedName("userIpAddress")
    val userIpAddress: String = "",
    @SerializedName("expectedAction")
    val expectedAction: String = ""
)