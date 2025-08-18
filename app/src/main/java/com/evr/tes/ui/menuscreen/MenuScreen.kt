package com.evr.tes.ui.menuscreen

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.evr.tes.ui.captchascreen.FriendlyCaptchaActivity
import com.evr.tes.ui.captchascreen.GetTestCatpchaActivity
import com.evr.tes.ui.captchascreen.GoogleReCaptchaActivity
import com.evr.tes.ui.captchascreen.HCaptchaActivity
import com.evr.tes.ui.success.SuccessActivity

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MenuScreen(
    paddingValues: PaddingValues,
    context: Context = LocalContext.current
) {
    Box(Modifier.padding(paddingValues)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Button(
                onClick = {
                    context.startActivity(
                        Intent(
                            context,
                            GoogleReCaptchaActivity::class.java
                        )
                    )
                }
            ) {
                Text(text = "Captcha Google")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    context.startActivity(
                        Intent(
                            context,
                            FriendlyCaptchaActivity::class.java
                        )
                    )
                }
            ) {
                Text(text = "Captcha Friendly")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    context.startActivity(
                        Intent(
                            context,
                            HCaptchaActivity::class.java
                        )
                    )
                }
            ) {
                Text(text = "Captcha hCaptcha")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    context.startActivity(
                        Intent(
                            context,
                            GetTestCatpchaActivity::class.java
                        )
                    )
                }
            ) {
                Text(text = "Captcha GetTest")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    context.startActivity(
                        Intent(
                            context,
                            SuccessActivity::class.java
                        )
                    )
                }
            ) {
                Text(text = "Captcha Tencent Cloud")
            }
        }
    }
}