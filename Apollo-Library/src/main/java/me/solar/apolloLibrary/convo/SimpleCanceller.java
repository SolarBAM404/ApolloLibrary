package me.solar.apolloLibrary.convo;

import java.util.List;

import me.solar.apolloLibrary.utils.Valid;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationContext;
import org.jetbrains.annotations.NotNull;

public record SimpleCanceller(List<String> cancelPhrases)
        implements ConversationCanceller {
    public SimpleCanceller(String... cancelPhrases) {
        this(List.of(cancelPhrases));
    }

    public SimpleCanceller {
        Valid.checkBoolean(!cancelPhrases.isEmpty(), "Cancel phrases cannot be empty");

    }


    public void setConversation(@NotNull Conversation conversation) {
    }


    public boolean cancelBasedOnInput(@NotNull ConversationContext context, @NotNull String input) {
        for (String phrase : this.cancelPhrases) {
            if (input.equalsIgnoreCase(phrase))
                return true;
        }
        return false;
    }

    @NotNull
    @Override
    public ConversationCanceller clone() {
        return new SimpleCanceller(this.cancelPhrases);
    }
}