package com.evr.tes

import com.evr.tes.core_data.dispacher.MainCoroutinesRule
import com.evr.tes.core_data.fields.FieldRepository
import com.evr.tes.core_data.fields.FieldResult
import com.evr.tes.core_model.fields.Field
import com.evr.tes.ui.initscreen.FieldsState
import com.evr.tes.ui.initscreen.MainScreenViewModel
import com.nhaarman.mockitokotlin2.atLeastOnce
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * MainScreenViewModelTest
 */
class MainScreenViewModelTest {

    private lateinit var viewModel: MainScreenViewModel
    private val repository: FieldRepository = mock()

    @get:Rule
    val coroutinesRule = MainCoroutinesRule()

    @Before
    fun setup() {
        viewModel = MainScreenViewModel(repository)
    }

    @Test
    fun getFieldsSuccess() = runTest {
        val resultFlow: Flow<FieldResult> = flow {
            emit(
                FieldResult.SuccessFieldList(
                    fieldsList = listOf(createFieldsMock(), createFieldsMock())
                )
            )
        }
        whenever(repository.getFields()).thenReturn(resultFlow)
        assertEquals(FieldsState.Loading, viewModel.fieldsState.value)
        viewModel.loadFields()
        assertEquals(
            FieldsState.SuccessFieldsList(listOf(createFieldsMock(), createFieldsMock())),
            viewModel.fieldsState.value
        )
        verify(repository, atLeastOnce()).getFields()
    }

    // Mocks
    private fun createFieldsMock(): Field {
        return Field(
            req = true,
            group = "group",
            visible = true,
            order = 1,
            maxlength = 10,
            type = "type",
            newline = true,
            hidetitle = true,
            split = true,
            mapper = "mapper",
            clientzoneVisible = true,
            clientzoneEditable = true,
            clientzoneCheck = "clientzoneCheck",
            clientzoneRequired = true,
            clVisible = true,
            step = 1,
            autoApprove = true,
            conditionType = 1,
            condition = listOf(1, 2, 3),
            regex = "regex",
            values = "\"\"\" { \"values\": { \"lv\": \"latvian\", \"ru\": \"russian\" } } \"\"\"",
            valuesList = listOf()
        )
    }
}