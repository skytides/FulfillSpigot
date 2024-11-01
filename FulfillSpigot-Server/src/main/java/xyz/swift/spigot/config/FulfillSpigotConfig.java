package xyz.swift.spigot.config;

import com.google.common.base.Throwables;
import com.hpfxd.configurate.eoyaml.EOYamlConfigurationLoader;
import net.minecraft.server.World;
import org.bukkit.Bukkit;
import org.spigotmc.SpigotConfig;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.util.MapFactories;
import org.spongepowered.configurate.util.NamingSchemes;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
public class FulfillSpigotConfig {
    private static FulfillSpigotConfig config;
    private static FulfillSpigotWorldConfig defaultWorldConfig;
    private static final Map<String, FulfillSpigotWorldConfig> worldConfigs = new HashMap<>();

    /**
     * Initialize the configuration, and load it from a file.
     * <p>
     * This is called once on server startup, and every reload.
     *
     * @param file The configuration file.
     */
    public static void init(File file) {
        EOYamlConfigurationLoader loader = EOYamlConfigurationLoader.builder()
            .file(file)
            .defaultOptions(o -> o
                .header("This is the configuration file for FulfillSpigot.\n" +
                    "Use caution when modifying settings, as some may impact gameplay in non-obvious ways.")
                .mapFactory(MapFactories.insertionOrdered())
                .serializers(build -> build.register(PacketLimiterConfig.class, new PacketLimiterConfig.Serializer())
                    .registerAnnotatedObjects(ObjectMapper.factoryBuilder()
                        .defaultNamingScheme(NamingSchemes.CAMEL_CASE)
                        .build())))
            .build();

        try {
            CommentedConfigurationNode root = loader.load();
            config = root.get(FulfillSpigotConfig.class);

            // worlds
            CommentedConfigurationNode worldsNode = root.node("worlds")
                .comment("The worlds section is for settings which can be configured per-world.\n" +
                    "\n" +
                    "Any settings in the \"default\" world will provide default values for\n" +
                    "other worlds which don't explicitly specify settings.\n" +
                    "\n" +
                    "To specify settings for a specific world, just add a new section with the world's name.");
            ConfigurationNode defaultWorldNode = worldsNode.node("default");
            defaultWorldConfig = defaultWorldNode.get(FulfillSpigotWorldConfig.class);
            defaultWorldNode.set(defaultWorldConfig); // populate default in config

            root.set(config); // update backing node
            loader.save(root);

            // call after save
            initWorlds(worldsNode);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    private static void initWorlds(ConfigurationNode node) throws Exception {
        worldConfigs.clear();
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : node.childrenMap().entrySet()) {
            Object worldName = entry.getKey();
            if (worldName.equals("default")) continue; // skip "default"
            ConfigurationNode worldNode = entry.getValue();

            FulfillSpigotWorldConfig worldConfig = worldNode.get(FulfillSpigotWorldConfig.class);

            worldConfigs.put(worldName.toString(), worldConfig);
        }
    }

    public static FulfillSpigotWorldConfig getWorldConfig(String worldName) {
        return worldConfigs.getOrDefault(worldName, defaultWorldConfig);
    }

    public static FulfillSpigotWorldConfig getWorldConfig(World world) {
        return getWorldConfig(world.worldData.getName());
    }

    public static FulfillSpigotConfig get() {
        return config;
    }

    //------------------------------------------------------------------------
    @Comment("On servers with plugins that constantly churn through scoreboards, there is a risk of\n" +
        "degraded GC performance due to the number of scoreboards held on by weak references.\n" +
        "Most plugins don't even need the (vanilla) functionality that requires all plugin\n" +
        "scoreboards to be tracked by the server. Instead, only track scoreboards when an\n" +
        "objective is added with a non-dummy criteria.")
    public boolean trackPluginScoreboards = false;

    @Comment("Instructs the server how to handle player UUIDs and data when behind a proxy. Set to\n" +
        "match your proxy's online-mode setting.")
    public boolean proxyOnlineMode = true;

    public boolean isProxyOnlineMode() {
        return Bukkit.getOnlineMode() || (SpigotConfig.bungee && this.proxyOnlineMode);
    }

    public int maxBookPageSize = 2560;

    public double maxBookTotalSizeMultiplier = 0.98d;

    @Comment("Limits incoming packets from players. See Paper's documentation for more info:\n" +
        "https://docs.papermc.io/paper/reference/paper-global-configuration#packet-limiter\n" +
        "(note that \"max-packet-rate\" is renamed to \"maxPacketRate\")")
    public PacketLimiterConfig packetLimiter = PacketLimiterConfig.createDefault();


    public boolean resolveSelectorsInBooks = true;

    @Comment("Whether player IP addresses should be logged by the server. This does not impact\n" +
        "the ability of plugins to log the IP addresses of players.")
    public boolean logPlayerIpAddresses = true;

    // FulfillSpigot - Add smooth pots
    @Comment("Set this to true for smoother pots")
    public boolean smoothPotting = true;

    @Comment("The default minecraft hit-ticks are 20 ticks, However changing it to 0 will result in a no hit delay, 19 is a great value for great hit detection")
    public int hitDelay = 20;

    @Comment("Should it fix the eat while running bug")
    public boolean fixEatWhileRunning = true;

    @Comment("Improves overall Hit registration")
    public boolean improveHitReg = true;

    public double potionSpeed = 0.0;

    @Comment("Whether or not it should kick players for tab completion packets spam")
    public boolean kickTabCompleteSpam = false;

    @Comment("The max cache limit for the RegionFileCache (Please use choose a value according to \n" +
        "the amount of ram your server has\n" +
        "1 GB RAM: Cache Size: 100-200\n" +
        "2 GB RAM: Cache Size: 200-400\n" +
        "4 GB RAM: Cache Size: 400-800\n" +
        "6 GB RAM: Cache Size: 500-1000\n" +
        "8 GB RAM: Cache Size: 700-1400 entries\n" +
        "16 GB RAM: Cache Size: 1400-2800 entries\n" +
        "32 GB RAM: Cache Size: 2800-5600 entries")
    public int maxCacheSize = 500;

    public int pluginsPerPage = 10;


    public static boolean stopDecodingItemStackOnPlace = true;

}

