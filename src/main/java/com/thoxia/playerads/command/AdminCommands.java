package com.thoxia.playerads.command;

import com.thoxia.playerads.PlayerAdsPlugin;
import com.thoxia.playerads.ad.Ad;
import com.thoxia.playerads.util.ChatUtils;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;

@RequiredArgsConstructor
@Command(value = "playeradsadmin", alias = {"reklamadmin", "adsadmin", "paadmin"})
public class AdminCommands extends BaseCommand {

    private final PlayerAdsPlugin plugin;

    @SubCommand("reload")
    @Permission("playerads.command.reload")
    public void onReload(CommandSender sender) {
        plugin.getPluginConfig().load(true);
        plugin.getPluginMessages().load(true);
        plugin.getSpotsConfig().create();
        plugin.getMenuConfig().create();
        plugin.getHooksConfig().create();
        plugin.getWebhookConfig().create();

        plugin.getAdManager().init();

        ChatUtils.sendMessage(sender, ChatUtils.format(plugin.getPluginMessages().getReloaded()));
    }

    @SubCommand("removeads")
    @Permission("playerads.command.removeads")
    public void onRemove(CommandSender sender, @Suggestion("online-players") String target) {
        plugin.getAdManager().removeAds(target).exceptionally(throwable -> {
            PlayerAdsPlugin.getInstance().getLogger().log(Level.SEVERE,
                    "An exception was found whilst removing ads!", throwable);
            return false;
        }).thenAccept(removed -> {
            if (removed) {
                ChatUtils.sendMessage(sender, ChatUtils.format(plugin.getPluginMessages().getRemovedAllAdmin(),
                        Placeholder.unparsed("player", target)));
            } else {
                ChatUtils.sendMessage(sender, ChatUtils.format(plugin.getPluginMessages().getAdNotFoundAdmin()));
            }
        });
    }

    @SubCommand("removespot")
    @Permission("playerads.command.removespot")
    public void onRemoveSpot(CommandSender sender, Integer slot) {
        plugin.getAdManager().getAd(plugin.getAdManager().getSpot(slot))
                .exceptionally(throwable -> {
                    PlayerAdsPlugin.getInstance().getLogger().log(Level.SEVERE,
                            "An exception was found whilst removing ads by spot!", throwable);
                    return null;
                })
                .thenCompose(ad -> plugin.getAdManager().removeAds(ad))
                .thenAccept(removed -> {
                    if (removed) {
                        ChatUtils.sendMessage(sender, ChatUtils.format(plugin.getPluginMessages().getRemovedSpotAdmin(),
                                Placeholder.unparsed("slot", String.valueOf(slot))));
                    } else {
                        ChatUtils.sendMessage(sender, ChatUtils.format(plugin.getPluginMessages().getAdNotFoundAdmin()));
                    }
                });
    }

    @SubCommand("removeall")
    @Permission("playerads.command.removeall")
    public void onRemoveAll(CommandSender sender) {
        plugin.getAdManager().getAds()
                .exceptionally(throwable -> {
                    PlayerAdsPlugin.getInstance().getLogger().log(Level.SEVERE,
                            "An exception was found whilst removing all ads!", throwable);
                    return null;
                })
                .thenCompose(ad -> plugin.getAdManager().removeAds(ad.toArray(new Ad[0])))
                .thenAccept(removed -> {
                    if (removed) {
                        ChatUtils.sendMessage(sender, ChatUtils.format(plugin.getPluginMessages().getClearedAllAdmin()));
                    } else {
                        ChatUtils.sendMessage(sender, ChatUtils.format(plugin.getPluginMessages().getAdNotFoundAdmin()));
                    }
                });
    }

}
