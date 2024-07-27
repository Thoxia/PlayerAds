package com.thoxia.playerads.mute.impl;

import com.thoxia.playerads.mute.MuteManager;
import litebans.api.Database;
import org.bukkit.entity.Player;

public class LitebansMute implements MuteManager {

    @Override
    public boolean isMuted(Player player) {
        return Database.get().isPlayerMuted(player.getUniqueId(), null);
    }

    @Override
    public String getName() {
        return "LiteBans";
    }

}
