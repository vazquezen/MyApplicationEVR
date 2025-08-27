package com.evr.tes.ui.captchascreen

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.evr.tes.databinding.ActivityTencentCloudCaptchaBinding
import com.tencent.captcha.sdk.RetCode
import com.tencent.captcha.sdk.TencentCaptcha
import com.tencent.captcha.sdk.TencentCaptchaCallback
import com.tencent.captcha.sdk.TencentCaptchaConfig
import com.tencent.captcha.sdk.TencentCaptchaParam
import org.json.JSONObject
import timber.log.Timber

class TencentCloudCaptchaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTencentCloudCaptchaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTencentCloudCaptchaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            WindowInsetsCompat.CONSUMED
        }


        val configBuilder = TencentCaptchaConfig.Builder(applicationContext, { false }, object : TencentCaptchaConfig.ICaptchaDeviceInfoProvider() {

                override fun getAndroidId(): String {
                    // Implementación para obtener el AndroidId
                    return androidId
                }
            }
        )

        val ret = TencentCaptcha.init(configBuilder.build())

        if (ret == RetCode.OK) {
            // inicialización exitosa
        } else {
            // inicialización fallida
            Timber.e("ret code: ${ret.code} msg: ${ret.msg}")
            return
        }

        val callback = object : TencentCaptchaCallback {

            override fun finish(ret: RetCode, resultObject: JSONObject) {
                if (ret == RetCode.OK) {
                    // éxito
                } else {
                    // fallo
                    Timber.e("ret code: ${ret.code} msg: ${ret.msg}")
                }
            }

            override fun exception(t: Throwable) {
                t.printStackTrace()
            }
        }

        val tencentCaptchaBuilder = TencentCaptchaParam.Builder()
            .setWebView(binding.tcaptchaWebview)
            .setCaptchaAppid("CaptchaAppid")

        TencentCaptcha.start(callback, tencentCaptchaBuilder.build())

    }
}