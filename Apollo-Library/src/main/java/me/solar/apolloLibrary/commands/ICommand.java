package me.solar.apolloLibrary.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import org.bukkit.command.CommandSender;

public interface ICommand {

    LiteralCommandNode<CommandSender> build();

}
