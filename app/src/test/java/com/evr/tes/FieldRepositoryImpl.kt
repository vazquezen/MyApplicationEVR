package com.evr.tes

import com.evr.tes.core_data.dispacher.MainCoroutinesRule
import com.evr.tes.core_data.fields.FieldRepository
import com.evr.tes.core_data.fields.FieldRepositoryImpl
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule


class FieldRepositoryImpl {

    private lateinit var repository: FieldRepository

    @get:Rule
    val coroutinesRule = MainCoroutinesRule()

    @Before
    fun setUp() {
        /*repository = FieldRepositoryImpl(

        )*/
    }
}