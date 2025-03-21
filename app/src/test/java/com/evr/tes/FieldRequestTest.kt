package com.evr.tes

import com.evr.tes.core_model.request.RequestDto
import com.evr.tes.core_network.request.NetworkClient
import com.evr.tes.core_network.utils.EndPoints
import com.evr.tes.core_network.utils.HttpCodes
import com.evr.tes.core_network.utils.HttpMethods
import com.evr.tes.core_network.utils.getSignedRequest
import org.junit.Test

/**
 * Class that tests the field request.
 */
class FieldRequestTest {

    private val network: NetworkClient = NetworkClient.instance()

    @Test
    fun getFieldError() {
        val requestDto = RequestDto(method = HttpMethods.GET, resource = EndPoints.RESOURCE_URL_FIELDS_BAD)
        val response = network.client().newCall(requestDto.getSignedRequest()).execute()
        assert(response.code == HttpCodes.REQUEST_NOT_FOUND)
    }

    @Test
    fun getFieldSuccess() {
        val requestDto = RequestDto(method = HttpMethods.GET, resource = EndPoints.RESOURCE_URL_FIELDS)
        val response = network.client().newCall(requestDto.getSignedRequest()).execute()
        assert(response.code == 200)
    }
}