package com.thoxia.playerads.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class YamlConfig extends YamlConfiguration {

    private final JavaPlugin plugin;
    private final File file;
    private final String folder;
    private final boolean save;

    public YamlConfig(JavaPlugin plugin, String name, boolean save) {
        this.plugin = plugin;
        file = new File(plugin.getDataFolder(), name.endsWith(".yml") ? name : name + ".yml");
        this.folder = null;
        this.save = save;
    }

    public YamlConfig(JavaPlugin plugin, String folder, String name) {
        this.plugin = plugin;
        file = new File(new File(plugin.getDataFolder(), folder), name.endsWith(".yml") ? name : name + ".yml");
        this.folder = folder;
        this.save = true;
    }

    @SuppressWarnings("all")
    public void create() {
        if (!file.exists()) {
            file.getParentFile().mkdirs();

            if (!save) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                plugin.saveResource(folder == null ? file.getName() : folder + File.separator + file.getName(), false);
            }
        }

        reload();
    }

    public void reload() {
        try {
            this.load(this.file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            this.save(this.file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getFile() {
        return file;
    }

}
