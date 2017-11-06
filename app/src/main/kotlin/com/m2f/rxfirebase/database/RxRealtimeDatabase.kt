package com.m2f.rxfirebase.database

import com.google.firebase.database.*
import exceptions.ElementNotExistsException
import extensions.canEmitt
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Maybe

/**
 * Created by marc on 24/7/17.
 */

inline fun <reified T> Query.observeSingleValue(): Maybe<T> {
    return Maybe.create { emitter ->
        emitter canEmitt {
            object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    canEmitt {
                        onError(p0.toException())
                    }
                }

                override fun onDataChange(p0: DataSnapshot) {
                    canEmitt {
                        if (p0.exists()) {
                            val value: T? = p0.getValue(T::class.java)
                            value?.let {
                                onSuccess(it)
                            } ?: onError(kotlin.ClassCastException("Object emitted is not the same class as ${T::class}"))
                        } else {
                            onComplete()
                        }
                    }
                }

            }.let {
                setCancellable { this@observeSingleValue.removeEventListener(it) }
                this@observeSingleValue.addListenerForSingleValueEvent(it)
            }
        }

    }
}

inline fun <reified T> Query.observeValueEvent(backpressureStrategy: BackpressureStrategy = BackpressureStrategy.LATEST): Flowable<T> {
    return Flowable.create({
        it canEmitt {
            object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    canEmitt {
                        onError(p0.toException())
                    }
                }

                override fun onDataChange(p0: DataSnapshot) {
                    canEmitt {
                        if (p0.exists()) {
                            val value: T? = p0.getValue(T::class.java)
                            value?.let {
                                onNext(it)
                            } ?: onError(kotlin.ClassCastException("Object emitted is not the same class as ${T::class}"))
                        } else {
                            onError(ElementNotExistsException())
                        }
                    }
                }

            }.let {
                setCancellable { this@observeValueEvent.removeEventListener(it) }
                this@observeValueEvent.addValueEventListener(it)
            }
        }
    }, backpressureStrategy)
}

inline fun <reified T> Query.observeChildEvents(backpressureStrategy: BackpressureStrategy = BackpressureStrategy.LATEST): Flowable<ChildEvent<T>> {
    return Flowable.create({ emitter ->
        emitter canEmitt {
            object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    canEmitt {
                        onError(p0.toException())
                    }
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                    canEmitt {
                        if (p0.exists()) {
                            val value: T? = p0.getValue(T::class.java)
                            value?.let {
                                onNext(ChildEvent(p0.key, it, ChildEvent.EventType.MOVED, p1))
                            } ?: onError(kotlin.ClassCastException("Object emitted is not the same class as ${T::class}"))
                        } else {
                            onError(ElementNotExistsException())
                        }
                    }
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    canEmitt {
                        if (p0.exists()) {
                            val value: T? = p0.getValue(T::class.java)
                            value?.let {
                                onNext(ChildEvent(p0.key, it, ChildEvent.EventType.CHANGED, p1))
                            } ?: onError(kotlin.ClassCastException("Object emitted is not the same class as ${T::class}"))
                        } else {
                            onError(ElementNotExistsException())
                        }
                    }
                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    canEmitt {
                        if (p0.exists()) {
                            val value: T? = p0.getValue(T::class.java)
                            value?.let {
                                onNext(ChildEvent(p0.key, it, ChildEvent.EventType.ADDED, p1))
                            } ?: onError(kotlin.ClassCastException("Object emitted is not the same class as ${T::class}"))
                        } else {
                            onError(ElementNotExistsException())
                        }
                    }
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                    canEmitt {
                        if (p0.exists()) {
                            val value: T? = p0.getValue(T::class.java)
                            value?.let {
                                onNext(ChildEvent(p0.key, it, ChildEvent.EventType.REMOVED, null))
                            } ?: onError(kotlin.ClassCastException("Object emitted is not the same class as ${T::class}"))
                        } else {
                            onError(ElementNotExistsException())
                        }
                    }
                }
            }.let {
                setCancellable { this@observeChildEvents.removeEventListener(it) }
                this@observeChildEvents.addChildEventListener(it)
            }

        }
    }, backpressureStrategy)
}



