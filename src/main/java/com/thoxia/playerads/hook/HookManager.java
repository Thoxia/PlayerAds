package com.thoxia.playerads.hook;

import com.thoxia.playerads.PlayerAdsPlugin;
import com.thoxia.playerads.economy.EconomyManager;
import com.thoxia.playerads.economy.impl.VaultEconomyManager;
import com.thoxia.playerads.mute.MuteManager;
import com.thoxia.playerads.mute.impl.EmptyMute;
import com.thoxia.playerads.mute.impl.EssentialsMute;
import com.thoxia.playerads.mute.impl.LitebansMute;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class HookManager {

    private final Map<String, Class<? extends MuteManager>> muteHooks = new HashMap<>();
    private final Map<String, Class<? extends EconomyManager>> economyHooks = new HashMap<>();

    private final PlayerAdsPlugin plugin;

    @Getter private MuteManager muteManager;
    @Getter private EconomyManager economyManager;

    public void init() {
        muteHooks.put("Essentials", EssentialsMute.class);
        muteHooks.put("LiteBans", LitebansMute.class);

        economyHooks.put("Vault", VaultEconomyManager.class);
    }

    @SneakyThrows
    public void initMuteManager() {
        for (Map.Entry<String, Class<? extends MuteManager>> entry : muteHooks.entrySet()) {
            Plugin p = plugin.getServer().getPluginManager().getPlugin(entry.getKey());
            if (p != null && plugin.getHooksConfig().getBoolean("hooks.mute." + entry.getKey())) {
                muteManager = entry.getValue().getDeclaredConstructor().newInstance();
                break;
            }
        }

        if (muteManager == null)
            muteManager = new EmptyMute();

        plugin.getLogger().info("Using " + muteManager.getName() + " as mute manager.");
    }

    @SneakyThrows
    public void initEconomyManager() {
        for (Map.Entry<String, Class<? extends EconomyManager>> entry : economyHooks.entrySet()) {
            Plugin p = plugin.getServer().getPluginManager().getPlugin(entry.getKey());
            if (p != null && plugin.getHooksConfig().getBoolean("hooks.economy." + entry.getKey())) {
                economyManager = entry.getValue().getDeclaredConstructor().newInstance();
                economyManager.init();
                plugin.getLogger().info("Using " + economyManager.getName() + " as economy manager.");
                break;
            }
        }

        if (economyManager == null) {
            plugin.getLogger().info("Could not find any supported economy plugin! Disabling...");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

}
