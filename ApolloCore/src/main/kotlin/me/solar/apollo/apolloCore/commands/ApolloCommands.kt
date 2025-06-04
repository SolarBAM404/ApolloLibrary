@file:JvmName("ApolloCommands")
package me.solar.apollo.apolloCore.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.ArgumentCommandNode
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import me.solar.apollo.apolloCore.ApolloPlugin

fun createCommand(
    name: String,
    arguments: List<ArgumentCommandNode<CommandSourceStack, Any>>? = null,
    action: (CommandContext<CommandSourceStack>) -> Unit
): LiteralArgumentBuilder<CommandSourceStack> {

    var commandBuilder: LiteralArgumentBuilder<CommandSourceStack> = LiteralArgumentBuilder.literal(name)

    if (arguments != null) {
        for (i in arguments.indices) {
            commandBuilder.then(arguments[i])
        }
    }
    commandBuilder.executes { context ->
        action(context)
        1 // Return 1 to indicate success
    }
    return commandBuilder
}

fun createCommand(
    name: String,
    action: (CommandContext<CommandSourceStack>) -> Unit
): LiteralArgumentBuilder<CommandSourceStack> {
    return createCommand(name, null, action)
}

fun createCommand(
    name: String
): LiteralArgumentBuilder<CommandSourceStack> {
    return createCommand(name, null) { context ->
        // Default action does nothing
    }

}

fun registerCommand(
    command: LiteralCommandNode<CommandSourceStack>
) {
    ApolloPlugin.instance.lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, { event ->
        event.registrar().register(command)
    })
}

fun registerCommand(
    command: LiteralArgumentBuilder<CommandSourceStack>
) {
    ApolloPlugin.instance.lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, { event ->
        event.registrar().register(command.build())
    })
}

fun registerCommands(
    commands: List<LiteralCommandNode<CommandSourceStack>>
) {
    ApolloPlugin.instance.lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, { event ->
        for (command in commands) {
            event.registrar().register(command)
        }
    })
}