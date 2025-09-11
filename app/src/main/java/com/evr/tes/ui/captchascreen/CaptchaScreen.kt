package com.evr.tes.ui.captchascreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evr.tes.ui.success.SuccessActivity
import timber.log.Timber


@Composable
fun CaptchaScreen(
    paddingValues: PaddingValues,
    captchaScreenViewModel: CaptchaScreenViewModel? = hiltViewModel()
) {

    Box(Modifier.padding(paddingValues)) {
        UIState(viewModel = captchaScreenViewModel!!)
    }
}

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun UIState(
    viewModel: CaptchaScreenViewModel,
    context: Context = LocalContext.current
) {
    val uiState = viewModel.captchaState.collectAsStateWithLifecycle(
        initialValue = CaptchaState.Nonce
    )
    val isLoading = uiState.value is CaptchaState.Loading

    when(uiState.value) {
        is CaptchaState.Nonce -> {
            viewModel.resetState()
        }

        is CaptchaState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }

        is CaptchaState.Success -> {
            val successState = uiState.value as CaptchaState.Success
            Toast.makeText(
                context, 
                "✅ Identidad validada! Score: ${String.format("%.2f", successState.score)} - (${successState.trustLevel})",
                Toast.LENGTH_LONG
            ).show()
            context.startActivity(Intent(context, SuccessActivity::class.java))
        }
        
        is CaptchaState.Warning -> {
            val warningState = uiState.value as CaptchaState.Warning
            Toast.makeText(
                context, 
                "⚠️ ${warningState.message}", 
                Toast.LENGTH_LONG
            ).show()
        }

        is CaptchaState.Error -> {
            val errorState = uiState.value as CaptchaState.Error
            Timber.tag("EVR").e("reCaptcha Error: ${errorState.message}")
            Toast.makeText(
                context,
                "🚨 Error: ${errorState.message}",
                Toast.LENGTH_LONG
            ).show()
            viewModel.resetState()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { viewModel.getToken() },
            enabled = isLoading.not()
        ) {
            Text(text = if (isLoading) "Verificando..." else "Verificar con Google reCaptcha")
        }
    }
}
