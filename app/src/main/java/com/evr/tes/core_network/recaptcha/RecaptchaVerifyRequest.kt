package com.evr.tes.core_network.recaptcha

data class RecaptchaVerifyRequest(
    val token: String,
    val action: String
)