package me.solar.apollo.apolloCore.utils

object Variables {

    private val playerPlaceholder = "%player%"
    private val worldPlaceholder = "%world%"

    fun replace(message: String, playerName: String): String {
        return message.replace(playerPlaceholder, playerName)
    }

    fun replace(message: String, playerName: String, worldName: String): String {
        return message.replace(playerPlaceholder, playerName).replace(worldPlaceholder, worldName)
    }

    fun replaceWorld(message: String, worldName: String): String {
        return message.replace(worldPlaceholder, worldName)
    }

}