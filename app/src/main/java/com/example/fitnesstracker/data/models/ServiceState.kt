package com.example.fitnesstracker.data.models

sealed class ServiceState {
    object Running : ServiceState()
    object Paused : ServiceState()
    object Stopped : ServiceState()
}