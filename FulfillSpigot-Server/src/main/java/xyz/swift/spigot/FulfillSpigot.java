package xyz.swift.spigot;

import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.command.SimpleCommandMap;
import xyz.swift.spigot.commands.KnockbackCommand;
import xyz.swift.spigot.commands.FulfillCommand;
import xyz.swift.spigot.commands.PingCommand;
import xyz.swift.spigot.commands.PluginsCommand;
import xyz.swift.spigot.config.FulfillSpigotConfig;
import xyz.swift.spigot.config.KnockbackConfig;

import java.io.File;
import java.util.Arrays;

public class FulfillSpigot {

    public static final String VERSION = "2.0.0";
    public static final Logger LOGGER = LogManager.getLogger(FulfillSpigot.class);
    private static FulfillSpigot instance;
    private FulfillSpigotConfig config;
    private KnockbackConfig knockbackConfig;
    private final File knockbackConfigFile = new File("knockback.yml");

    public FulfillSpigot() {
        instance = this;
        initialize();
        reload();
        reloadKnockbackConfig();
        LOGGER.info("FulfillSpigot version " + VERSION + " initialized.");
    }

    public static FulfillSpigot getInstance() {
        return instance;
    }

    public void reload() {
        initialize();
    }

    public void reloadKnockbackConfig() {
        if (!knockbackConfigFile.exists()) {
            LOGGER.warn("knockback.yml file does not exist. Skipping reload.");
            return;
        }

        try {
            knockbackConfig.init(knockbackConfigFile);
            LOGGER.info("Successfully reloaded knockback.yml");
        } catch (final Exception e) {
            LOGGER.error("Failed to reload knockback.yml", e);
        }
    }

    public KnockbackConfig getKnockbackConfig() {
        return knockbackConfig;
    }

    public void setConfig(final FulfillSpigotConfig config) {
        this.config = config;
    }

    private void initialize() {
        registerCommands();
    }

    private void registerCommands() {
        final SimpleCommandMap commandMap = MinecraftServer.getServer().server.getCommandMap();
        Arrays.asList(
                new FulfillCommand("fulfillspigot"),
                new PingCommand("ping"),
                new KnockbackCommand("knockback"),
                new PluginsCommand("plugins")
        ).forEach(cmd -> commandMap.register(cmd.getLabel(), cmd));
    }
}
