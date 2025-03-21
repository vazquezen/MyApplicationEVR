package com.evr.tes.core_data.fields

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Module for providing the [FieldRepository] implementation.
 */
@Module
@InstallIn(SingletonComponent::class)
internal fun interface FieldDIRepository {

    @Binds
    fun bindFieldsRepository(impl: FieldRepositoryImpl): FieldRepository
}