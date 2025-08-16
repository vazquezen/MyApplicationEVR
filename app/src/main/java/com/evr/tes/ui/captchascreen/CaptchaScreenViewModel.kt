package com.evr.tes.ui.captchascreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evr.tes.App
import com.evr.tes.helpers.Keys
import com.google.android.gms.safetynet.SafetyNet
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
class CaptchaScreenViewModel @Inject constructor() : ViewModel() {

    private val tag = "CaptchaScreenViewModel"
    private lateinit var recaptchaClient: RecaptchaClient

    private val _captchaState: MutableStateFlow<CaptchaState> = MutableStateFlow(CaptchaState.Nonce)
    val captchaState: StateFlow<CaptchaState> = _captchaState

    private val _token = MutableStateFlow("")
    val token: StateFlow<String> = _token

    init {
        initializeRecaptchaClient()
    }

    fun initializeRecaptchaClient() = viewModelScope.launch {
        _captchaState.value = CaptchaState.Loading
        try {
            recaptchaClient = Recaptcha.fetchClient(
                application = App.instance,
                siteKey = Keys.RECAPTCHA_API_SITE_KEY
            )
            Timber.tag(tag).d("Recaptcha client initialized successfully")

        } catch(e: RecaptchaException) {
            _captchaState.value = CaptchaState.Error(
                message = "Recaptcha client initialization failed: ${e.errorCode.errorMessage}"
            )
            Timber.tag(tag).d(e.errorCode.errorMessage)
        }
    }

    fun getToken() = viewModelScope.launch {
        _captchaState.value = CaptchaState.Loading
       /*recaptchaClient
            .execute(RecaptchaAction.custom("Verify"), timeout = 10000L)
            .onSuccess {
                Timber.tag(tag).d("Recaptcha token: $it")
                _token.value = it
                _captchaState.value = CaptchaState.Success(it)
            }.onFailure {
                Timber.tag(tag).d(it.toString())
                _captchaState.value = CaptchaState.Error(it.message.toString())
            }*/

        try {
            SafetyNet.getClient(App.appContext).verifyWithRecaptcha(Keys.RECAPTCHA_API_SITE_KEY)
                .addOnSuccessListener { response ->
                    val token = response.tokenResult
                    Timber.tag(tag).d("Recaptcha token: $token")

                    if (token.isNullOrEmpty().not()) {
                        _captchaState.value = CaptchaState.Success(token)
                    } else {
                        _captchaState.value = CaptchaState.Error("Token vacío")
                    }
                }
                .addOnFailureListener { exception ->
                    Timber.tag(tag).d("Recaptcha error token: ${exception.message}")
                    _captchaState.value = CaptchaState.Error(exception.message ?: "Error desconocido")
                }
        } catch (e: Exception) {
            _captchaState.value = CaptchaState.Error(e.message ?: "Error desconocido")
        }
    }

    fun resetState() = viewModelScope.launch {
        _captchaState.value = CaptchaState.Nonce
        _token.value = ""
    }
}

sealed interface CaptchaState {
    data object Nonce : CaptchaState
    data object Loading : CaptchaState
    data class Success(val token: String) : CaptchaState
    data class Error(val message: String) : CaptchaState
}