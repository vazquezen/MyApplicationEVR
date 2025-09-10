package com.evr.tes.core_network.recaptcha

import com.google.gson.annotations.SerializedName

/**
 * Response structure from reCaptcha Enterprise API
 */
data class RecaptchaEnterpriseResponse(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("event")
    val event: RecaptchaEventResponse,
    
    @SerializedName("riskAnalysis")
    val riskAnalysis: RiskAnalysis,
    
    @SerializedName("tokenProperties")
    val tokenProperties: TokenProperties
)

data class RecaptchaEventResponse(
    @SerializedName("token")
    val token: String,
    
    @SerializedName("siteKey")
    val siteKey: String,
    
    @SerializedName("userAgent")
    val userAgent: String
)

data class RiskAnalysis(
    @SerializedName("score")
    val score: Float,
    
    @SerializedName("reasons")
    val reasons: List<String>? = null
)

data class TokenProperties(
    @SerializedName("valid")
    val valid: Boolean,
    
    @SerializedName("invalidReason")
    val invalidReason: String? = null,
    
    @SerializedName("hostname")
    val hostname: String,
    
    @SerializedName("action")
    val action: String,
    
    @SerializedName("createTime")
    val createTime: String
)