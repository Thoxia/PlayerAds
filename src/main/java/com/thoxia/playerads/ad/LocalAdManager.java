package com.thoxia.playerads.ad;

import com.thoxia.playerads.PlayerAdsPlugin;
import com.thoxia.playerads.config.Config;
import com.thoxia.playerads.gui.MainGui;
import com.thoxia.playerads.util.ChatUtils;
import com.thoxia.playerads.util.PermissionUtils;
import com.thoxia.playerads.util.SkinUtils;
import com.thoxia.playerads.util.webhook.WebhookUtils;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import sh.okx.timeapi.TimeAPI;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

@RequiredArgsConstructor
public class LocalAdManager implements AdManager {

    private final PlayerAdsPlugin plugin;
    private final Map<Ad, BossBar> bossBarMap = new ConcurrentHashMap<>();
    private final Map<Integer, AdSpot> spotMap = new ConcurrentHashMap<>();
    private final Map<AdSpot, Ad> adMap = new ConcurrentHashMap<>();
    private final Map<String, AdPreset> presetMap = new ConcurrentHashMap<>();

    private long lastAd;

    @Override
    public void init() {
        this.spotMap.clear();

        ConfigurationSection spots = plugin.getSpotsConfig().getConfigurationSection("spots");
        if (spots == null) return;

        ConfigurationSection presets = plugin.getSpotsConfig().getConfigurationSection("presets");
        if (presets == null) return;

        for (String key : presets.getKeys(false)) {
            String message = presets.getString(key + ".message");
            List<String> loreTexts = presets.getStringList(key + ".lore");

            this.presetMap.put(key, new AdPreset(key, message, loreTexts));
        }

        for (String key : spots.getKeys(false)) {
            List<Integer> slots = spots.getIntegerList(key + ".slots");
            double price = spots.getDouble(key + ".price");
            String time = spots.getString(key + ".time");
            String presetName = spots.getString(key + ".preset");
            long duration = new TimeAPI(time).getMilliseconds();
            for (Integer slot : slots) {
                spotMap.put(slot, new AdSpot(slot, price, duration, presetName == null ? null : this.presetMap.get(presetName)));
            }
        }
    }

    @Override
    public Collection<Ad> getCachedAds() {
        return new ArrayList<>(this.adMap.values());
    }

    @Override
    public void clearCache() {
        // do nothing
    }

    @Override
    public boolean addToCache(Ad ad) {
        return false;
    }

    @Override
    public boolean removeFromCache(Ad ad) {
        return false;
    }

    @Override
    public CompletableFuture<Collection<Ad>> getAds() {
        return CompletableFuture.completedFuture(new ArrayList<>(this.adMap.values()));
    }

    @Override
    public Collection<AdSpot> getSpots() {
        return spotMap.values();
    }

    @Override
    public AdSpot getSpot(int slot) {
        return spotMap.get(slot);
    }

    @Override
    public CompletableFuture<Ad> getAd(AdSpot spot) {
        return CompletableFuture.completedFuture(this.adMap.get(spot));
    }

    @Override
    public CompletableFuture<Collection<Ad>> getAds(String player) {
        return CompletableFuture.completedFuture(this.adMap.values().stream()
                .filter(Objects::nonNull).filter(ad -> ad.getPlayerName().equals(player)).toList());
    }

    @Override
    public void addAd(Ad ad) {
        this.adMap.put(ad.getSpot(), ad);
    }

    @Override
    public void postAd(Ad ad, boolean postWebhooks) {
        lastAd = System.currentTimeMillis();

        addAd(ad);

        Audience all = plugin.getAdventure().all();
        all.showTitle(Title.title(
                ChatUtils.format(plugin.getPluginConfig().getTitleSettings().getTitleMessage(),
                        Placeholder.unparsed("player", ad.getPlayerName()),
                        Placeholder.component("message", ChatUtils.formatAdMessage(ad.getMessage(), plugin))
                ),
                ChatUtils.format(plugin.getPluginConfig().getTitleSettings().getSubtitleMessage(),
                        Placeholder.unparsed("player", ad.getPlayerName()),
                        Placeholder.component("message", ChatUtils.formatAdMessage(ad.getMessage(), plugin))
                ),
                Title.Times.times(
                        Duration.ofMillis(plugin.getPluginConfig().getTitleSettings().getFadeIn()),
                        Duration.ofMillis(plugin.getPluginConfig().getTitleSettings().getStay()),
                        Duration.ofMillis(plugin.getPluginConfig().getTitleSettings().getFadeOut())
                )
        ));

        all.sendActionBar(
                ChatUtils.format(plugin.getPluginConfig().getTitleSettings().getActionBarMessage())
        );

        Config.BarSettings barSettings = plugin.getPluginConfig().getBarSettings();
        BossBar bar = BossBar.bossBar(ChatUtils.format(barSettings.getMessage(),
                        Placeholder.component("message", ChatUtils.formatAdMessage(ad.getMessage(), plugin))),
                1f, barSettings.getColor(), barSettings.getOverlay()
        );
        all.showBossBar(bar);

        bossBarMap.put(ad, bar);

        ChatUtils.format(plugin.getPluginMessages().getAdMessage(),
                Placeholder.component("message", ChatUtils.formatAdMessage(ad.getMessage(), plugin)),
                Placeholder.unparsed("player", ad.getPlayerName())
        ).forEach(all::sendMessage);

        if (postWebhooks)
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> WebhookUtils.send(ad, plugin));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (bar.progress() <= 0.0f) {
                    all.hideBossBar(bar);
                    cancel();
                    return;
                }

                int timer = barSettings.getSeconds() * 1000;
                float multiplier = 250f / timer;
                float secondsLeft = (timer - (System.currentTimeMillis() - ad.getCreationTime())) / 250f;
                bar.progress(Math.min(1.0F, Math.max(0, secondsLeft * multiplier)));
            }
        }.runTaskTimer(plugin, 5L, 5L);
    }

    @Override
    public CompletableFuture<Boolean> removeAds(Ad... ads) {
        boolean removedAll = false;

        for (Ad ad : ads) {
            Audience all = plugin.getAdventure().all();
            removedAll = adMap.values().removeIf(advertisement -> ad.getSpot().equals(advertisement.getSpot()));
            BossBar bar = this.bossBarMap.remove(ad);
            if (bar != null)
                bar.removeViewer(all);
        }

        return CompletableFuture.completedFuture(removedAll);
    }

    @Override
    public CompletableFuture<Long> getLastAdvertisementTime() {
        return CompletableFuture.completedFuture(lastAd);
    }

    @Override
    public void createAd(Player player, AdSpot spot, String message) {
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

                // foolish litebans
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

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
                            ChatUtils.sendMessage(player, ChatUtils.format(plugin.getPluginMessages().getMutedMessage()));
                            if (plugin.getPluginMessages().isMutedTitleEnabled())
                                ChatUtils.sendTitle(player, ChatUtils.format(plugin.getPluginMessages().getMutedTitle()), ChatUtils.format(plugin.getPluginMessages().getMutedSubtitle()));
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

                        plugin.getAdManager().postAd(new Ad(player.getName(), message, spot, SkinUtils.getSkin(player)), true);

                        if (plugin.getPluginConfig().isReopenGuiAfterPurchase())
                            Bukkit.getScheduler().runTaskLater(plugin, () -> MainGui.open(player, plugin), 5);
                    });
                });
            });
        });
    }

}
