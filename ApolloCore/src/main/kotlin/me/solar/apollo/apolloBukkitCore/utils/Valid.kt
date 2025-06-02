package me.solar.apollo.apolloBukkitCore.utils

object Valid {
    fun checkBoolean(value: Boolean, message: String) {
        if (!value) {
            throw IllegalArgumentException(message)
        }
    }

    fun checkNotNull(value: Any?, message: String) {
        if (value == null) {
            throw IllegalArgumentException(message)
        }
    }

    fun checkNotEmpty(value: String) {
        checkNotNull(value, "Value cannot be null")
    }
}