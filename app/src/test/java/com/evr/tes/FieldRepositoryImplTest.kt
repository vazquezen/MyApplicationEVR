package com.evr.tes

import android.content.Context
import android.content.res.Resources
import app.cash.turbine.test
import com.evr.tes.core_data.dispacher.MainCoroutinesRule
import com.evr.tes.core_data.fields.FieldRepositoryImpl
import com.evr.tes.core_data.fields.FieldResult
import com.evr.tes.core_network.request.NetworkClient
import com.evr.tes.core_model.fields.Data
import com.evr.tes.core_model.fields.Field
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import kotlin.time.DurationUnit
import kotlin.time.toDuration


class FieldRepositoryImplTest {

    private lateinit var repository: FieldRepositoryImpl
    private var context: Context = mock()

    @Mock
    private lateinit var resources: Resources


    @get:Rule
    val coroutinesRule = MainCoroutinesRule()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        whenever(context.resources).thenReturn(resources)
        repository = FieldRepositoryImpl(
            network = NetworkClient(OkHttpClient(), Gson()),
            context = context,
            dispatcher = coroutinesRule.testDispatcher
        )
    }

    @Test
    fun getFieldsSuccess() = runTest {
        repository.getFields().test(2.toDuration(DurationUnit.SECONDS)) {
            val expectItem = awaitItem()
            assertTrue(expectItem is FieldResult.SuccessFieldList)
            awaitComplete()
        }
    }

    @Test
    fun getFieldsError() = runTest {
        repository.getFields().test(2.toDuration(DurationUnit.SECONDS)) {
            val expectItem = awaitItem()
            assertFalse(expectItem is FieldResult.Error)
            awaitComplete()
        }
    }

    @Test
    fun loadDataFromLocalResource() {
        whenever(resources.openRawResource(R.raw.getregistrationfieldsresponse)).thenThrow(Resources.NotFoundException::class.java)
        assertThrows(Resources.NotFoundException::class.java) {
            repository.loadDataFromLocalResource(R.raw.getregistrationfieldsresponse)
        }
    }

    @Test
    fun dataToFieldsListTest() {
        val data = Data(
            customerLastname = createFieldsMock(),
            customerPhone = createFieldsMock(),
            customerMonthlyIncome = createFieldsMock(),
            bankIban = createFieldsMock(),
            language = createFieldsMock(),
            customerPersoncode = createFieldsMock(),
            customerEmail = createFieldsMock(),
            customerFirstname = createFieldsMock(),
            customerGender = createFieldsMock(),
            customerBirthday = createFieldsMock(),
            pepStatus = createFieldsMock(),
            amlCheck = createFieldsMock()
        )

        val result = repository.dataToFieldsList(data)
        assertEquals(12, result.size)
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