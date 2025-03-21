package com.evr.tes.core_data.fields

import com.evr.tes.core_model.fields.Field

sealed class FieldResult {
    data class SuccessFieldList(val fieldsList: List<Field>) : FieldResult()
    data object Error : FieldResult()
}