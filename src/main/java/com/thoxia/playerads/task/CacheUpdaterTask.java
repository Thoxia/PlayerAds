package com.thoxia.playerads.task;

import com.thoxia.playerads.PlayerAdsPlugin;
import com.thoxia.playerads.ad.Ad;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.logging.Level;

@RequiredArgsConstructor
public class CacheUpdaterTask extends BukkitRunnable {

    private final PlayerAdsPlugin plugin;

    @Override
    public void run() {
        plugin.getAdManager().getAds().exceptionally(throwable -> {
            PlayerAdsPlugin.getInstance().getLogger().log(Level.SEVERE,
                    "An exception was found whilst fetching ads!", throwable);
            return new ArrayList<>();
        }).thenAccept(ads -> {
            plugin.getAdManager().clearCache();
            for (Ad ad : ads) {
                plugin.getAdManager().addToCache(ad);
            }
        });
    }

}
