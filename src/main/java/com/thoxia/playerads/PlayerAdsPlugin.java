package com.thoxia.playerads;

import com.thoxia.playerads.ad.AdManager;
import com.thoxia.playerads.ad.LocalAdManager;
import com.thoxia.playerads.command.AdminCommands;
import com.thoxia.playerads.command.MainCommand;
import com.thoxia.playerads.config.Config;
import com.thoxia.playerads.config.WebhookConfig;
import com.thoxia.playerads.config.YamlConfig;
import com.thoxia.playerads.config.messages.MessagesEN;
import com.thoxia.playerads.economy.EconomyManager;
import com.thoxia.playerads.hook.HookManager;
import com.thoxia.playerads.module.ModuleManager;
import com.thoxia.playerads.papi.PapiHook;
import com.thoxia.playerads.task.AdCheckerTask;
import com.thoxia.playerads.task.CacheUpdaterTask;
import com.thoxia.playerads.util.ChatUtils;
import com.thoxia.playerads.util.UpdateChecker;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ExactMatchConversationCanceller;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Getter
public final class PlayerAdsPlugin extends PlayerAdsAPI {

    @Getter
    private static PlayerAdsPlugin instance;

    // config related stuff
    private MessagesEN pluginMessages;
    private Config pluginConfig;
    private final YamlConfig menuConfig = new YamlConfig(this, "menus", "main-menu.yml");
    private final YamlConfig spotsConfig = new YamlConfig(this, "spots.yml", true);
    private final YamlConfig hooksConfig = new YamlConfig(this, "hooks.yml", true);
    private WebhookConfig webhookConfig;

    // managers
    private final ModuleManager moduleManager = new ModuleManager(this);
    private final HookManager hookManager = new HookManager(this);
    @Setter private AdManager adManager;
    private BukkitCommandManager<CommandSender> commandManager;
    private ConversationFactory conversationFactory;

    // utility
    private BukkitAudiences adventure;
    private UpdateChecker updateChecker;
    private Metrics metrics;

    @Override
    public void onLoad() {
        instance = this;
        PlayerAdsAPI.setInstance(this);

        this.moduleManager.loadModules();
    }

