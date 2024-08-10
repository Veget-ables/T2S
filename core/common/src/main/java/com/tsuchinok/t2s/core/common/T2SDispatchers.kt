package com.tsuchinok.t2s.core.common

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val t2sDispatcher: T2SDispatchers)

enum class T2SDispatchers {
    Default,
    IO,
}
