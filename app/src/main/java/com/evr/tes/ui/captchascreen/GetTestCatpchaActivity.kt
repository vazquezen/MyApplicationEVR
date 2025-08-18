package com.evr.tes.ui.captchascreen

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.evr.tes.R
import com.geetest.sdk.GT3ConfigBean
import com.geetest.sdk.GT3ErrorBean
import com.geetest.sdk.GT3GeetestUtils
import com.geetest.sdk.GT3Listener
import com.geetest.sdk.views.GT3GeetestButton


class GetTestCatpchaActivity : AppCompatActivity() {

    private val TAG = "EVR"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_test_catpcha)
        enableEdgeToEdge()

        val geetestButton = findViewById<View>(R.id.btn_geetest) as GT3GeetestButton
        val gt3GeetestUtils = GT3GeetestUtils(this)
        val gt3ConfigBean = GT3ConfigBean()

        gt3ConfigBean.pattern = 1

        gt3ConfigBean.isCanceledOnTouchOutside = false

        gt3ConfigBean.lang = null

        gt3ConfigBean.timeout = 10000

        gt3ConfigBean.webviewTimeout = 10000



        gt3GeetestUtils.startCustomFlow()



        gt3ConfigBean.listener = object : GT3Listener() {
            /**
             * CAPTCHA loading is completed
             * @param duration Loading duration and version info，in JSON format
             */
            override fun onDialogReady(duration: String) {
                Log.e(TAG, "GT3BaseListener-->onDialogReady-->$duration")
            }

            /**
             * Verification result callback
             * @param code 1:success, 0:fail
             */
            override fun onReceiveCaptchaCode(code: Int) {
                Log.e(TAG, "GT3BaseListener-->onReceiveCaptchaCode-->$code")
            }

            /**
             * api2 custom call
             * @param result
             */
            override fun onDialogResult(result: String) {
                Log.e(TAG, "GT3BaseListener-->onDialogResult-->$result")
                // Start api2 workflow
                //RequestAPI2.execute(result)

            }

            /**
             * Statistic info.
             * @param result
             */
            override fun onStatistics(result: String) {
                Log.e(TAG, "GT3BaseListener-->onStatistics-->$result")
            }

            /**
             * Close the CAPTCHA
             * @param num 1 Click the close button to close the CAPTCHA, 2 Click anyplace on screen to close the CAPTCHA, 3 Click return button the close
             */
            override fun onClosed(num: Int) {
                Log.e(TAG, "GT3BaseListener-->onClosed-->$num")
            }

            /**
             * Verfication succeeds
             * @param result
             */
            override fun onSuccess(result: String) {
                Log.e(TAG, "GT3BaseListener-->onSuccess-->$result")
            }

            /**
             * Verification fails
             * @param errorBean Version info, error code & description, etc.
             */
            override fun onFailed(errorBean: GT3ErrorBean) {
                Log.e(TAG, "GT3BaseListener-->onFailed-->$errorBean")
            }

            /**
             * api1 custom call
             */
            override fun onButtonClick() {
                //RequestAPI1().execute()
            }
        }

        gt3GeetestUtils.init(gt3ConfigBean)
    }
}