package com.example.fitnesstracker.util.extensions

import android.view.View
import com.example.fitnesstracker.util.const.BindingConstant.SMALL_THROTTLE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.ldralighieri.corbind.view.clicks
import java.util.*

@Suppress("UNCHECKED_CAST")
fun <T> Flow<T>.throttleLatest(periodMillis: Long): Flow<T> {
    return channelFlow {
        var lastValue: T?
        var timer: Timer? = null
        onCompletion { timer?.cancel() }
        collect { value ->
            lastValue = value

            if (timer == null) {
                timer = Timer()
                timer?.scheduleAtFixedRate(
                    object : TimerTask() {
                        override fun run() {
                            val last = lastValue
                            lastValue = null
                            if (last != null) {
                                launch {
                                    send(last as T)
                                }
                            } else {
                                timer?.cancel()
                                timer = null
                            }
                        }
                    },
                    0,
                    periodMillis
                )
            }
        }
    }
}

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

fun View.throttleClicks(
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