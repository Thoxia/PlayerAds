package com.thoxia.playerads.util;

import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.entity.Player;

public class SkinUtils {

    private static final boolean isPaper;

    static {
        isPaper = hasClass("com.destroystokyo.paper.PaperConfig") || hasClass("io.papermc.paper.configuration.Configuration");
    }

    public static String getSkin(Player player) {
        if (!isPaper) return "";

        return player.getPlayerProfile().getProperties()
                .stream()
                .filter(property -> property.getName().equals("textures"))
                .map(ProfileProperty::getValue)
                .findAny()
                .orElse("");
    }

    private static boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
