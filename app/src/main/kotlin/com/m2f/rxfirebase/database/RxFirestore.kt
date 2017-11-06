package com.m2f.rxfirebase.database

import com.google.firebase.firestore.*
import extensions.canEmitt
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

/**
 * @author Marc Moreno
 * @since 4/11/17.
 */

inline fun <reified T> Query.observe(backpressureStrategy: BackpressureStrategy = BackpressureStrategy.LATEST): Flowable<Change<T>> {

    var registration: ListenerRegistration? = null

    return Flowable.create({ emitter ->
        emitter canEmitt {
            { querySnapshot: QuerySnapshot, error: FirebaseFirestoreException? ->
                if (error != null) {
                    onError(error)
                } else {
                    querySnapshot.documentChanges.forEach { change ->
                        onNext(Change(change.oldIndex, change.newIndex, change.document.toObject(T::class.java), change.type.type()))
                    }
                }
            }.let {
                setCancellable { registration?.remove(); registration = null }
                registration = this@observe.addSnapshotListener(it)
            }
        }
    }, backpressureStrategy)
}

inline fun <reified T> DocumentReference.observe(backpressureStrategy: BackpressureStrategy = BackpressureStrategy.LATEST): Flowable<T> {

    var registration: ListenerRegistration? = null

    return Flowable.create({ emitter ->
        emitter canEmitt {
            { documentSnapshot: DocumentSnapshot, error: FirebaseFirestoreException? ->
                if (error != null) {
                    onError(error)
                } else {
                    try {
                        val toObject = documentSnapshot.toObject(T::class.java)
                        onNext(toObject)
                    } catch (exception: RuntimeException) {
                        onError(exception)
                    }
                }
            }.let {
                setCancellable { registration?.remove(); registration = null }
                registration = this@observe.addSnapshotListener(it)
            }
        }
    }, backpressureStrategy)
}

fun DocumentChange.Type.type(): Change.Type = when (this) {
    DocumentChange.Type.ADDED -> Change.Type.ADDED
    DocumentChange.Type.MODIFIED -> Change.Type.MODIFIED
    DocumentChange.Type.REMOVED -> Change.Type.REMOVED
}
