package com.firstapplication.dormapp.utils

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

fun DatabaseReference.getOnce(): SelectBuilder {
    return SelectBuilder(this)
}

class Snapshot(val dataSnapshot: DataSnapshot) {
    var cancel: Boolean = false
}

class SelectBuilder(private val reference: DatabaseReference) {

    private var childrenDataChange: Boolean? = null
    private var onDataChange: ((Snapshot) -> Unit)? = null
    private var onCancelled: ((DatabaseError) -> Unit)? = null

    fun childrenDataChange(value: Boolean): SelectBuilder {
        childrenDataChange = value
        return this
    }

    fun onDataChange(onDataChange: (Snapshot) -> Unit): SelectBuilder {
        this.onDataChange = onDataChange
        return this
    }

    fun onCancelled(onCancelled: (DatabaseError) -> Unit): SelectBuilder {
        this.onCancelled = onCancelled
        return this
    }

    suspend fun read() {
        var key = false
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (onDataChange != null) {
                    if (childrenDataChange == true && snapshot.exists()) {
                        readChildrenData(snapshot)
                    } else if (snapshot.exists()) {
                        onDataChange!!(Snapshot(snapshot))
                    }
                }

                key = true
            }

            override fun onCancelled(error: DatabaseError) {
                if (onCancelled != null) onCancelled!!(error)
                key = true
            }
        })

        while (!key) defaultDelay()
    }

    private fun readChildrenData(snapshot: DataSnapshot) {
        for (s in snapshot.children) {
            val sn = Snapshot(s)
            onDataChange!!(sn)
            if (sn.cancel) return
        }
    }
}