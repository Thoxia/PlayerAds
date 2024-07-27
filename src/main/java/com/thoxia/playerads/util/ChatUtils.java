package com.thoxia.playerads.util;

import com.thoxia.playerads.PlayerAdsPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ChatUtils {

    private final static MiniMessage MINI_MESSAGE = MiniMessage.builder()
            .tags(TagResolver.standard())
            .preProcessor(s -> s.replace("<prefix>", PlayerAdsPlugin.getInstance().getPluginMessages().getPrefix()))
            .postProcessor(component -> component.decoration(TextDecoration.ITALIC, false))
            .build();
    public static final DecimalFormat FORMATTER = (DecimalFormat) NumberFormat.getNumberInstance();
    public static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder().character('&')
            .hexColors().useUnusualXRepeatedCharacterHexFormat().build();

    public static final LegacyComponentSerializer LEGACY_AMPERSAND = LegacyComponentSerializer.builder()
            .hexColors().useUnusualXRepeatedCharacterHexFormat().build();

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    static {
        FORMATTER.setMinimumIntegerDigits(1);
        FORMATTER.setMaximumIntegerDigits(20);
        FORMATTER.setMaximumFractionDigits(2);
        FORMATTER.setGroupingSize(3);
    }

    public static Component formatAdMessage(String text, PlayerAdsPlugin plugin) {
        return switch (plugin.getPluginConfig().getColorTypeForAds()) {
            case MINIMESSAGE -> ChatUtils.format(text);
            case LEGACY -> ChatUtils.colorLegacyString(text);
            case NONE -> Component.text(text);
        };
    }

    public static String fromLegacy(Component component) {
        return LEGACY_AMPERSAND.serialize(component);
    }

    public static Component colorLegacyString(String string) {
        return LEGACY.deserialize(string);
    }

    public static String toLegacy(String string, TagResolver... placeholders) {
        Component component = ChatUtils.format(string, placeholders);
        return LEGACY.serialize(component);
    }

    public static List<String> toLegacy(List<String> list, TagResolver... placeholders) {
        return list.stream().map(s -> toLegacy(s, placeholders)).collect(Collectors.toList());
    }

    public static ComponentWrapper formatForGui(String string, TagResolver... placeholders) {
        return new AdventureComponentWrapper(format(string, placeholders));
    }

    public static List<ComponentWrapper> formatForGui(List<String> list, TagResolver... placeholders) {
        return list.stream().map(s -> formatForGui(s, placeholders)).collect(Collectors.toList());
    }

    public static Component format(String string, TagResolver... placeholders) {
        return MINI_MESSAGE.deserialize(string, placeholders);
    }

    public static List<Component> format(List<String> list, TagResolver... placeholders) {
        return list.stream().map(s -> MINI_MESSAGE.deserialize(s, placeholders)).collect(Collectors.toList());
    }

    public static void sendMessage(CommandSender player, Component component) {
        PlayerAdsPlugin.getInstance().getAdventure().sender(player).sendMessage(component);
    }

    public static void sendMessage(CommandSender player, List<Component> components) {
        components.forEach(s -> ChatUtils.sendMessage(player, s));
    }

    public static String formatTimeBetween(long to) {
        long now = System.currentTimeMillis();
        long s = to - now;

        if (s <= 0) {
            return PlayerAdsPlugin.getInstance().getPluginMessages().getExpiredPlaceholder();
        }

        long seconds = TimeUnit.MILLISECONDS.toSeconds(s) % 60;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(s) % 60;
        long hours = TimeUnit.MILLISECONDS.toHours(s) % 24;
        long days = TimeUnit.MILLISECONDS.toDays(s);

        String str = "";
        if (days > 0) {
            str += days + " " + PlayerAdsPlugin.getInstance().getPluginMessages().getDays() + " ";
        }

        if (hours > 0) {
            str += hours + " " + PlayerAdsPlugin.getInstance().getPluginMessages().getHours() + " ";
        }

        if (minutes > 0) {
            str += minutes + " " + PlayerAdsPlugin.getInstance().getPluginMessages().getMinutes() + " ";
        }

        if (seconds > 0) {
            str += seconds + " " + PlayerAdsPlugin.getInstance().getPluginMessages().getSeconds();
        }

        return str;
    }

}
