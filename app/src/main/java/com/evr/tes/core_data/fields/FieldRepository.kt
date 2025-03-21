package com.evr.tes.core_data.fields

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

/**
 * Repository for fetching fields.
 */
interface FieldRepository {

    @WorkerThread
    fun getFields(): Flow<FieldResult>
}