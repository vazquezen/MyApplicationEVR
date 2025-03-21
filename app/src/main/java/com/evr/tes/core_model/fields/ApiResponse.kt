package com.evr.tes.core_model.fields

import com.google.gson.annotations.SerializedName


data class Field(
    @SerializedName("req") val req: Boolean,
    @SerializedName("group") val group: String,
    @SerializedName("visible") val visible: Boolean,
    @SerializedName("order") val order: Int,
    @SerializedName("maxlength") val maxlength: Int?,
    @SerializedName("type") val type: String,
    @SerializedName("newline") val newline: Boolean,
    @SerializedName("hidetitle") val hidetitle: Boolean,
    @SerializedName("split") val split: Boolean,
    @SerializedName("mapper") val mapper: String,
    @SerializedName("clientzone_visible") val clientzoneVisible: Boolean,
    @SerializedName("clientzone_editable") val clientzoneEditable: Boolean,
    @SerializedName("clientzone_check") val clientzoneCheck: String?,
    @SerializedName("clientzone_required") val clientzoneRequired: Boolean,
    @SerializedName("cl_visible") val clVisible: Boolean,
    @SerializedName("step") val step: Int,
    @SerializedName("auto_approve") val autoApprove: Boolean,
    @SerializedName("condition_type") val conditionType: Int,
    @SerializedName("condition") val condition: List<Int>,
    @SerializedName("regex") val regex: String?,
    @SerializedName("values") val values: Any?,
    @SerializedName("select_values") var valuesList: List<String>
)

data class Data(
    @SerializedName("customer-lastname") val customerLastname: Field,
    @SerializedName("customer-phone") val customerPhone: Field,
    @SerializedName("customer-monthly-income") val customerMonthlyIncome: Field,
    @SerializedName("bank-iban") val bankIban: Field,
    @SerializedName("language") val language: Field,
    @SerializedName("customer-personcode") val customerPersoncode: Field,
    @SerializedName("customer-email") val customerEmail: Field,
    @SerializedName("customer-firstname") val customerFirstname: Field,
    @SerializedName("customer-gender") val customerGender: Field,
    @SerializedName("customer-birthday") val customerBirthday: Field,
    @SerializedName("pep-status") val pepStatus: Field,
    @SerializedName("aml-check") val amlCheck: Field
)

data class ApiResponse(
    @SerializedName("ok") val ok: Int,
    @SerializedName("data") val data: Data
)