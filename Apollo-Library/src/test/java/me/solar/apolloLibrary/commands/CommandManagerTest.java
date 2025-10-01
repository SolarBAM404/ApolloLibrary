package me.solar.apolloLibrary.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommandManagerTest {
    private JavaPlugin plugin;
    private CommandManager manager;
    private CommandSender sender;

    @BeforeEach
    void setup() {
        plugin = mock(JavaPlugin.class, RETURNS_DEEP_STUBS);
        sender = mock(CommandSender.class, RETURNS_DEEP_STUBS);
        manager = new CommandManager(plugin, "test"); // Use a valid command name from plugin.yml for integration
    }

    @Test
    void testExecuteValidSubCommand() {
        // Arrange: Register a test subcommand
        SubCommand sub = mock(SubCommand.class);
        when(sub.name()).thenReturn("hello");
        when(sub.permissions()).thenReturn(new String[]{}); // No perm needed
        when(sub.isPlayerOnly()).thenReturn(false);
        manager.addSubcommand(sub);

        // Act: Call the command with "hello"
        boolean result = manager.onCommand(sender, null, "test", new String[]{"hello"});

        // Assert
        verify(sub).onCommand(eq(sender), anyList());
        assertTrue(result);
    }

    @Test
    void testPlayerOnlySubCommand_withConsoleSender() {
        SubCommand sub = mock(SubCommand.class);
        when(sub.name()).thenReturn("playercmd");
        when(sub.permissions()).thenReturn(new String[]{});
        when(sub.isPlayerOnly()).thenReturn(true);
        manager.addSubcommand(sub);

        boolean result = manager.onCommand(sender, null, "test", new String[]{"playercmd"});
        verify(sub, never()).onCommand(any(), anyList());
        assertFalse(result);
    }
}