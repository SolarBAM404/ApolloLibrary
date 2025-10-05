package me.solar.apolloLibrary.convo;

import me.solar.apolloLibrary.utils.Common;
import me.solar.apolloLibrary.utils.Valid;
import me.solar.apolloLibrary.utils.Variables;
import net.kyori.adventure.audience.Audience;
import org.apache.commons.lang3.SerializationException;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ConvoPrompt extends ValidatingPrompt {
    private boolean openMenu;
    private Player player;
    private final JavaPlugin plugin;

    protected ConvoPrompt(JavaPlugin plugin) {
        this(true, plugin);
    }

    protected ConvoPrompt(boolean openMenu, JavaPlugin plugin) {
        this.plugin = plugin;
        this.player = null;
        this.openMenu = openMenu;
    }

    protected String getCustomPrefix() {
        return null;
    }

    protected boolean isModal() {
        return true;
    }

    @NotNull
    public String getPromptText(@NotNull ConversationContext context) {
        try {
            String prompt = this.getPrompt(context);

            return Variables.replace(prompt, this.getPlayer(context));
        } catch (Throwable $ex) {
            throw $ex;
        }
    }

    private Player getPlayer(@NotNull ConversationContext context) {
        if (this.player == null) {
            this.player = (Player)context.getForWhom();
        }

        return this.player;
    }

    @NotNull
    protected abstract String getPrompt(@NotNull ConversationContext var1);

    public boolean blocksForInput(@NotNull ConversationContext context) {
        return true;
    }

    protected final void tellLaterNoPrefix(int delayTicks, Conversable conversable, String message) {
        Common.tellLater((Audience) conversable, "{no-prefix}" + message, delayTicks);
    }

    @Nullable
    public Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        try {
            if (context.getForWhom() == this.player && this.isInputValid(context, input)) {
                return this.acceptValidatedInput(context, input);
            } else {
                String failedPrompt = this.getFailedValidationText(context, input);
                if (failedPrompt != null) {
                    Common.tell(this.getPlayer(context), failedPrompt);
                }

                return this;
            }
        } catch (Throwable $ex) {
            throw $ex;
        }
    }

    @Nullable
    protected String getFailedValidationText(@NotNull ConversationContext context, @NotNull String invalidInput) {
        return null;
    }

    protected boolean isInputValid(@NotNull ConversationContext context, @NotNull String input) {
        return true;
    }

    public void onConversationEnd(SimpleConversation simpleConversation, @NotNull ConversationAbandonedEvent abandonedEvent) {
    }

    public final Conversation show(Player player) {
        try {
            try {
                Valid.checkBoolean(!player.isConversing(), "Player " + player.getName() + " is already in a conversation!");
            } catch (SerializationException e) {
                throw new RuntimeException(e);
            }

            SimpleConversation conversation = new SimpleConversation(plugin) {
                protected Prompt getFirstPrompt() {
                    return ConvoPrompt.this;
                }

                protected boolean isModal() {
                    return ConvoPrompt.this.isModal();
                }

                protected ConversationPrefix getPrefix() {
                    String prefix = ConvoPrompt.this.getCustomPrefix();
                    return (ConversationPrefix)(prefix != null ? new SimplePrefix(prefix) : super.getPrefix());
                }

                protected void onConversationEnd(@NotNull ConversationAbandonedEvent abandonedEvent, boolean cancelledForInactivity) {
                    try {
                        String message = cancelledForInactivity ? "Conversation was cancelled for inactivity" : "Conversation was ended";

                        Player player = ConvoPrompt.this.getPlayer(abandonedEvent.getContext());
                        if (!abandonedEvent.gracefulExit()) {
                            Common.tell(player, message);
                        }

                    } catch (Throwable $ex) {
                        throw $ex;
                    }
                }
            };
            return conversation.start(player);
        } catch (Throwable $ex) {
            throw $ex;
        }
    }

    public static void show(Player player, ConvoPrompt prompt) {
        prompt.show(player);
    }
}