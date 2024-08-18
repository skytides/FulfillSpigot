package xyz.zenithdev.spigot.database;

import com.google.common.base.Throwables;
import com.hpfxd.configurate.eoyaml.EOYamlConfigurationLoader;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.util.MapFactories;

import java.io.File;

@ConfigSerializable
@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
public class DatabaseConfig {

    private static DatabaseConfig config;


    public static void init(File file) {
        EOYamlConfigurationLoader loader = EOYamlConfigurationLoader.builder()
            .file(file)
            .defaultOptions(o -> o
                .header("This is the configuration file for database settings.\n" +
                    "Use caution when modifying settings, as some may impact database connectivity.")
                .mapFactory(MapFactories.insertionOrdered())
            )
            .build();

        try {
            CommentedConfigurationNode root = loader.load();
            config = root.get(DatabaseConfig.class);

            //set def values
            config.enabled = config.enabled != null ? config.enabled : false;
            config.jdbcUrl = config.jdbcUrl != null ? config.jdbcUrl : "jdbc:mysql://localhost:3306/minecraft";
            config.username = config.username != null ? config.username : "root";
            config.password = config.password != null ? config.password : "";
            config.maximumPoolSize = config.maximumPoolSize != null ? config.maximumPoolSize : 10;
            config.minimumIdle = config.minimumIdle != null ? config.minimumIdle : 2;
            config.connectionTimeout = config.connectionTimeout != null ? config.connectionTimeout : 30000;
            config.idleTimeout = config.idleTimeout != null ? config.idleTimeout : 600000;
            config.maxLifetime = config.maxLifetime != null ? config.maxLifetime : 1800000;
            config.autoCommit = config.autoCommit != null ? config.autoCommit : true;
            config.leakDetectionThreshold = config.leakDetectionThreshold != null ? config.leakDetectionThreshold : 0;


            if (config.enabled && (config.jdbcUrl == null || config.jdbcUrl.isEmpty())) {
                throw new IllegalArgumentException("jdbcUrl is required when database is enabled.");
            }

            //save the configuration if it was modified
            root.set(config);
            loader.save(root);

        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    @Comment("Enable or disable the database connection.")
    public Boolean enabled;

    @Comment("The JDBC URL for the database connection.")
    public String jdbcUrl;

    @Comment("The username for the database connection.")
    public String username;

    @Comment("The password for the database connection.")
    public String password;

    @Comment("The maximum number of connections in the pool.")
    public Integer maximumPoolSize;

    @Comment("The minimum number of idle connections in the pool.")
    public Integer minimumIdle;

    @Comment("The maximum amount of time (in milliseconds) that a connection is allowed to sit idle in the pool.")
    public Integer idleTimeout;

    @Comment("The maximum amount of time (in milliseconds) that a connection is allowed to live.")
    public Integer maxLifetime;

    @Comment("The maximum amount of time (in milliseconds) that a client will wait for a connection from the pool.")
    public Integer connectionTimeout;

    @Comment("Whether to enable auto-commit mode.")
    public Boolean autoCommit;

    @Comment("The threshold (in milliseconds) for logging connection leaks.")
    public Integer leakDetectionThreshold;

    public static DatabaseConfig get() {
        return config;
    }
}