    @Override
    public void onEnable() {
        adventure = BukkitAudiences.create(this);

        createConfig();
        menuConfig.create();
        spotsConfig.create();
        webhookConfig = new WebhookConfig(this, new File(this.getDataFolder(), "embed.json"));
        webhookConfig.create();
        hooksConfig.create();

        hookManager.init();
        hookManager.initEconomyManager();
        hookManager.initMuteManager();

        this.metrics = new Metrics(this, 22773);

        registerCommands();

        this.getModuleManager().enableModules();

        if (adManager == null)
            adManager = new LocalAdManager(this);

        adManager.init();

        conversationFactory = new ConversationFactory(this);
        conversationFactory.withLocalEcho(false);
        conversationFactory.withTimeout(60);
        conversationFactory.withModality(false);
        conversationFactory.withEscapeSequence(pluginMessages.getInputCancelKeyword());
        conversationFactory.addConversationAbandonedListener(listener -> {
            if (listener.getCanceller() instanceof ExactMatchConversationCanceller)
                ChatUtils.sendMessage((CommandSender) listener.getContext().getForWhom(), ChatUtils.format(pluginMessages.getCancelled()));
        });

        new AdCheckerTask(this).runTaskTimerAsynchronously(this, 20, 20);

        (updateChecker = new UpdateChecker(this)).checkUpdates();
        this.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                if (updateChecker.isUpToDate()) return;
                Player player = event.getPlayer();
                if (!player.isOp()) return;

                ChatUtils.sendMessage(player, ChatUtils.format(
                        "<gold>[PlayerAds] <yellow>An update was found!"
                ));
                ChatUtils.sendMessage(player, ChatUtils.format(
                        "<gold>[PlayerAds] <yellow>Update message:"
                ));
                ChatUtils.sendMessage(player, ChatUtils.format(
                        "<gold>[PlayerAds] <yellow><message>",
                        Placeholder.parsed("message", updateChecker.getUpdateMessage())
                ));
            }
        }, this);

        // no need to run cache update task on single instance setups
        if (moduleManager.isModulePresent("MultiServer")) {
            new CacheUpdaterTask(this).runTaskTimerAsynchronously(this, 3600, 3600);
        }

        if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PapiHook(this).register();
        }
    }

    @Override
    public void onDisable() {
        if (adventure != null) {
            adventure.close();
            adventure = null;
        }

        this.moduleManager.disableModules();

        unregisterCommands();
    }

    public void unregisterCommands() {
        List.of("playerads", "reklam", "reklamver", "advertisement", "ads",
                        "advertisements", "paadmin", "reklamadmin", "playeradsadmin", "adsadmin")
                .forEach(this::unregisterCommand);
    }

    public void unregisterCommand(String name) {
        getBukkitCommands(getCommandMap()).remove(name);
    }

    @NotNull
    private CommandMap getCommandMap() {
        try {
            final Server server = Bukkit.getServer();
            final Method getCommandMap = server.getClass().getDeclaredMethod("getCommandMap");
            getCommandMap.setAccessible(true);

            return (CommandMap) getCommandMap.invoke(server);
        } catch (final Exception ignored) {
            throw new CommandRegistrationException("Unable get Command Map. Commands will not be registered!");
        }
    }

    // copied from triumph-cmd, credit goes to triumph-team
    @NotNull
    private Map<String, Command> getBukkitCommands(@NotNull final CommandMap commandMap) {
        try {
            final Field bukkitCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
            bukkitCommands.setAccessible(true);
            //noinspection unchecked
            return (Map<String, org.bukkit.command.Command>) bukkitCommands.get(commandMap);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new CommandRegistrationException("Unable get Bukkit commands. Commands might not be registered correctly!");
        }
    }

    @SneakyThrows
    private void createConfig() {
        pluginConfig = ConfigManager.create(Config.class, (it) -> {
            it.withConfigurer(new YamlBukkitConfigurer());
            it.withBindFile(new File(this.getDataFolder(), "config.yml"));
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });

        Class<MessagesEN> langClass = (Class<MessagesEN>) Class.forName("com.thoxia.playerads.config.messages.Messages" + pluginConfig.getLanguage().name());
        pluginMessages = ConfigManager.create(langClass, (it) -> {
            it.withConfigurer(new YamlBukkitConfigurer());
            it.withBindFile(new File(this.getDataFolder(), pluginConfig.getLanguage().getFileName()));
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });
    }

    private void registerCommands() {
        commandManager = BukkitCommandManager.create(this);

        commandManager.registerSuggestion(SuggestionKey.of("online-players"),
                (sender, context) -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());

        commandManager.registerCommand(new MainCommand(this), new AdminCommands(this));

        commandManager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, invalidArgumentContext) ->
                ChatUtils.sendMessage(sender, ChatUtils.format(pluginMessages.getInvalidArgument())));
        commandManager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, invalidArgumentContext) ->
                ChatUtils.sendMessage(sender, ChatUtils.format(pluginMessages.getUnknownCommand())));
        commandManager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, invalidArgumentContext) ->
                ChatUtils.sendMessage(sender, ChatUtils.format(pluginMessages.getNotEnoughArguments())));
        commandManager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, invalidArgumentContext) ->
                ChatUtils.sendMessage(sender, ChatUtils.format(pluginMessages.getTooManyArguments())));
        commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, invalidArgumentContext) ->
                ChatUtils.sendMessage(sender, ChatUtils.format(pluginMessages.getNotEnoughPermission())));
    }

    public EconomyManager getEconomyManager() {
        return this.hookManager.getEconomyManager();
    }

}
