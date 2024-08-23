package com.thoxia.playerads.mute.impl;

import com.thoxia.playerads.mute.MuteManager;
import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.utils.Punishment;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AdvancedbanMute implements MuteManager {

    @Override
    public boolean isMuted(Player player) {
        UUID playerUUID = player.getUniqueId();
        Punishment punishment = PunishmentManager.get().getMute(playerUUID.toString());
        return punishment != null && !punishment.isExpired();
    }

    @Override
    public String getName() {
        return "AdvancedBan";
    }
}
