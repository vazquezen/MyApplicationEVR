package com.evr.tes.core_network.recaptcha

import com.google.gson.annotations.SerializedName

data class RecaptchaVerifyResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("score")
    val score: Float,
    
    @SerializedName("action")
    val action: String,
    
    @SerializedName("challenge_ts")
    val challengeTs: String,
    
    @SerializedName("hostname")
    val hostname: String,
    
    @SerializedName("error-codes")
    val errorCodes: List<String>? = null
)