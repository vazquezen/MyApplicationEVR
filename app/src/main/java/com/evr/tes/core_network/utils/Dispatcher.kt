package com.evr.tes.core_network.utils

import javax.inject.Qualifier

/**
 * Qualifier annotation for the dispatchers.
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val applicationDispatcher: AppDispatcher)

enum class AppDispatcher {
    IO,
    UNCONFINED,
}