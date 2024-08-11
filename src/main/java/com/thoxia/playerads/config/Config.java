package com.thoxia.playerads.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.bossbar.BossBar;

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
public class Config extends OkaeriConfig {

    @Comment("Currently supports: EN, ES, TR")
    private Language language = Language.EN;

    private String boughtSound = "BLOCK_ANVIL_USE";

    private String guiClickSound = "BLOCK_NOTE_BLOCK_PLING";

    private String denySound = "ENTITY_VILLAGER_NO";

    @Comment({"Will be used ONLY for the advertisement messages.", "Does not affect plugin messages.",
            "MINIMESSAGE: Will use minimessage as the component parser.",
            "LEGACY: Will use adventure's legacy component serializer. Char: &",
            "NONE: We will not replace the color codes at all. No colors."
    })
    private ColorType colorTypeForAds = ColorType.LEGACY;

    private boolean notifyOwnerOnExpire = true;

    private boolean announceOnExpire = true;

    @Comment("Set this to \"\" (empty) to disable commands")
    private String clickCommand = "/is warp <player>";

    private int minMessageLength = 10;
    private int maxMessageLength = 80;

    @Comment("How long should we not allow players to advertise? Takes the last advertisement's creation time. In seconds.")
    private int waitBeforeNewAd = 30;

    @Comment("We'll use this value if playerads.limit.# permission is not set for player")
    private int defaultLimit = 1;

    @Comment({"Should we post webhooks to your discord on every advertisement?", "Embed & Webhook configuration can be found in embed.json"})
    private boolean postWebhooks = true;

    @Comment({"Should we open the GUI after purchasing a new ad?"})
    private boolean reopenGuiAfterPurchase = false;

    private BarSettings barSettings = new BarSettings();

    private TitleSettings titleSettings = new TitleSettings();

    @Getter
    @Setter
    public static class BarSettings extends OkaeriConfig {

        private boolean enabled = true;

        @Comment("For some reason that I don't know why, I couldn't make adventure to use any other color from PURPLE. Feel free to edit tho.")
        private BossBar.Color color = BossBar.Color.WHITE;
        private BossBar.Overlay overlay = BossBar.Overlay.NOTCHED_12;
        private String message = "<prefix> <#f1c232>Message: <#ffffcc><message>";

        @Comment("Does not affect advertisement's time at all. Only sets the bossbar's stay time.")
        private int seconds = 30;

    }

    @Getter
    @Setter
    public static class TitleSettings extends OkaeriConfig {

        private boolean enabled = true;
        private String titleMessage = "<#f1c232><bold>ADVERTISEMENT";
        private String subtitleMessage = "<#ffffcc><message>";
        private String actionBarMessage = "<#ffffcc>You want to advertise as well? Type /playerads!";

        private int fadeIn = 200;
        private int stay = 2000;
        private int fadeOut = 200;

    }

    @RequiredArgsConstructor
    @Getter
    public static enum Language {
        EN("messages.yml"),
        TR("messages_tr.yml"),
        ES("messages_es.yml"),
        ;

        private final String fileName;

    }

    public static enum ColorType {
        MINIMESSAGE,
        LEGACY,
        NONE
    }

}
