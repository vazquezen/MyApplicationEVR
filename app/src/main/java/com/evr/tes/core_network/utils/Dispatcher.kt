package com.evr.tes.core_network.utils

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val applicationDispatcher: AppDispatcher)

enum class AppDispatcher {
    IO,
    UNCONFINED,
}