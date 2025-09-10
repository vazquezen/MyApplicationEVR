package com.evr.tes.ui.menuscreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.evr.tes.ui.captchascreen.CaptchaScreen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MenuScreen(
    paddingValues: PaddingValues
) {
    var showCaptcha by remember { mutableStateOf(false) }
    
    if (showCaptcha) {
        CaptchaScreen(paddingValues)
    } else {
        Box(Modifier.padding(paddingValues)) {
            Column(
                modifier = Modifier.fillMaxSize().padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Button(
                    onClick = { showCaptcha = true }
                ) {
                    Text(text = "Google reCaptcha")
                }
            }
        }
    }
}