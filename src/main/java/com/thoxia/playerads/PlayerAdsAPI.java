package com.thoxia.playerads;

import com.thoxia.playerads.ad.AdManager;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class PlayerAdsAPI extends JavaPlugin {

    public static final String CREATE_PERMISSION = "playerads.create";

    private static PlayerAdsAPI api;

    public static PlayerAdsAPI getAPI() {
        return api;
    }

    public static void setInstance(PlayerAdsAPI api) {
        if (PlayerAdsAPI.api != null) {
            throw new RuntimeException("API is already set!");
        }

        PlayerAdsAPI.api = api;
    }

    public abstract AdManager getAdManager();

}
