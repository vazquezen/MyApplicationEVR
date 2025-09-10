package com.evr.tes.core_network.recaptcha

import com.evr.tes.helpers.Keys
import com.google.gson.Gson
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RecaptchaRepository @Inject constructor(
    private val recaptchaService: RecaptchaService
) {
    
    companion object {
        private const val TAG = "RecaptchaRepository"
        
        // Score thresholds for decision making
        const val HIGH_TRUST_THRESHOLD = 0.7f
        const val LOW_TRUST_THRESHOLD = 0.3f
    }
    
    /**
     * Verifies a reCAPTCHA token with Google and returns the result.
     */
    suspend fun verifyToken(
        token: String, 
        expectedAction: String
    ): RecaptchaResult {
        return try {
            Timber.tag(TAG).d("Starting reCaptcha Enterprise v3 token verification...")
            Timber.tag(TAG).d("Token length: ${token.length}")
            Timber.tag(TAG).d("Token preview: ${token.take(50)}...")
            Timber.tag(TAG).d("Expected action: $expectedAction")
            Timber.tag(TAG).d("Site key: ${Keys.RECAPTCHA_API_APP_KEY}")
            Timber.tag(TAG).d("Project ID: ${Keys.GOOGLE_CLOUD_PROJECT_ID}")
            Timber.tag(TAG).d("API Key: ${Keys.GOOGLE_CLOUD_API_KEY.take(20)}...")
            Timber.tag(TAG).d("Using reCaptcha Enterprise API")

            val request = RecaptchaEnterpriseRequest(
                event = RecaptchaEvent(
                    token = token,
                    siteKey = Keys.RECAPTCHA_API_APP_KEY,
                    userIpAddress = getDeviceIpAddress(),
                    expectedAction = expectedAction
                )
            )
            
            val response = recaptchaService.createAssessment(
                projectId = Keys.GOOGLE_CLOUD_PROJECT_ID,
                apiKey = Keys.GOOGLE_CLOUD_API_KEY,
                request = request
            )

            Timber.tag(TAG).d("Response JSON: ${Gson().toJson(response.body())}")
            Timber.tag(TAG).d("Response code: ${response.code()}")
            Timber.tag(TAG).d("Response successful: ${response.isSuccessful}")
            
            if (response.isSuccessful) {
                val body = response.body()
                Timber.tag(TAG).d("Response body: $body")

                if (body != null) {
                    processEnterpriseResponse(body, expectedAction)
                } else {
                    Timber.tag(TAG).e("Empty response body from Google")
                    RecaptchaResult.Error("Empty response from Google")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Timber.tag(TAG).e("HTTP Error ${response.code()}: ${response.message()}")
                Timber.tag(TAG).e("Error body: $errorBody")
                RecaptchaResult.Error("HTTP ${response.code()}: ${response.message()}")
            }
            
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Network error during verification")
            RecaptchaResult.Error("Network error: ${e.message}")
        }
    }

    private fun processEnterpriseResponse(
        response: RecaptchaEnterpriseResponse,
        expectedAction: String
    ): RecaptchaResult {
        
        // Check if token is valid
        if (!response.tokenProperties.valid) {
            val errorMessage = response.tokenProperties.invalidReason ?: "Token invalid"
            Timber.tag(TAG).w("reCAPTCHA Enterprise token invalid: $errorMessage")
            return RecaptchaResult.Error("Token validation failed: $errorMessage")
        }
        
        // Check if action matches what we expected  
        if (response.tokenProperties.action != expectedAction) {
            Timber.tag(TAG).w("Action mismatch. Expected: $expectedAction, Got: ${response.tokenProperties.action}")
            return RecaptchaResult.Error("Action mismatch")
        }
        
        // Get risk score from Enterprise API
        val score = response.riskAnalysis.score
        
        // Evaluate score and return appropriate result
        return when {
            score >= HIGH_TRUST_THRESHOLD -> {
                Timber.tag(TAG).d("High trust score: $score")
                RecaptchaResult.Success(score, TrustLevel.HIGH)
            }
            
            score >= LOW_TRUST_THRESHOLD -> {
                Timber.tag(TAG).d("Medium trust score: $score")
                RecaptchaResult.Success(score, TrustLevel.MEDIUM)
            }
            
            else -> {
                Timber.tag(TAG).w("Low trust score: $score")
                RecaptchaResult.Success(score, TrustLevel.LOW)
            }
        }
    }

    fun getDeviceIpAddress(): String {
        val interfaces = java.net.NetworkInterface.getNetworkInterfaces()

        for (networkInterface in interfaces) {
            val addresses = networkInterface.inetAddresses

            for (address in addresses) {

                if (!address.isLoopbackAddress && address is java.net.Inet4Address) {
                    return address.hostAddress
                }
            }
        }

        return "00.00.00.00"
    }
}

/**
 * Result of reCAPTCHA verification
 */
sealed class RecaptchaResult {
    data class Success(val score: Float, val trustLevel: TrustLevel) : RecaptchaResult()
    data class Error(val message: String) : RecaptchaResult()
}

/**
 * Trust level based on reCAPTCHA score
 */
enum class TrustLevel {
    HIGH,    // Score >= 0.7 - Very likely human
    MEDIUM,  // Score 0.3-0.7 - Uncertain, might need additional verification
    LOW      // Score < 0.3 - Very likely bot
}