package com.thoxia.playerads.config.messages;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Header("################################################################")
@Header("#                                                              #")
@Header("#                           PlayerAds                          #")
@Header("#                                                              #")
@Header("#     Need help configuring the plugin? Join our discord!      #")
@Header("#                  https://discord.thoxia.com                  #")
@Header("#                                                              #")
@Header("################################################################")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
@Getter @Setter
public class MessagesEN extends OkaeriConfig {

    private String prefix = "<#f1c232>PlayerAds <dark_gray>•";

    private String invalidArgument = "<prefix> <red>Invalid argument.";

    private String tooManyArguments = "<prefix> <red>Too many arguments.";

    private String notEnoughArguments = "<prefix> <red>Not enough arguments.";

    private String unknownCommand = "<prefix> <red>Unknown command.";

    private String notEnoughMoney = "<prefix> <red>Insufficient balance.";

    private String mutedMessage = "<prefix> <red>You cannot buy advertisements while you are muted.";

    private boolean mutedTitleEnabled = true;
    private String mutedTitle = "<red><bold>YOU ARE MUTED!";
    private String mutedSubtitle = "<red>Cannot buy ads while you are muted.";

    private String notEnoughPermission = "<prefix> <red>You don't have enough permission to do this.";

    private String invalidInput = "<prefix> <red>Invalid input. Your message must be in between length of 10 to 80! Please re-enter.";

    private String cancelled = "<prefix> <red>Input listener cancelled. You are free to talk now.";

    private String emptyMessage = "<prefix> <red>Please enter a text to advertise!";

    private String emptyPlaceholder = "<red>This place is empty.";

    private String placeholderFormat = "<#f1c232><player>: <#ffffcc><message>";

    private String reloaded = "<prefix> <green>Plugin reloaded. Somethings may not be reloaded, always prefer to restart your server.";

    private String taken = "<prefix> <red>This place was taken!";

    private String bought = "<prefix> <green>Successfully bought an ad!";

    private String inputStarted = "<prefix> <#ffffcc>Please write the message you want it to be displayed. Type 'cancel' to cancel this action.";

    private String expired = "<prefix> <red>Bad news... your advertisement just got expired!";

    private String wait = "<prefix> <red>Someone recently bought an advertisement, please wait for a while. (30 seconds)";

    private String max = "<prefix> <red>You can only have <max> advertisement at the same time.";

    private String removedAllAdmin = "<prefix> <#ffffcc>Successfully removed all advertisements of <player>.";

    private String removedSpotAdmin = "<prefix> <#ffffcc>Successfully removed an advertisement from spot <slot>.";

    private String clearedAllAdmin = "<prefix> <#ffffcc>Successfully cleared all advertisements.";

    private String adNotFoundAdmin = "<prefix> <red>Could not find an advertisement.";

    private String expiredAnnouncement = "<prefix> <#ffffcc>An advertisement was just expired! Go ahead and claim it!";

    private String inputCancelKeyword = "cancel";

    private List<String> adMessage = List.of(
            "",
            "<prefix> <#f1c232><player> <#ffffcc>bought an advertisement for their claim!",
            "<prefix> <#f1c232>Message: <#ffffcc><message>",
            ""
    );

    private String hours = "hours";
    private String days = "days";
    private String seconds = "seconds";
    private String minutes = "minutes";

    private String expiredPlaceholder = "<red>Expired";

}
