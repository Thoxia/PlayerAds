package com.thoxia.playerads.papi;

import com.thoxia.playerads.PlayerAdsPlugin;
import com.thoxia.playerads.ad.Ad;
import com.thoxia.playerads.util.ChatUtils;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public class PapiHook extends PlaceholderExpansion {

    private final PlayerAdsPlugin plugin;

    @Override
    public @NotNull String getIdentifier() {
        return "playerads";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        // playerads_last_1
        if (params.startsWith("last")) {
            int place = Integer.parseInt(params.split("_")[1]);
            List<Ad> list = plugin.getAdManager().getCachedAds().stream()
                    .sorted(Comparator.comparingLong(Ad::getCreationTime).reversed()).toList();
            if (list.size() > place) {
                Ad ad = list.get(place);
                // PlaceholderAPI does not support Components, yet. :(
                return ChatUtils.LEGACY_AMPERSAND.serialize(ChatUtils.format(
                        plugin.getPluginMessages().getPlaceholderFormat(),
                        Placeholder.unparsed("player", ad.getPlayerName()),
                        Placeholder.component("message", ChatUtils.formatAdMessage(ad.getMessage(), plugin))
                ));
            } else {
                // PlaceholderAPI does not support Components, yet. :(
                return ChatUtils.LEGACY_AMPERSAND.serialize(
                        ChatUtils.format(plugin.getPluginMessages().getEmptyPlaceholder())
                );
            }
        }

        return null;
    }

}
