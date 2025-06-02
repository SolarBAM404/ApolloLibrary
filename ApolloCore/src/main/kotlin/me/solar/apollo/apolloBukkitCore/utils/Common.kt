package me.solar.apollo.apolloBukkitCore.utils

import me.solar.apollo.apolloBukkitCore.ApolloPlugin
import me.solar.apollo.apolloBukkitCore.runnables.RunnableObject
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

fun tell(audience: Audience?, message: String) {
    if (audience == null) {
        tellConsole("Audience is null. Message: $message")
        return
    }
    audience.tell(message)
}

fun tell(player: Player, message: String) {
    val audience : Audience? = adventure().player(player)
    tell(audience, message)
    return
}

fun tellConsole(message: String) {
    tell(ApolloPlugin.adventure?.console(), message)
}

fun error(message: String) {
    tellConsole("<red>$message")
}

fun error(throwable: Throwable) {
    error(throwable.message ?: "An error occurred")
    throwable.printStackTrace()
}

fun line(audience: Audience?) {
    if (audience == null) {
        tellConsole("Audience is null. Cannot send line.")
        return
    }
    audience.line()
}

fun line(player: Player) {
    val audience: Audience? = adventure().player(player)
    line(audience)
}

fun lineConsole() {
    line(ApolloPlugin.adventure?.console())
}

fun box(audience: Audience?, message: String) {
    if (audience == null) {
        tellConsole("Audience is null. Cannot send box with message: $message")
        return
    }
    audience.box(message)
}

fun box(player: Player, message: String) {
    val audience: Audience? = adventure().player(player)
    box(audience, message)
}

fun box(message: String) {
    box(ApolloPlugin.adventure?.console(), message)
}

fun messageLater(audience: Audience, message: String, delay: Long) {
    audience.messageLater(message, delay)
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
