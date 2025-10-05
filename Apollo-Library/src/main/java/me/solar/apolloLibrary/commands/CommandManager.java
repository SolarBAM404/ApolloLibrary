package me.solar.apolloLibrary.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.solar.apolloLibrary.utils.Common;
import me.solar.apolloLibrary.exceptions.ApolloCommandException;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * CommandManager class is responsible for handling the execution of commands
 * and their corresponding sub-commands.
 */
public class CommandManager implements TabExecutor {

    private final JavaPlugin plugin;

    private final List<SubCommand> subCommands = new ArrayList<>();

    protected String commandName;

    /**
     * Constructor to set the plugin class and register the command executor.
     *
     * @param plugin      Reference to the main plugin instance.
     * @param commandName Name of the command to register.
     */
    protected CommandManager(JavaPlugin plugin, String commandName) {
        this.plugin = plugin;
        this.commandName = commandName;
        PluginCommand command = plugin.getCommand(commandName);
        if (command != null) {
            command.setExecutor(this);
            return;
        }

        plugin.getLogger().severe(() -> "Failed to register command: " + commandName);
        plugin.getLogger().severe("This is likely due to the command not being registered in the plugin.yml file.");
    }

    /**
     * Executes the command when called.
     *
     * @param sender  Command sender.
     * @param command Command that was executed.
     * @param label   Label of the command.
     * @param args    Arguments passed with the command.
     * @return true if the command was executed successfully, false otherwise.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>You need to enter a subcommand to use this command");
            return false;
        }

        SubCommand subCommand = getSubcommand(args[0]);
        if (subCommand == null) {
            sendMessage(sender, "<red>No sub command has been found with that name");
            return false;
        }

        if (!(sender instanceof Player) && subCommand.isPlayerOnly()) {
            sendMessage(sender, "<red>Only players can use this command");
            return false;
        }

        if (!hasPermission(sender, subCommand)) {
            sendMessage(sender, "<red>You do not have valid permission to use this command.");
            return false;
        }

        List<String> subArgs = new ArrayList<>(Arrays.asList(args));
        subArgs.remove(0);
        subCommand.onCommand(sender, subArgs);
        return true;
    }

    /**
     * Provides a list of available commands or subcommands for tab completion.
     *
     * @param sender  Command sender.
     * @param command Command that was executed.
     * @param label   Label of the command.
     * @param args    Arguments passed with the command.
     * @return List of available commands or subcommands.
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return getSubCommandNames(args[0]);
        }

        SubCommand subCommand = getSubcommand(args[0]);
        if (subCommand != null) {
            ArrayList<String> subArgs = new ArrayList<>(Arrays.asList(args));
            subArgs.remove(0);
            try {
                return subCommand.onTabComplete(sender, subArgs);
            } catch (Exception e) {
                Common.tell(sender, "<red>You have encountered an error while tab completing the command. Please contact a server administrator.</red>");
                throw new ApolloCommandException("An error occurred while tab completing the command.", e);
            }
        }
        return null;
    }

    /**
     * Retrieves a list of all registered sub command names.
     *
     * @return List of sub command names.
     */
    private List<String> getSubCommandNames() {
        List<String> subCommandNames = new ArrayList<>();
        for (SubCommand subCommand : subCommands) {
            subCommandNames.add(subCommand.name());
        }
        return subCommandNames;
    }

    private List<String> getSubCommandNames(String startsWith) {
        List<String> subCommandNames = new ArrayList<>();
        for (SubCommand subCommand : subCommands) {
            if (subCommand.name().startsWith(startsWith)) {
                subCommandNames.add(subCommand.name());
            }
        }
        return subCommandNames;
    }

    /**
     * Checks if the sender has the required permissions for a sub command.
     *
     * @param sender     Command sender.
     * @param subCommand Sub command to check.
     * @return true if sender has permission, false otherwise.
     */
    private boolean hasPermission(CommandSender sender, SubCommand subCommand) {
        for (String perm : subCommand.permissions()) {
            if (Bukkit.getPluginManager().getPermission(perm) == null) {
                Bukkit.getPluginManager().addPermission(new Permission(perm));
            }
            if (!sender.hasPermission(perm)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retrieves the SubCommand associated with the given command name.
     *
     * @param commandName Name of the command.
     * @return Corresponding SubCommand or null if not found.
     */
    private SubCommand getSubcommand(String commandName) {
        for (SubCommand subCmd : subCommands) {
            if (subCmd.name().equalsIgnoreCase(commandName) || subCmd.aliases().contains(commandName.toLowerCase())) {
                return subCmd;
            }
        }
        return null;
    }

    /**
     * Sends an error message to the sender. Uses messages from config or a default message if not found.
     *
     * @param sender         Command sender.
     * @param defaultMessage Default message to send
     */
    protected void sendMessage(CommandSender sender, String defaultMessage) {
        Common.tell(sender, defaultMessage);
    }

    /**
     * Registers a new sub command.
     *
     * @param subCommand Sub command to register.
     */
    protected void addSubcommand(SubCommand subCommand) {
        subCommands.add(subCommand);
    }
}
