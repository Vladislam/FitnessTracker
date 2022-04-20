package com.example.fitnesstracker.util.extensions

import android.view.View
import com.example.fitnesstracker.util.const.BindingConstant.SMALL_THROTTLE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.ldralighieri.corbind.view.clicks
import java.util.*

fun <T> Flow<T>.throttleFirst(periodMillis: Long = SMALL_THROTTLE): Flow<T> {
    require(periodMillis > 0) { "period should be positive" }
    return flow {
        var lastTime = 0L
        collect { value ->
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastTime >= periodMillis) {
                lastTime = currentTime
                emit(value)
            }
        }
    }
}

fun View.throttleFirstClicks(
    scope: CoroutineScope,
    periodMillis: Long = SMALL_THROTTLE,
    onEach: () -> Unit,
) {
    this.clicks()
        .throttleFirst(periodMillis)
        .onEach {
            onEach.invoke()
        }
        .launchIn(scope)
}