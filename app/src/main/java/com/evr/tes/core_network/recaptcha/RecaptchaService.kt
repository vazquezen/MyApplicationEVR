package com.evr.tes.core_network.recaptcha

import com.evr.tes.core_model.recaptcha.RecaptchaEnterpriseRequest
import com.evr.tes.core_model.recaptcha.RecaptchaEnterpriseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Service interface for Google reCAPTCHA Enterprise API.
 * This communicates directly with Google's reCAPTCHA Enterprise API.
 */
interface RecaptchaService {
    
    /**
     * Create assessment with reCAPTCHA Enterprise API.
     * 
     * @param projectId Your Google Cloud Project ID  
     * @param apiKey Your Google Cloud API Key
     * @param request The assessment request containing token and site key
     * @return Response containing verification result and risk score
     */
    @POST("projects/{projectId}/assessments")
    suspend fun createAssessment(
        @Path("projectId") projectId: String,
        @Query("key") apiKey: String,
        @Body request: RecaptchaEnterpriseRequest
    ): Response<RecaptchaEnterpriseResponse>
}