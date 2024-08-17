package com.thoxia.playerads.command;

import com.thoxia.playerads.PlayerAdsPlugin;
import com.thoxia.playerads.gui.MainGui;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@Command(value = "playerads", alias = {"ads", "advertisement", "advertisements", "reklam", "reklamver"})
public class MainCommand extends BaseCommand {

    private final PlayerAdsPlugin plugin;

    @Default
    public void defaultCommand(Player player) {
        MainGui.open(player, plugin);
    }

}
