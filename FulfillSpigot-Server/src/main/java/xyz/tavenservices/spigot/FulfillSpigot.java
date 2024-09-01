package xyz.tavenservices.spigot;

import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.tavenservices.spigot.commands.KnockbackCommand;
import xyz.tavenservices.spigot.commands.LoadedChunksCommand;
import xyz.tavenservices.spigot.commands.PingCommand;
import xyz.tavenservices.spigot.commands.ReloadCommand;
import xyz.tavenservices.spigot.config.FulfillSpigotConfig;
import xyz.tavenservices.spigot.config.KnockbackConfig;

import java.io.File;

public class FulfillSpigot {

    public static final Logger LOGGER = LogManager.getLogger(FulfillSpigot.class);
    private static FulfillSpigot INSTANCE;
    private FulfillSpigotConfig config;
    private KnockbackConfig knockbackConfig;
    private File knockbackConfigFile;

    public FulfillSpigot() {
        INSTANCE = this;
        this.init();
    }

    public void reload() {
        this.init();
    }

    public void reloadKnockbackConfig() {
        // it always exists just another check for it to work because reloading in spigots is kinda different.
        if (knockbackConfigFile == null) {
            knockbackConfigFile = new File("knockback.yml");
        }

        if (!knockbackConfigFile.exists()) {
            LOGGER.warn("knockback.yml file does not exist. Skipping reload.");
            return;
        }

        try {
            YamlConfiguration.loadConfiguration(knockbackConfigFile);
            knockbackConfig.init(knockbackConfigFile);
            LOGGER.info("Successfully reloaded knockback.yml");
        } catch (Exception e) {
            LOGGER.error("Failed to reload knockback.yml file.", e);
        }
    }

    private void initCmds() {
        SimpleCommandMap commandMap = MinecraftServer.getServer().server.getCommandMap();

        PingCommand pingCommand = new PingCommand("ping");
        commandMap.register(pingCommand.getName(), "", pingCommand);


        KnockbackCommand knockbackCommand = new KnockbackCommand();
        commandMap.register(knockbackCommand.getName(), "", knockbackCommand);

        LoadedChunksCommand loadedChunksCommand = new LoadedChunksCommand("loadedchunks");
        commandMap.register(loadedChunksCommand.getName(), "", loadedChunksCommand);

        ReloadCommand reloadCommand = new ReloadCommand("kbreload");
        commandMap.register(reloadCommand.getName(), "", reloadCommand);

    }

    private void init() {
        initCmds();
    }

    public static FulfillSpigot getInstance() {
        return INSTANCE;
    }


    public KnockbackConfig getKnockbackConfig() {
        return this.knockbackConfig;
    }

    public void setConfig(FulfillSpigotConfig config) {
        this.config = config;
    }
}
