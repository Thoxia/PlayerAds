package com.thoxia.playerads.convo;

import com.thoxia.playerads.PlayerAdsPlugin;
import com.thoxia.playerads.ad.Ad;
import com.thoxia.playerads.ad.AdSpot;
import com.thoxia.playerads.gui.MainGui;
import com.thoxia.playerads.util.ChatUtils;
import com.thoxia.playerads.util.PermissionUtils;
import com.thoxia.playerads.util.SkinUtils;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

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
        plugin.getAdManager().getAds(player.getName()).exceptionally(throwable -> {
            PlayerAdsPlugin.getInstance().getLogger().log(Level.SEVERE,
                    "An exception was found whilst fetching ads by player!", throwable);
            return null;
        }).thenAccept(ads -> {
            int max = PermissionUtils.getPlayerAdLimit(player, plugin);
            if (ads.size() >= max) {
                ChatUtils.sendMessage(player, ChatUtils.format(plugin.getPluginMessages().getMax(),
                        Placeholder.unparsed("max", String.valueOf(max))));
                player.playSound(player.getLocation(), Sound.valueOf(plugin.getPluginConfig().getDenySound()), 1, 1);
                return;
            }

            plugin.getAdManager().getLastAdvertisementTime().exceptionally(throwable -> {
                PlayerAdsPlugin.getInstance().getLogger().log(Level.SEVERE,
                        "An exception was found whilst fetching last advertisement time!", throwable);
                return null;
            }).thenAccept(time -> {

                if (System.currentTimeMillis() <= time + (plugin.getPluginConfig().getWaitBeforeNewAd() * 1000L)) {
                    ChatUtils.sendMessage(player, ChatUtils.format(plugin.getPluginMessages().getWait()));
                    player.playSound(player.getLocation(), Sound.valueOf(plugin.getPluginConfig().getDenySound()), 1, 1);
                    return;
                }

                plugin.getAdManager().getAd(spot).exceptionally(throwable -> {
                    PlayerAdsPlugin.getInstance().getLogger().log(Level.SEVERE,
                            "An exception was found whilst fetching ads by spot!", throwable);
                    return null;
                }).thenAccept(ad -> {
                    if (ad != null) {
                        ChatUtils.sendMessage(player, ChatUtils.format(plugin.getPluginMessages().getTaken()));
                        player.playSound(player.getLocation(), Sound.valueOf(plugin.getPluginConfig().getDenySound()), 1, 1);
                        return;
                    }

                    if (plugin.getHookManager().getMuteManager().isMuted(player)) {
                        ChatUtils.sendMessage(player, ChatUtils.format(plugin.getPluginMessages().getMuted()));
                        player.playSound(player.getLocation(), Sound.valueOf(plugin.getPluginConfig().getDenySound()), 1, 1);
                        return;
                    }

                    if (!plugin.getEconomyManager().has(player, spot.getPrice())) {
                        ChatUtils.sendMessage(player, ChatUtils.format(plugin.getPluginMessages().getNotEnoughMoney()));
                        player.playSound(player.getLocation(), Sound.valueOf(plugin.getPluginConfig().getDenySound()), 1, 1);
                        return;
                    }

                    // no need to check if the text fulfills the requirements since it's already checked
                    plugin.getEconomyManager().remove(player, spot.getPrice());

                    ChatUtils.sendMessage(player, ChatUtils.format(plugin.getPluginMessages().getBought()));
                    player.playSound(player.getLocation(), Sound.valueOf(plugin.getPluginConfig().getBoughtSound()), 1, 1);

                    plugin.getAdManager().postAd(new Ad(player.getName(), input, spot, SkinUtils.getSkin(player)), true);

                    Bukkit.getScheduler().runTaskLater(plugin, () -> MainGui.open(player, plugin), 5);
                });
            });
        });

        return Prompt.END_OF_CONVERSATION;
    }

}
