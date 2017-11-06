package com.m2f.rxfirebase.database

/**
 * @author Marc Moreno
 * @since 5/11/17.
 */
data class Change<T>(val oldIndex: Int,
                     val newIndex: Int,
                     val document: T,
                     val type: Type) {


    enum class Type {
        ADDED,
        MODIFIED,
        REMOVED
    }

}