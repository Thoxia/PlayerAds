package com.thoxia.playerads.mute.impl;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.thoxia.playerads.mute.MuteManager;
import org.bukkit.entity.Player;

public class EssentialsMute implements MuteManager {

    @Override
    public boolean isMuted(Player player) {
        Essentials plugin = Essentials.getPlugin(Essentials.class);
        User user = plugin.getUser(player);
        if (user == null) return false;

        return user.isMuted();
    }

    @Override
    public String getName() {
        return "Essentials";
    }

}
