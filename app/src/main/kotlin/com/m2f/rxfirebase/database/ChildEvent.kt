package com.m2f.rxfirebase.database

/**
 * Created by marc on 30/7/17.
 */
data class ChildEvent<out Value>(val key: String,
                                 val value: Value,
                                 val eventType: EventType,
                                 val previousChildName: String? = null) {

    enum class EventType {
        ADDED,
        CHANGED,
        REMOVED,
        MOVED
    }

    override fun equals(other: Any?): Boolean {
        if (this == other) return true
        if (other == null || this::class.java != other::class.java) return false

        val that = other as ChildEvent<*>

        if (eventType != that.eventType) return false
        if (value != that.value) return false
        return previousChildName != that.previousChildName
    }


    override fun hashCode(): Int {
        var result = eventType.hashCode()
        result = 31 * result + (value?.hashCode() ?: 0)
        result = 31 * result + (previousChildName?.hashCode() ?: 0)
        return result

    }
}