package com.evr.tes.ui.captchascreen

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.evr.tes.R
import com.evr.tes.core_data.friendly.doLoginRequest
import com.evr.tes.helpers.Keys.FRIENDLY_CAPTCHA_SITEKEY
import com.evr.tes.ui.success.SuccessActivity
import com.evr.tes.ui.theme.MyApplicationTheme
import com.friendlycaptcha.android.sdk.FriendlyCaptchaSDK
import com.friendlycaptcha.android.sdk.FriendlyCaptchaWidgetHandle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FriendlyCaptchaActivity : ComponentActivity() {

    private val sdk by lazy {
        FriendlyCaptchaSDK(context = this, apiEndpoint = "global")
    }

    private val widget by lazy {
        sdk.createWidget(sitekey = FRIENDLY_CAPTCHA_SITEKEY)
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    topBar = {
                    TopAppBar(
                        title = { Text(text = stringResource(id = R.string.title_activity_friendly_captcha)) }
                    )
                },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    LoginForm(
                        modifier = Modifier.padding(innerPadding),
                        onLoginClicked = { username, password, captchaResponse, setLoading, setMessage ->
                            setLoading(true)
                            setMessage("")

                            CoroutineScope(Dispatchers.Main).launch {
                                val response = doLoginRequest(username, password, captchaResponse)
                                setLoading(false)
                                if (response.success) {
                                    startActivity(
                                        Intent(
                                            this@FriendlyCaptchaActivity,
                                            SuccessActivity::class.java
                                        )
                                    )
                                    finish()
                                } else {
                                    widget.reset()
                                    setMessage(response.message)
                                }
                            }
                        },
                        widget = widget
                    )
                }
            }
        }
    }
}

@Composable
fun LoginForm(
    modifier: Modifier = Modifier,
    onLoginClicked: (
        username: String,
        password: String,
        captchaResponse: String,
        setLoading: (Boolean) -> Unit,
        setMessage: (String) -> Unit
    ) -> Unit,
    widget: FriendlyCaptchaWidgetHandle
) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val captchaResponse = remember { mutableStateOf("") }
    var loginMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var buttonEnabled by remember { mutableStateOf(false) }

    widget.setOnStateChangeListener { event ->
        captchaResponse.value = event.response

        when (event.state) {
            "reset" -> buttonEnabled = false
            "completed" -> buttonEnabled = true
            "expired" -> buttonEnabled = false
            "error" -> buttonEnabled = true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Login",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        OutlinedTextField(
            value = username.value,
            onValueChange = { username.value = it },
            label = { Text("Username") },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        widget.start()
                    }
                }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            shape = RoundedCornerShape(8.dp),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        widget.start()
                    }
                }
        )
        Spacer(modifier = Modifier.height(16.dp))

        AndroidView(
            factory = { _ ->
                widget.view
            },
            modifier = Modifier.fillMaxWidth().height(60.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                onLoginClicked(
                    username.value,
                    password.value,
                    captchaResponse.value,
                    { isLoading = it },
                    { loginMessage = it }
                )
            },
            enabled = !isLoading && buttonEnabled,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3DDC84)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Click to log in")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "This is an example app.\nYou can enter any username or password.",
            fontSize = 12.sp,
            lineHeight = 16.sp,
            color = Color.Gray,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(loginMessage, color = Color.Red)
    }
}