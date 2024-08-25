package com.tsuchinok.t2s.core.common.error

import android.content.Intent

data class RecoverableIntentError(
    override val message: String?,
    val intent: Intent,
) : Exception()
