package com.m2f.rxfirebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import io.reactivex.Single

/**
 * Created by marc on 24/7/17.
 */

inline fun <reified T> Query.observeSingleValue(): Single<T> {
    return  Single.create { emitter ->
        if(!emitter.isDisposed) {
            object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    if (!emitter.isDisposed) {
                        emitter.onError(p0.toException())
                    }
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(!emitter.isDisposed) {
                        val value: T? = p0.getValue(T::class.java)
                        value?.let {
                            emitter.onSuccess(p0.getValue(T::class.java))
                        } ?: emitter.onError(kotlin.ClassCastException("Object emitted is not the same class as ${T::class.simpleName}"))
                    }
                }

            }.let {
                emitter.setCancellable { this.removeEventListener(it)}
                this.addListenerForSingleValueEvent(it)
            }
        }

    }
}
