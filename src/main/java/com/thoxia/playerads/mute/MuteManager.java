package com.thoxia.playerads.mute;

import com.thoxia.playerads.hook.Hook;
import org.bukkit.entity.Player;

public interface MuteManager extends Hook {

    boolean isMuted(Player player);

}
