package com.thoxia.playerads.mute.impl;

import com.thoxia.playerads.mute.MuteManager;
import org.bukkit.entity.Player;

public class EmptyMute implements MuteManager {
    @Override
    public boolean isMuted(Player player) {
        return false;
    }

    @Override
    public String getName() {
        return "Empty";
    }

}
