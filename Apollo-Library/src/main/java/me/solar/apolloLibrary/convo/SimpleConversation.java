package me.solar.apolloLibrary.convo;

import lombok.Generated;
import lombok.SneakyThrows;
import me.solar.apolloLibrary.utils.Common;
import me.solar.apolloLibrary.utils.PlayerUtils;
import me.solar.apolloLibrary.utils.Valid;
import me.solar.apolloLibrary.collection.expiringmap.ExpiringMap;
import me.solar.apolloLibrary.menus.Menu;
import me.solar.apolloLibrary.runnables.RunnableObject;
import net.kyori.adventure.audience.Audience;
import org.apache.commons.lang3.SerializationException;
import org.bukkit.Sound;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class SimpleConversation implements ConversationAbandonedListener {
    private Menu menu;
    private final JavaPlugin plugin;
    public SimpleConversation(JavaPlugin plugin) {
        this(plugin, null);
    }

    public SimpleConversation(JavaPlugin plugin, Menu menu) {
        this.plugin = plugin;
        this.menu = menu;
    }

    @SneakyThrows
    public final CustomConversation start(Player player) {
        try {
            try {
                Valid.checkBoolean(!player.isConversing(), "You are already in a conversation!");
            } catch (SerializationException e) {
                throw new RuntimeException(e);
            }

            player.closeInventory();
            CustomConversation conversation = new CustomConversation(player);
            CustomCanceller canceller = new CustomCanceller();
            canceller.setConversation(conversation);
            conversation.getCancellers().add(canceller);
            conversation.getCancellers().add(this.getCanceller());
            conversation.addConversationAbandonedListener(this);
            conversation.begin();
            return conversation;
        } catch (Throwable e) {
            throw new ApolloConversationException("An error occurred while starting the conversation", e);
        }
    }

    protected abstract Prompt getFirstPrompt();

    public void conversationAbandoned(@NotNull ConversationAbandonedEvent abandonedEvent) {
        ConversationContext context = abandonedEvent.getContext();
        Conversable conversable = context.getForWhom();
        Map<Object, Object> sessionData = context.getAllSessionData();
        Object source = abandonedEvent.getSource();
        boolean timeout = (Boolean)sessionData.getOrDefault("FLIP#TIMEOUT", false);
        sessionData.remove("FLIP#TIMEOUT");
        if (source instanceof CustomConversation) {
            ConvoPrompt lastPrompt = ((CustomConversation)source).getLastConvoPrompt();
            if (lastPrompt != null) {
                lastPrompt.onConversationEnd(this, abandonedEvent);
            }
        }

        this.onConversationEnd(abandonedEvent, timeout);
        if (conversable instanceof Player player) {
            Sound sound = abandonedEvent.gracefulExit() ? Sound.ENTITY_ARROW_HIT : Sound.BLOCK_GLASS_BREAK;
            PlayerUtils.playSound(player, sound);
            if (this.menu != null && this.reopenMenu()) {
                this.menu.open(player);
            }
        }

    }

    protected void onConversationEnd(@NotNull ConversationAbandonedEvent abandonedEvent, boolean cancelledForInactivity) {
        this.onConversationEnd(abandonedEvent);
    }

    protected void onConversationEnd(@NotNull ConversationAbandonedEvent abandonedEvent) {
    }

    protected ConversationCanceller getCanceller() {
        return new SimpleCanceller("quit", "exit", "cancel");
    }

    @SneakyThrows
    protected ConversationPrefix getPrefix() {
        try {
            return new SimplePrefix("");
        } catch (Throwable e) {
            throw new ApolloConversationException("An error occurred while getting the conversation prefix", e);
        }
    }

    protected boolean insertPrefix() {
        return true;
    }

    protected boolean reopenMenu() {
        return true;
    }

    protected int getTimeoutSeconds() {
        return 60;
    }

    protected boolean isModal() {
        return true;
    }

    public final void setMenuToReturnTo(Menu menu) {
        this.menu = menu;
    }

    protected final void tellBoxed(int delayTicks, Conversable conversable, String... messages) {
        Common.runTaskLater(plugin, () -> tellBoxed(conversable, messages), delayTicks);
    }

    protected final void tellBoxed(Conversable conversable, String... messages) {
        Common.tell((Audience) conversable, messages);
    }

    protected final void tell(Conversable conversable, String message) {
        Common.tell((Audience)conversable, message);
    }

    protected final void tellLater(int delayTicks, Conversable conversable, String message) {
        Common.runTaskLater(plugin, () -> tell(conversable, message), delayTicks);
    }

    @Generated
    private Menu getMenu() {
        return this.menu;
    }

    final class CustomCanceller implements ConversationCanceller {
        private Conversation conversation;
        private final int timeoutSeconds = SimpleConversation.this.getTimeoutSeconds();
        private RunnableObject task;

        public CustomCanceller() {
        }

        public void setConversation(@NotNull Conversation conversation) {
            this.conversation = conversation;
            this.startTimer();
        }

        public boolean cancelBasedOnInput(@NotNull ConversationContext context, @NotNull String input) {
            this.stopTimer();
            this.startTimer();
            return false;
        }

        @SneakyThrows
        public @NotNull ConversationCanceller clone() {
            ConversationCanceller canceller = (ConversationCanceller) super.clone();
            return SimpleConversation.this.new CustomCanceller();
        }

        private void startTimer() {
            this.task = Common.runTaskLater(plugin, () -> {
                if (this.conversation.getState() == Conversation.ConversationState.UNSTARTED) {
                    this.startTimer();
                } else if (this.conversation.getState() == Conversation.ConversationState.STARTED) {
                    this.conversation.getContext().setSessionData("FLIP#TIMEOUT", true);
                    this.conversation.abandon(new ConversationAbandonedEvent(this.conversation, this));
                }

            }, (long)this.timeoutSeconds * 20L);
        }

        private void stopTimer() {
            if (this.task != null) {
                this.task.cancel();
            }

            this.task = null;
        }
    }

    public class CustomConversation extends Conversation {
        private ConvoPrompt lastConvoPrompt;

        public CustomConversation(Player player) {
            super(plugin, player, SimpleConversation.this.getFirstPrompt());
            this.localEchoEnabled = false;
            this.modal = SimpleConversation.this.isModal();
            if (SimpleConversation.this.insertPrefix() && SimpleConversation.this.getPrefix() != null) {
                this.prefix = SimpleConversation.this.getPrefix();
            }

        }

        @SneakyThrows
        public void outputNextPrompt() {
            try {
                if (this.currentPrompt == null) {
                    try {
                        this.abandon(new ConversationAbandonedEvent(this, SimpleConversation.this.getCanceller()));
                    } catch (Throwable e) {
                        String message = "<red>An error occurred while processing the conversation. Please try again";

                        Common.tell((Audience) this.getForWhom(), message);
                        Common.log("<red>An error occurred while processing the conversation. Please try again");
                        Common.log(e.getMessage());
                    }

                } else {
                    String promptClass = this.currentPrompt.getClass().getSimpleName();
                    String question = this.currentPrompt.getPromptText(this.context);

                    try {
                        Object sessionData = this.context.getSessionData("Asked_" + promptClass);
                        if (!(sessionData instanceof ExpiringMap)) {
                            sessionData = ExpiringMap.builder().expiration(SimpleConversation.this.getTimeoutSeconds(), TimeUnit.SECONDS).build();
                            this.context.setSessionData("Asked_" + promptClass, sessionData);
                        }
                        @SuppressWarnings("unchecked") ExpiringMap<String, Void> askedQuestions = (ExpiringMap<String, Void>) sessionData;
                        if (!askedQuestions.containsKey(question)) {
                            askedQuestions.put(question, null);
                            this.context.setSessionData("Asked_" + promptClass, askedQuestions);
                            Common.tell((Audience)this.getForWhom(), question);
                        }
                    } catch (NoSuchMethodError | ClassCastException e) {
                        Common.tell((Audience)this.getForWhom(), question);
                        Common.log("<red>An error occurred while processing the conversation. Please try again");
                        Common.log(e.getMessage());
                    }

                    if (this.currentPrompt instanceof ConvoPrompt) {
                        this.lastConvoPrompt = (ConvoPrompt)this.currentPrompt;
                    }

                    if (!this.currentPrompt.blocksForInput(this.context)) {
                        this.currentPrompt = this.currentPrompt.acceptInput(this.context, null);
                        this.outputNextPrompt();
                    }

                }
            } catch (Throwable e) {
                throw new ApolloConversationException("An error occurred while processing the conversation", e);
            }
        }

        @Generated
        private ConvoPrompt getLastConvoPrompt() {
            return this.lastConvoPrompt;
        }
    }
}

