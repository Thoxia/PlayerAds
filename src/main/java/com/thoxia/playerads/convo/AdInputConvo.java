package com.thoxia.playerads.convo;

import com.thoxia.playerads.PlayerAdsPlugin;
import com.thoxia.playerads.ad.AdSpot;
import com.thoxia.playerads.util.ChatUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class AdInputConvo extends ValidatingPrompt {

    private final PlayerAdsPlugin plugin;
    private final AdSpot spot;

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext context) {
        return ChatUtils.LEGACY_AMPERSAND.serialize(ChatUtils.format(plugin.getPluginMessages().getInputStarted()));
    }

    @Override
    protected @Nullable String getFailedValidationText(@NotNull ConversationContext context, @NotNull String invalidInput) {
        return ChatUtils.LEGACY_AMPERSAND.serialize(ChatUtils.format(plugin.getPluginMessages().getInvalidInput()));
    }

    @Override
    protected boolean isInputValid(@NotNull ConversationContext context, @NotNull String input) {
        return input.length() >= plugin.getPluginConfig().getMinMessageLength()
                && input.length() <= plugin.getPluginConfig().getMaxMessageLength();
    }

    @Override
    protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
        Player player = (Player) context.getForWhom();
        // check if the spot is filled yet
        plugin.getAdManager().createAd(player, spot, input);

        return Prompt.END_OF_CONVERSATION;
    }

}
