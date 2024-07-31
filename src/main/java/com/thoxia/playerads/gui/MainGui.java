package com.thoxia.playerads.gui;

import com.thoxia.playerads.PlayerAdsAPI;
import com.thoxia.playerads.PlayerAdsPlugin;
import com.thoxia.playerads.ad.Ad;
import com.thoxia.playerads.ad.AdSpot;
import com.thoxia.playerads.config.YamlConfig;
import com.thoxia.playerads.convo.AdInputConvo;
import com.thoxia.playerads.gui.item.UpdatingItem;
import com.thoxia.playerads.util.ChatUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.builder.AbstractItemBuilder;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.builder.SkullBuilder;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;

public class MainGui {

    public static void open(Player player, PlayerAdsPlugin plugin) {
        YamlConfig config = plugin.getMenuConfig();

        ItemBuilder border = new ItemBuilder(Material.valueOf(config.getString("items.#.material")));
        border.setDisplayName(ChatUtils.formatForGui(config.getString("items.#.name")))
                .setCustomModelData(config.getInt("items.#.model"))
                .setLore(ChatUtils.formatForGui(config.getStringList("items.#.lore")));

        ItemBuilder info = new ItemBuilder(Material.valueOf(config.getString("items.?.material")));
        info.setDisplayName(ChatUtils.formatForGui(config.getString("items.?.name")))
                .setCustomModelData(config.getInt("items.?.model"))
                .setLore(ChatUtils.formatForGui(config.getStringList("items.?.lore")));

        Gui gui = Gui.normal()
                .setStructure(config.getStringList("structure").toArray(new String[0]))
                .addIngredient('#', border)
                .addIngredient('?', info)
                .build();

        plugin.getAdManager().getAds().exceptionally(throwable -> {
            PlayerAdsPlugin.getInstance().getLogger().log(Level.SEVERE,
                    "An exception was found whilst fetching ads!", throwable);
            return new ArrayList<>();
        }).thenAccept(ads -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                for (Ad ad : ads) {
                    Material material = Material.valueOf(config.getString("items.x.material"));
                    Supplier<? extends AbstractItemBuilder<?>> itemBuilder;
                    if (material == Material.PLAYER_HEAD) {
                        itemBuilder = () -> new SkullBuilder(new SkullBuilder.HeadTexture(ad.getSkinTexture()))
                                .setCustomModelData(config.getInt("items.x.model"))
                                .setDisplayName(ChatUtils.formatForGui(config.getString("items.x.name"),
                                        Placeholder.unparsed("player", ad.getPlayerName())
                                ))
                                .setLore(ChatUtils.formatForGui(config.getStringList("items.x.lore"),
                                        Placeholder.unparsed("player", ad.getPlayerName()),
                                        Placeholder.parsed("remaining", ChatUtils.formatTimeBetween(ad.getCreationTime() + ad.getSpot().getDuration())),
                                        Placeholder.component("message", ChatUtils.formatAdMessage(ad.getMessage(), plugin))
                                ));
                    } else {
                        itemBuilder = () -> new ItemBuilder(material)
                                .setCustomModelData(config.getInt("items.x.model"))
                                .setDisplayName(ChatUtils.formatForGui(config.getString("items.x.name"),
                                        Placeholder.unparsed("player", ad.getPlayerName())
                                ))
                                .setLore(ChatUtils.formatForGui(config.getStringList("items.x.lore"),
                                        Placeholder.unparsed("player", ad.getPlayerName()),
                                        Placeholder.parsed("remaining", ChatUtils.formatTimeBetween(ad.getCreationTime() + ad.getSpot().getDuration())),
                                        Placeholder.component("message", ChatUtils.formatAdMessage(ad.getMessage(), plugin))
                                ));
                    }

                    gui.setItem(ad.getSpot().getSlot(), new UpdatingItem(20, itemBuilder, event -> {
                        player.closeInventory();
                        String clickCommand = plugin.getPluginConfig().getClickCommand().replace("<player>", ad.getPlayerName());
                        if (StringUtils.isNotEmpty(clickCommand)) {
                            player.performCommand(clickCommand);
                        }
                    }));
                }

                for (AdSpot spot : plugin.getAdManager().getSpots()) {
                    var item = gui.getItem(spot.getSlot());
                    if (item != null) continue;

                    List<ComponentWrapper> lore = new ArrayList<>();

                    if (spot.getPreset() == null) {
                        lore.addAll(ChatUtils.formatForGui(config.getStringList("items.y.lore"),
                                Placeholder.unparsed("price", ChatUtils.FORMATTER.format(spot.getPrice())),
                                Placeholder.unparsed("time", ChatUtils.formatTimeBetween(System.currentTimeMillis() + spot.getDuration()))));
                    } else {
                        lore.addAll(ChatUtils.formatForGui(spot.getPreset().getLore()));
                        lore.addAll(ChatUtils.formatForGui(config.getStringList("items.y.lore"),
                                Placeholder.unparsed("price", ChatUtils.FORMATTER.format(spot.getPrice())),
                                Placeholder.unparsed("time", ChatUtils.formatTimeBetween(System.currentTimeMillis() + spot.getDuration()))));
                    }

                    gui.setItem(spot.getSlot(), new UpdatingItem(20, () ->
                            new ItemBuilder(Material.valueOf(config.getString("items.y.material")))
                                    .setCustomModelData(config.getInt("items.y.model"))
                                    .setDisplayName(ChatUtils.formatForGui(config.getString("items.y.name")))
                                    .setLore(lore), event -> {

                        if (!player.hasPermission(PlayerAdsAPI.CREATE_PERMISSION)) {
                            ChatUtils.sendMessage(player, ChatUtils.format(plugin.getPluginMessages().getNotEnoughPermission()));
                            return;
                        }

                        player.closeInventory();

                        if (spot.getPreset() == null) {
                            plugin.getConversationFactory().withFirstPrompt(new AdInputConvo(plugin, spot))
                                    .buildConversation(player).begin();
                            return;
                        }

                        plugin.getAdManager().createAd(player, spot, spot.getPreset().getPresetMessage());
                    }));
                }

                Window window = Window.single()
                        .setViewer(player)
                        .setGui(gui)
                        .setTitle(ChatUtils.formatForGui(config.getString("title")))
                        .build();

                window.open();
            });
        });
    }

}
