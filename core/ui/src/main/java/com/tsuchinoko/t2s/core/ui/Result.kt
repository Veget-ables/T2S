package com.tsuchinoko.t2s.core.ui

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val exception: Throwable) : Result<Nothing>
    data object Loading : Result<Nothing>
    data object Standby : Result<Nothing>
}

fun <T> resultFlow(body: suspend () -> T): Flow<Result<T>> =
    flow { emit(body()) }
        .map<T, Result<T>> { Result.Success(it) }
        .onStart { emit(Result.Loading) }
        .catch { emit(Result.Error(it)) }

class ObservableResult<T> {
    private val _state: MutableStateFlow<Result<T>> = MutableStateFlow(Result.Standby)
    val state: StateFlow<Result<T>> = _state.asStateFlow()

    fun loading() {
        _state.value = Result.Loading
    }

    fun success(data: T) {
        _state.value = Result.Success(data)
    }

    fun error(exception: Throwable) {
        _state.value = Result.Error(exception)
    }
}

suspend fun <T> Flow<Result<T>>.collect(
    observable: ObservableResult<T>,
) = collect { status ->
    when (status) {
        Result.Loading -> {
            observable.loading()
        }
        is Result.Success -> {
            observable.success(status.data)
        }
        is Result.Error -> {
            observable.error(status.exception)
        }

        Result.Standby -> {
            // no op
        }
    }
}
