package com.thoxia.playerads.task;

import com.thoxia.playerads.PlayerAdsPlugin;
import com.thoxia.playerads.ad.Ad;
import com.thoxia.playerads.util.ChatUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class AdCheckerTask extends BukkitRunnable {

    private final PlayerAdsPlugin plugin;

    @Override
    public void run() {
        plugin.getAdManager().getAds().thenAccept(ads -> {
            for (Ad ad : ads) {
                if (ad.getCreationTime() + ad.getSpot().getDuration() <= System.currentTimeMillis()) {
                    plugin.getAdManager().removeAd(ad);

                    Player player = Bukkit.getPlayerExact(ad.getPlayerName());
                    if (plugin.getPluginConfig().isNotifyOwnerOnExpire() && player != null)
                        ChatUtils.sendMessage(player, ChatUtils.format(plugin.getPluginMessages().getExpired()));

                    if (plugin.getPluginConfig().isAnnounceOnExpire())
                        plugin.getAdventure().all()
                                .sendMessage(ChatUtils.format(plugin.getPluginMessages().getExpiredAnnouncement()));
                }
            }
        });
    }

}
