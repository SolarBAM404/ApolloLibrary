package me.solar.apollo.apolloCore.utils

import me.solar.apollo.apolloCore.Common
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component

fun Audience.tell(message: String) {
    val component = Common.miniMessage(message)
    sendMessage(component)
}

fun Audience.line() {
    tell("<gray>-----------------------------------------------------")
}

fun Audience.messageLater(message: String, delay: Long) {
    me.solar.apollo.apolloCore.runnables.RunnableObject.create {
        tell(message)
    }.startTask(delay)
}

fun Audience.box(message: String) {
    line()
    tell("<gray>$message")
    line()
}

fun String.toComponent(): Component {
    return Common.miniMessage(this)
}
