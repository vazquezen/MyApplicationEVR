package com.evr.tes.core_data.fields

import com.evr.tes.core_model.fields.Field

/**
 * Sealed class for handling the result of fetching fields.
 */
sealed class FieldResult {
    data class SuccessFieldList(val fieldsList: List<Field>) : FieldResult()
    data object Error : FieldResult()
}