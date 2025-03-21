package com.evr.tes.core_data.fields

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

interface FieldRepository {

    @WorkerThread
    fun getFields(): Flow<FieldResult>
}