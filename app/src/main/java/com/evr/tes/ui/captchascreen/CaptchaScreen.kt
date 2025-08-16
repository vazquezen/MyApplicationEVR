package com.evr.tes.ui.captchascreen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

@Composable
private fun UIState(
    viewModel: CaptchaScreenViewModel,
    context: Context = LocalContext.current
) {
    val uiState = viewModel.captchaState.collectAsStateWithLifecycle(
        initialValue = CaptchaState.Nonce
    )
    var loading by remember { mutableStateOf(false) }

    when(uiState.value) {
        is CaptchaState.Nonce -> {
            loading = false
            viewModel.resetState()
        }

        is CaptchaState.Loading -> {
            loading = true
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }

        is CaptchaState.Error -> {
            loading = false
            viewModel.resetState()
            Timber.tag("EVR").d("Recaptcha client NOT initialized successfully")
            Toast.makeText(context, "No se pudo validar tu humanidad", Toast.LENGTH_LONG).show()
        }

        is CaptchaState.Success -> {
            loading = false
            Toast.makeText(context, "Identidad validada :) ${uiState.value}", Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { viewModel.getToken() }
        ) {
            Text(text = "Captcha Google")
        }
    }
}