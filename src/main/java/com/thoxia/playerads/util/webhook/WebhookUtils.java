package com.thoxia.playerads.util.webhook;

import com.thoxia.playerads.PlayerAdsPlugin;
import com.thoxia.playerads.ad.Ad;
import lombok.SneakyThrows;

public class WebhookUtils {

    @SneakyThrows
    public static void send(Ad ad, PlayerAdsPlugin plugin) {
        if (!plugin.getPluginConfig().isPostWebhooks()) return;

        DiscordWebhook webhook = plugin.getWebhookConfig().prepareWebhook(ad);
        if (webhook.getUrl().equals("YOUR_WEBHOOK_URL")) {
            plugin.getLogger().severe("post-webhooks is set to true in config.yml but you did not configure webhook's url. Please update it.");
            return;
        }

        webhook.execute();
    }

}
