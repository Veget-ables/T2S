package com.tsuchinoko.t2s.core.ui

import com.tsuchinok.t2s.core.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.concurrent.atomic.AtomicInteger

class ObservableLoadingCounter {
    private val count = AtomicInteger()
    private val loadingState = MutableStateFlow(count.get())

    val observable: Flow<Boolean>
        get() = loadingState.map { it > 0 }.distinctUntilChanged()

    fun addLoader() {
        loadingState.value = count.incrementAndGet()
    }

    fun removeLoader() {
        loadingState.value = count.decrementAndGet()
    }
}

suspend fun <T> Flow<Result<T>>.collectResult(
    counter: ObservableLoadingCounter? = null,
    onError: (exception: Throwable) -> Unit = {},
    onSuccess: (T) -> Unit = {},
) = collect { status ->
    when (status) {
        Result.Loading -> {
            counter?.addLoader()
        }
        is Result.Success -> {
            counter?.removeLoader()
            onSuccess(status.data)
        }
        is Result.Error -> {
            counter?.removeLoader()
            onError(status.exception)
        }
    }
}
