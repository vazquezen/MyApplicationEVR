package com.evr.tes.core_model.request

import com.evr.tes.core_network.utils.Constant
import com.evr.tes.core_network.utils.HttpMethods

data class RequestDto(
    val method: String = HttpMethods.GET,
    var args: Map<String, Any> = mapOf(),
    val parBody: String = "",
    val file: String? = null,
    val resource: String = "",
    val instance: String = "",
    val subResource: String = "",
    var fileByte: ByteArray? = null,
    val headers: Map<String, String> = mapOf(),
    var connectTimeout: Int = Constant.TIMEOUT
)