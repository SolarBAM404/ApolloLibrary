package me.solar.apollo.apolloCore.utils

import me.solar.apollo.apolloCore.ApolloPlugin
import me.solar.apollo.apolloCore.runnables.RunnableObject
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player

private val colourPatten = "<[a-fA-F0-9]{6}>".toRegex()

fun adventure() : BukkitAudiences {
    return ApolloPlugin.adventure ?: throw IllegalStateException("Adventure is not initialized")
}

fun component(string: String): Component {
    val miniMessage = MiniMessage.miniMessage()
    return miniMessage.deserialize(string);
}

fun stripColor(string: String): String {
    if (string.contains("<")) {
        return string.replace(colourPatten, "")
    }
    return string
}

fun tell(player: Player, message: String) {
    val audience : Audience? = adventure().player(player)
    audience?.tell(message)
    return
}

fun tellConsole(message: String) {
    val audience: Audience? = ApolloPlugin.adventure?.console()
    if (audience == null) {
        println("Audience is null. Cannot send message to console: $message")
        return
    }
    audience.tell(message)
}

fun error(message: String) {
    tellConsole("<red>$message")
}

fun error(throwable: Throwable) {
    error(throwable.message ?: "An error occurred")
    throwable.printStackTrace()
}

fun line(player: Player) {
    val audience: Audience = adventure().player(player)
    audience.line()
}

fun lineConsole() {
    val audience: Audience? = ApolloPlugin.adventure?.console()
    if (audience == null) {
        println("Audience is null. Cannot send line message to console")
        return
    }
    audience.line()
}

fun box(player: Player, message: String) {
    val audience: Audience? = adventure().player(player)
    if (audience == null) {
        tellConsole("Audience is null. Cannot send box to player: ${player.name}")
        return
    }
    audience.box(message)
}

fun Audience.tell(message: String) {
    val component = component(message)
    sendMessage(component)
}

fun Audience.line() {
    tell("<gray>-----------------------------------------------------")
}


fun Audience.messageLater(message: String, delay: Long) {
    RunnableObject.create {
        tell(message)
    }.startTask(delay)
}


fun Audience.box(message: String) {
    line()
    tell("<gray>$message")
    line()
}

fun String.toComponent(): Component {
    return component(this)
}
