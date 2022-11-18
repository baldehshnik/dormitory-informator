package com.firstapplication.dormapp.data.models

class SingleEvent<T> (private val value: T) {
    private var handled: Boolean = false

    fun getValue(): T? {
        if (handled) return null
        handled = true

        return value
    }
}