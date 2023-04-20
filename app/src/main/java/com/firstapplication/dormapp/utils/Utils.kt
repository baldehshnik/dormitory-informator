package com.firstapplication.dormapp.utils

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

suspend fun defaultDelay(timeMillis: Long = 100L) = delay(timeMillis)

fun logRealtimeError(clazz: Any, error: DatabaseError) {
    Log.e(clazz::class.java.simpleName, error.message)
}

suspend fun <T> setLiveValueWithMainContext(
    liveData: MutableLiveData<T>,
    value: T,
) = withContext(Dispatchers.Main) {
    liveData.value = value
}

suspend fun DatabaseReference.remove(listener: ((error: DatabaseError?, ref: DatabaseReference) -> Unit)?): Boolean {
    var key = false
    this.removeValue { error, ref ->
        if (listener != null) listener(error, ref)
        key = true
    }

    while (!key) defaultDelay()
    return key
}

suspend fun DatabaseReference.set(value: Any, listener: ((error: DatabaseError?, ref: DatabaseReference) -> Unit)?): Boolean {
    var key = false
    this.setValue(value) { error, ref ->
        if (listener != null) listener(error, ref)
        key = true
    }

    while (!key) defaultDelay()
    return key
}