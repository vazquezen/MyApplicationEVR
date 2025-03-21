package com.evr.tes.ui.initscreen

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evr.tes.core_data.fields.FieldRepository
import com.evr.tes.core_data.fields.FieldResult
import com.evr.tes.core_model.fields.Field
import com.evr.tes.core_model.fields.FormField
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the MainScreen
 */
@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val fieldsRepository: FieldRepository
) : ViewModel() {

    private val _fieldsState: MutableStateFlow<FieldsState> = MutableStateFlow(FieldsState.Loading)
    val fieldsState: StateFlow<FieldsState> = _fieldsState
    private val _validFields = mutableStateListOf<FormField>()
    val fields = mutableListOf<Field>()

    init {
        loadFields()
    }

    /**
     * Load the fields from the repository
     */
    fun loadFields() = viewModelScope.launch {
        _fieldsState.value = FieldsState.Loading
        fieldsRepository.getFields().collect { fieldResult ->
            when (fieldResult) {
                is FieldResult.SuccessFieldList -> {
                    fields.clear()
                    fields.addAll(
                        fieldResult.fieldsList
                            .filter { it.visible }
                            .sortedBy { it.order }
                    )
                    fields.forEach{ _ ->
                        _validFields.add(FormField())
                    }
                    _fieldsState.value = FieldsState.AppliesList(fields)
                }

                is FieldResult.Error -> {
                    _fieldsState.value = FieldsState.Error
                }
            }
        }
    }

    /**
     * Update the validity of a field
     */
    fun updateValidField(index: Int, isValid: Boolean) {
        _validFields[index] = _validFields[index].copy(isValid = isValid)
    }

    /**
     * Check if all the form fields are valid
     */
    fun isFormFieldsValid(): Boolean {
        val fieldsValid = _validFields.all { it.isValid }
        return fieldsValid
    }

    /**
     * Reset the state of the fields
     */
    fun resetState() = viewModelScope.launch {
        _fieldsState.value = FieldsState.Nonce
    }
}

/**
 * Sealed class to represent the state of the fields
 */
sealed interface FieldsState {
    data object Nonce : FieldsState
    data object Loading : FieldsState
    data object Error : FieldsState
    data class AppliesList(val fields: List<Field>) : FieldsState
}