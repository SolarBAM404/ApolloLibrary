package me.solar.apolloLibrary.commands;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import me.solar.apolloLibrary.utils.Common;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a subcommand that can be executed within a larger command structure.
 */
public abstract class SubCommand {

    @Getter
    private final List<SubCommand> subCommands = new ArrayList<>();

    /**
     * Retrieves the name of the subcommand.
     *
     * @return Name of the subcommand; defaults to the class name.
     */
    public String name() {
        return this.getClass().getSimpleName();
    }

    /**
     * Determines if the command is exclusive to players.
     *
     * @return True if the command is player-only; false otherwise.
     */
    public boolean isPlayerOnly() {
        return true;
    }

    /**
     * Provides a set of help information lines related to this subcommand.
     *
     * @return Array of strings detailing help information.
     */
    public String[] helpInfo() {
        return new String[0];
    }

    /**
     * Lists the permission nodes required to execute this subcommand.
     *
     * @return Array of permission nodes required to use the command.
     */
    public String[] permissions() {
        return new String[]{"sparky.command." + name().toLowerCase()};
    }

    /**
     * Lists alternative names by which the subcommand can be invoked.
     *
     * @return List of aliases for the subcommand.
     */
    public List<String> aliases() {
        return List.of();
    }

    /**
     * Executes the logic associated with the subcommand.
     *
     * @param sender Sender of the command, could be a player or the console.
     * @param args   List of arguments provided along with the subcommand.
     */
    public void onCommand(@NotNull CommandSender sender, @NotNull List<String> args) {
        SubCommand command = getSubCommand(args.get(0));
        if (command != null) {
            List<String> newArgs = new ArrayList<>(args);
            newArgs.removeFirst();
            command.onCommand(sender, newArgs);
        }
    }

    /**
     * Assists in completing the command by providing relevant suggestions.
     *
     * @param sender Sender of the command.
     * @param args   Current list of arguments provided with the command.
     * @return List of suggested arguments based on current input.
     */
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull List<String> args) {
        if (args.isEmpty()) {
            return getSubCommandNames();
        } else if (args.size() == 1) {
            List<String> subCommandNames = getSubCommandNames();
            List<String> suggestions = new ArrayList<>();
            for (String subCommandName : subCommandNames) {
                if (subCommandName.startsWith(args.get(0))) {
                    suggestions.add(subCommandName);
                }
            }
            return suggestions;
        }

        SubCommand subCommand = getSubcommand(args.get(0));
        if (subCommand != null) {
            ArrayList<String> subArgs = new ArrayList<>(args);
            subArgs.removeFirst();
            try {
                return subCommand.onTabComplete(sender, subArgs);
            } catch (Exception e) {
                Common.tell(sender, "An error occurred while tab completing the command.");
                throw new CommandException("An error occurred while tab completing the command.", e);
            }
        }
        return null;
    }

    private List<String> getSubCommandNames() {
        List<String> subCommandNames = new ArrayList<>();
        for (SubCommand subCommand : subCommands) {
            subCommandNames.add(subCommand.name());
        }
        return subCommandNames;
    }

    private SubCommand getSubcommand(String commandName) {
        for (SubCommand subCmd : subCommands) {
            if (subCmd.name().equalsIgnoreCase(commandName) || subCmd.aliases().contains(commandName.toLowerCase())) {
                return subCmd;
            }
        }
        return null;
    }

    public SubCommand getSubCommand(String arg) {
        List<SubCommand> subCommands = getSubCommands();
        for (SubCommand subCommand : subCommands) {
            if (subCommand.name().equalsIgnoreCase(arg)) {
                return subCommand;
            }
        }
        return null;
    }

    protected void addSubCommand(SubCommand subCommand) {
        getSubCommands().add(subCommand);
    }
}