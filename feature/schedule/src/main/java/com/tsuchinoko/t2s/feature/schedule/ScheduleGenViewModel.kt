package com.tsuchinoko.t2s.feature.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsuchinoko.t2s.core.data.ScheduleGenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleGenViewModel @Inject constructor(
    private val scheduleGenRepository: ScheduleGenRepository
) : ViewModel() {
    private val _scheduleGenUiState: MutableStateFlow<ScheduleGenUiState> =
        MutableStateFlow(ScheduleGenUiState.Initial)
    val scheduleGenUiState: StateFlow<ScheduleGenUiState> =
        this._scheduleGenUiState.asStateFlow()

    fun sendPrompt(prompt: String) {
        _scheduleGenUiState.value = ScheduleGenUiState.Loading

        viewModelScope.launch {
            try {
                val scheduleEvents = scheduleGenRepository.generate(prompt)
                _scheduleGenUiState.value = ScheduleGenUiState.Success(scheduleEvents)
            } catch (e: Exception) {
                _scheduleGenUiState.value = ScheduleGenUiState.Error(e.localizedMessage ?: "")
            }
        }
    }
}
