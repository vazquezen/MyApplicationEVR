package com.evr.tes.core_model.recaptcha

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Response structure from reCaptcha Enterprise API
 */
@Parcelize
data class RecaptchaEnterpriseResponse(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("event")
    val event: RecaptchaEventResponse,
    @SerializedName("riskAnalysis")
    val riskAnalysis: RiskAnalysis,
    @SerializedName("tokenProperties")
    val tokenProperties: TokenProperties
) : Parcelable

@Parcelize
data class RecaptchaEventResponse(
    @SerializedName("token")
    val token: String = "",
    @SerializedName("siteKey")
    val siteKey: String = "",
    @SerializedName("userAgent")
    val userAgent: String = "",
) : Parcelable

@Parcelize
data class RiskAnalysis(
    @SerializedName("score")
    val score: Float = 0.0f,
    @SerializedName("reasons")
    val reasons: List<String> = listOf()
) : Parcelable

@Parcelize
data class TokenProperties(
    @SerializedName("valid")
    val valid: Boolean = false,
    @SerializedName("invalidReason")
    val invalidReason: String = "",
    @SerializedName("hostname")
    val hostname: String = "",
    @SerializedName("action")
    val action: String = "",
    @SerializedName("createTime")
    val createTime: String = ""
) : Parcelable