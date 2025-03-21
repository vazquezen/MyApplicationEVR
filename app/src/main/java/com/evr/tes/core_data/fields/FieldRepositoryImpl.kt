package com.evr.tes.core_data.fields

import android.content.Context
import androidx.annotation.RawRes
import androidx.annotation.WorkerThread
import com.evr.tes.R
import com.evr.tes.core_model.fields.ApiResponse
import com.evr.tes.core_model.fields.Data
import com.evr.tes.core_model.fields.Field
import com.evr.tes.core_model.request.RequestDto
import com.evr.tes.core_network.request.NetworkClient
import com.evr.tes.core_network.utils.AppDispatcher
import com.evr.tes.core_network.utils.Dispatcher
import com.evr.tes.core_network.utils.EndPoints
import com.evr.tes.core_network.utils.HttpCodes
import com.evr.tes.core_network.utils.HttpMethods
import com.evr.tes.core_network.utils.getSignedRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.jetbrains.annotations.VisibleForTesting
import org.json.JSONObject
import javax.inject.Inject

/**
 * Implementation of [FieldRepository].
 */
@VisibleForTesting
class FieldRepositoryImpl @Inject constructor(
    private val network: NetworkClient,
    @ApplicationContext private val context: Context,
    @Dispatcher(AppDispatcher.IO) private val dispatcher: CoroutineDispatcher
) : FieldRepository {

    /**
     * Fetches the fields from the server.
     */
    @WorkerThread
    override fun getFields(): Flow<FieldResult> = flow {

        val dto = RequestDto(
                method = HttpMethods.GET,
                resource = EndPoints.RESOURCE_URL_FIELDS
            )
        val response = network.client().newCall(dto.getSignedRequest()).execute()

        when (response.code) {
            HttpCodes.REQUEST_SUCCESS -> {

                val result =
                    network.gson().fromJson(response.body?.string(), ApiResponse::class.java)

                if (result.ok == 1) {
                    val list = dataToFieldsList(result.data)
                    emit(FieldResult.SuccessFieldList(list))
                } else {
                    val localResult = network.gson().fromJson(
                        loadDataFromLocalResource(R.raw.getregistrationfieldsresponse).toString(),
                        ApiResponse::class.java
                    )
                    val list = dataToFieldsList(localResult.data)
                    emit(FieldResult.SuccessFieldList(list))
                }
            }

            HttpCodes.REQUEST_NOT_FOUND, HttpCodes.REQUEST_INTERNAL_SERVER_ERROR -> {
                emit(FieldResult.Error)
            }

            else -> {
                emit(FieldResult.Error)
            }
        }
    }.flowOn(dispatcher).catch {
        emit(FieldResult.Error)
    }

    /**
     * Loads the data from a local resource.
     */
    fun loadDataFromLocalResource(@RawRes resource: Int): JSONObject {
        val rawJson = context.resources
            .openRawResource(resource)
            .bufferedReader()
            .use { it.readText() }
        return JSONObject(rawJson)
    }

    /**
     * Converts the data object to a list of fields.
     */
    fun dataToFieldsList(data: Data): List<Field> {
        val list = listOf(
            data.customerLastname,
            data.customerPhone,
            data.customerMonthlyIncome,
            data.bankIban,
            data.language,
            data.customerPersoncode,
            data.customerEmail,
            data.customerFirstname,
            data.customerGender,
            data.customerBirthday,
            data.pepStatus,
            data.amlCheck
        )

        list.forEach{ field ->
            field.valuesList = castValuesToList(field.values)
        }

        return list
    }

    /**
     * Casts the values of a field to a list of strings.
     */
    fun castValuesToList(values: Any?): List<String> {
        return when (values) {
            is List<*> -> values.filterIsInstance<String>()
            is Map<*, *> -> values.values.map { it.toString() }
            is String, is Number, is Boolean -> listOf(values.toString())
            else -> emptyList()
        }
    }
}