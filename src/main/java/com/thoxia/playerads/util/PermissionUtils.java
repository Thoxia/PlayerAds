package com.thoxia.playerads.util;

import com.thoxia.playerads.PlayerAdsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Set;

public class PermissionUtils {

    public static int getPlayerAdLimit(Player player, PlayerAdsPlugin plugin) {
        int limit = plugin.getPluginConfig().getDefaultLimit();
        Set<PermissionAttachmentInfo> permissions = player.getEffectivePermissions();
        for (PermissionAttachmentInfo permission : permissions) {
            String permissionName = permission.getPermission();
            if (permissionName.startsWith("playerads.limit.")) {
                try {
                    String[] parts = permissionName.split("\\.");
                    int i = Integer.parseInt(parts[parts.length - 1]);
                    if (limit >= i) continue;

                    limit = i;
                } catch (NumberFormatException ignored) {}
            }
        }

        return limit;
    }

}
