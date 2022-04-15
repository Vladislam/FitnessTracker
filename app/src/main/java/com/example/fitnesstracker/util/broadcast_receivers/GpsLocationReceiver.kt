package com.example.fitnesstracker.util.broadcast_receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.fitnesstracker.util.const.BindingConstant.LONG_THROTTLE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class GpsLocationReceiver @Inject constructor() : BroadcastReceiver() {

    private var callback: (() -> Unit)? = null
    private var isRegistered = false
    private var job: Job? = null

    fun register(context: Context, filter: IntentFilter?): Intent? {
        return try {
            if (!isRegistered) context.registerReceiver(this, filter) else null
        } finally {
            isRegistered = true
        }
    }

    fun unregister(context: Context): Boolean {
        return (isRegistered
                && unregisterInternal(context))
    }

    private fun unregisterInternal(context: Context): Boolean {
        context.unregisterReceiver(this)
        isRegistered = false
        return true
    }

    override fun onReceive(context: Context, intent: Intent) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            delay(LONG_THROTTLE)
            ensureActive()
            callback?.invoke()
        }
    }

    fun registerCallback(callback: () -> Unit) {
        this.callback = callback
    }
}