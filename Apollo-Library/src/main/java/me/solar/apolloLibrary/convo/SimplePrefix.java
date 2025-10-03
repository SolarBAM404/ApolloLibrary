package me.solar.apolloLibrary.convo;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationPrefix;
import org.jetbrains.annotations.NotNull;

public class SimplePrefix
        implements ConversationPrefix {
    private final String prefix;

    public SimplePrefix(String prefix) {
        this.prefix = prefix;
    }

    @NotNull
    public String getPrefix(@NotNull ConversationContext context) {
        return this.prefix;
    }
}