package com.firstapplication.dormapp.data.remote

import com.firstapplication.dormapp.utils.getOnce
import com.firstapplication.dormapp.utils.remove
import com.firstapplication.dormapp.utils.set
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference

class RealtimeDataSource {

    data class Result<T> (
        val result: T?,
        val error: DatabaseError?
    )

    suspend inline fun <reified T> readAllFrom(reference: DatabaseReference): List<T>? {
        var result: MutableList<T>? = mutableListOf()
        reference.getOnce()
            .childrenDataChange(true)
            .onDataChange { snapshot ->
                val value = snapshot.dataSnapshot.getValue(T::class.java)
                if (value != null) result?.add(value)
            }
            .onCancelled {
                result = null
            }.read()

        return result
    }

    suspend inline fun <reified T> getObjectFrom(reference: DatabaseReference): Result<T> {
        var value: T? = null
        var error: DatabaseError? = null
        reference.getOnce()
            .onDataChange { snapshot ->
                if (snapshot.dataSnapshot.exists()) value = snapshot.dataSnapshot.getValue(T::class.java)
            }
            .onCancelled {
                value = null
                error = it
            }.read()

        return Result(value, error)
    }

    suspend fun setObject(reference: DatabaseReference, value: Any): Result<Boolean> {
        var result = false
        var error: DatabaseError? = null
        reference.set(value) { it, _ ->
            if (it == null) result = true
            else error = it
        }

        return Result(result, error)
    }

    suspend fun removeObject(reference: DatabaseReference): Result<Boolean> {
        var result = false
        var error: DatabaseError? = null
        reference.remove { it, _ ->
            if (it == null) result = true
            else error = it
        }

        return Result(result, error)
    }
}