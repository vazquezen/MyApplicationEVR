package com.evr.tes.ui.captchascreen

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evr.tes.App
import com.evr.tes.core_data.recaptcha.RecaptchaRepository
import com.evr.tes.core_data.recaptcha.RecaptchaResult
import com.evr.tes.core_data.recaptcha.TrustLevel
import com.evr.tes.helpers.Keys
import com.google.android.recaptcha.Recaptcha
import com.google.android.recaptcha.RecaptchaAction
import com.google.android.recaptcha.RecaptchaClient
import com.google.android.recaptcha.RecaptchaException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CaptchaScreenViewModel @Inject constructor(
    private val recaptchaRepository: RecaptchaRepository
) : ViewModel() {

    private val tag = "CaptchaScreenViewModel"
    private lateinit var recaptchaClient: RecaptchaClient

    private val _captchaState: MutableStateFlow<CaptchaState> = MutableStateFlow(CaptchaState.Nonce)
    val captchaState: StateFlow<CaptchaState> = _captchaState

    private val _token = MutableStateFlow("")

    init {
        initializeRecaptchaClient()
    }

    fun initializeRecaptchaClient() = viewModelScope.launch {
        try {
            Timber.tag(tag).d("Initializing reCaptcha v3 client with site key: ${Keys.RECAPTCHA_API_APP_KEY}")
            Timber.tag(tag).d("Using Google reCaptcha v3 Android SDK")
            _captchaState.value = CaptchaState.Loading
            
            recaptchaClient = Recaptcha.fetchClient(
                application = App.instance,
                siteKey = Keys.RECAPTCHA_API_APP_KEY
            )
            Timber.tag(tag).d("reCaptcha v3 client initialized successfully")
            _captchaState.value = CaptchaState.Nonce

        } catch(e: RecaptchaException) {
            val errorMessage = "Recaptcha client initialization failed: ${e.errorCode.errorMessage}"
            _captchaState.value = CaptchaState.Error(message = errorMessage)
            Timber.tag(tag).e("Error inicializando reCaptcha: ${e.errorCode.errorMessage}")
            Timber.tag(tag).e("RecaptchaException details: ${e.message}")
            Timber.tag(tag).e("Error code: ${e.errorCode}")

            if (e.errorCode.errorMessage.contains("Key type invalid", ignoreCase = true)) {
                Timber.tag(tag).w("Las keys de testing no funcionan con reCaptcha v3. Necesitas keys reales de Google Cloud Console.")
                
                // En modo demo, simular éxito para demostrar la implementación
                if (Keys.DEMO_MODE) {
                    Timber.tag(tag).i("DEMO MODE: Simulando inicialización exitosa")
                    _captchaState.value = CaptchaState.Nonce
                    return@launch
                }
            }
        } catch(e: Exception) {
            val errorMessage = "Unexpected error during initialization: ${e.message}"
            _captchaState.value = CaptchaState.Error(message = errorMessage)
            Timber.tag(tag).e(e, "Unexpected error: ${e.message}")
        }
    }

    fun getToken() = viewModelScope.launch {
        _captchaState.value = CaptchaState.Loading
        
        try {
            if (!::recaptchaClient.isInitialized) {

                if (Keys.DEMO_MODE) {
                    Timber.tag(tag).i("DEMO MODE: Simulando token y verificación exitosa")
                    simulateDemoSuccess()
                    return@launch
                }
                _captchaState.value = CaptchaState.Error("Cliente reCaptcha no inicializado")
                return@launch
            }
            
            val action = "login"
            recaptchaClient
                .execute(RecaptchaAction.custom(action), timeout = 10000L)
                .onSuccess { token ->
                    Timber.tag(tag).d("Recaptcha token generated: ${token.take(20)}...")
                    _token.value = token
                    
                    // Now verify the token with Google's servers
                    verifyTokenWithGoogle(token, action)
                }.onFailure { exception ->
                    Timber.tag(tag).e("Recaptcha token generation failed: ${exception.message}")
                    _captchaState.value = CaptchaState.Error(exception.message ?: "Error generando token")
                }
        } catch (e: Exception) {
            Timber.tag(tag).e("Exception en getToken: ${e.message}")
            _captchaState.value = CaptchaState.Error(e.message ?: "Error desconocido")
        }
    }
    
    @SuppressLint("DefaultLocale")
    private suspend fun verifyTokenWithGoogle(token: String, action: String) {
        try {
            Timber.tag(tag).d("Verifying token with Google servers...")
            val result = recaptchaRepository.verifyToken(token, action)
            
            when (result) {
                is RecaptchaResult.Success -> {
                    val trustLevelText = when (result.trustLevel) {
                        TrustLevel.HIGH -> "HIGH"
                        TrustLevel.MEDIUM -> "MEDIUM"
                        TrustLevel.LOW -> "LOW"
                    }
                    
                    Timber.tag(tag).d("Verification successful. Score: ${result.score}, Trust: $trustLevelText")
                    
                    when (result.trustLevel) {
                        TrustLevel.HIGH -> {
                            _captchaState.value = CaptchaState.Success(
                                token = token,
                                score = result.score,
                                trustLevel = trustLevelText
                            )
                        }
                        TrustLevel.MEDIUM -> {
                            _captchaState.value = CaptchaState.Warning(
                                token = token,
                                score = result.score,
                                message = "Score medio (${String.format("%.2f", result.score)}). Puede requerir verificación adicional."
                            )
                        }
                        TrustLevel.LOW -> {
                            _captchaState.value = CaptchaState.Warning(
                                token = token,
                                score = result.score,
                                message = "Score bajo (${String.format("%.2f", result.score)}). Posible actividad automatizada detectada."
                            )
                        }
                    }
                }
                is RecaptchaResult.Error -> {
                    Timber.tag(tag).e("Verification failed: ${result.message}")
                    _captchaState.value = CaptchaState.Error("Verificación fallida: ${result.message}")
                }
            }
            
        } catch (e: Exception) {
            Timber.tag(tag).e("Exception during verification: ${e.message}")
            _captchaState.value = CaptchaState.Error("Error durante verificación: ${e.message}")
        }
    }

    private suspend fun simulateDemoSuccess() {
        Timber.tag(tag).i("DEMO MODE: Simulando verificación exitosa de reCaptcha v3")
        kotlinx.coroutines.delay(3000)
        
        val demoToken = "demo_token_03AGdBq26_reCaptcha_v3_demo_simulation_${System.currentTimeMillis()}"
        val demoScore = 0.85f
        
        _token.value = demoToken
        _captchaState.value = CaptchaState.Success(
            token = demoToken,
            score = demoScore,
            trustLevel = "HIGH"
        )
        
        Timber.tag(tag).i("DEMO MODE: Token simulado: ${demoToken.take(30)}...")
        Timber.tag(tag).i("DEMO MODE: Score simulado: $demoScore (HIGH)")
    }

    fun resetState() = viewModelScope.launch {
        _captchaState.value = CaptchaState.Nonce
        _token.value = ""
    }
}

sealed interface CaptchaState {
    data object Nonce : CaptchaState
    data object Loading : CaptchaState
    data class Success(val token: String, val score: Float = 1.0f, val trustLevel: String = "HIGH") : CaptchaState
    data class Warning(val token: String, val score: Float, val message: String = "Verificación adicional requerida") : CaptchaState
    data class Error(val message: String) : CaptchaState
}
