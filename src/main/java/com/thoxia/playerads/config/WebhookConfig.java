package com.thoxia.playerads.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.thoxia.playerads.PlayerAdsPlugin;
import com.thoxia.playerads.ad.Ad;
import com.thoxia.playerads.gson.ColorDeserializer;
import com.thoxia.playerads.gson.ColorSerializer;
import com.thoxia.playerads.util.webhook.DiscordWebhook;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.awt.*;
import java.io.*;

@RequiredArgsConstructor
public class WebhookConfig {

    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeHierarchyAdapter(Color.class, new ColorDeserializer())
            .registerTypeHierarchyAdapter(Color.class, new ColorSerializer())
            .create();

    private final PlayerAdsPlugin plugin;
    private final File file;

    private String json;

    @SneakyThrows
    public void create() {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(this.file.getName(), false);
        }

        load();
    }

    @SneakyThrows
    public void load() {
        try (Reader reader = new FileReader(file)) {
            json = GSON.toJson(GSON.fromJson(reader, JsonObject.class));
        }
    }

    @SneakyThrows
    public void save() {
        try (Writer writer = new FileWriter(file, false)) {
            GSON.toJson(json, writer);
            writer.flush();
        }
    }

    public DiscordWebhook prepareWebhook(Ad ad) {
        String updatedJson = json
                .replace("[message]", ad.getMessage())
                .replace("[player]", ad.getPlayerName());

        return GSON.fromJson(updatedJson, DiscordWebhook.class);
    }

}
