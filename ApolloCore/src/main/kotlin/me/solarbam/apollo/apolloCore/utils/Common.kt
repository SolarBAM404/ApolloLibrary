package me.solarbam.apollo.apolloCore.utils

import me.solarbam.apollo.apolloCore.Common
import me.solarbam.apollo.apolloCore.runnables.RunnableObject
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
    return Common.miniMessage(this)
}
