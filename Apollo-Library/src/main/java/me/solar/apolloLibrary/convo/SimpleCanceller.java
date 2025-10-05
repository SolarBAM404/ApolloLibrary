package me.solar.apolloLibrary.convo;

import java.util.List;

import me.solar.apolloLibrary.utils.Valid;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationContext;
import org.jetbrains.annotations.NotNull;

public class SimpleCanceller
        implements ConversationCanceller {
    private final List<String> cancelPhrases;

    public SimpleCanceller(String... cancelPhrases) {
        this.cancelPhrases = List.of(cancelPhrases);
    }

    public SimpleCanceller(List<String> cancelPhrases) {
        Valid.checkBoolean(!cancelPhrases.isEmpty(), "Cancel phrases cannot be empty");

        this.cancelPhrases = cancelPhrases;
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
    public ConversationCanceller clone() {
        return new SimpleCanceller(this.cancelPhrases);
    }
}