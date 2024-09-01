package xyz.tavenservices.spigot.config;

import net.minecraft.server.Packet;
import org.bukkit.Bukkit;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class PacketLimiterConfig {
    private final String kickMessage;
    private final PacketLimit allPacketsLimit;
    private final Map<Class<? extends Packet<?>>, PacketLimit> packetSpecificLimits;

    public PacketLimiterConfig(String kickMessage, PacketLimit allPacketsLimit, Map<Class<? extends Packet<?>>, PacketLimit> packetSpecificLimits) {
        this.kickMessage = kickMessage;
        this.allPacketsLimit = allPacketsLimit;
        this.packetSpecificLimits = packetSpecificLimits;
    }

    public static final class PacketLimit {
        public final double packetLimitInterval;
        public final double maxPacketRate;
        public final ViolateAction violateAction;

        public PacketLimit(final double packetLimitInterval, final double maxPacketRate, final ViolateAction violateAction) {
            this.packetLimitInterval = packetLimitInterval;
            this.maxPacketRate = maxPacketRate;
            this.violateAction = violateAction;
        }

        public enum ViolateAction {
            KICK, DROP;
        }
    }

    static PacketLimiterConfig createDefault() {
        return new PacketLimiterConfig("&cSent too many packets.", new PacketLimit(7, 500, PacketLimit.ViolateAction.KICK), new HashMap<>());
    }

    public String getKickMessage() {
        return this.kickMessage;
    }

    public PacketLimit getAllPacketsLimit() {
        return this.allPacketsLimit;
    }

    public Map<Class<? extends Packet<?>>, PacketLimit> getPacketSpecificLimits() {
        return this.packetSpecificLimits;
    }

    public static class Serializer implements TypeSerializer<PacketLimiterConfig> {
        @Override
        public PacketLimiterConfig deserialize(Type type, ConfigurationNode node) throws SerializationException {
            String kickMessage = node.node("kickMessage").getString("&cSent too many packets.");
            ConfigurationNode limits = node.node("limits");

            PacketLimit allPacketsLimit = null;
            Map<Class<? extends Packet<?>>, PacketLimit> packetSpecificLimits = new HashMap<>();

            for (Map.Entry<Object, ? extends ConfigurationNode> entry : limits.childrenMap().entrySet()) {
                String key = String.valueOf(entry.getKey());
                ConfigurationNode limitNode = entry.getValue();
                if (key.equals("all")) {
                    PacketLimit packetLimit = new PacketLimit(
                        limitNode.node("interval").getDouble(),
                        limitNode.node("maxPacketRate").getDouble(),
                        PacketLimit.ViolateAction.KICK
                    );

                    if (packetLimit.packetLimitInterval > 0.0 && packetLimit.maxPacketRate > 0.0) {
                        allPacketsLimit = packetLimit;
                    }
                } else {
                    Class<?> clazz;
                    try {
                        clazz = Class.forName("net.minecraft.server." + key);
                    } catch (ClassNotFoundException e) {
                        Bukkit.getLogger().warning("Packet '" + key + "' does not exist, cannot limit it! Please update fulfillspigot.yml");
                        continue;
                    }

                    if (!Packet.class.isAssignableFrom(clazz)) {
                        Bukkit.getLogger().warning("Class '" + key + "' is not a packet, cannot limit it! Please update fulfillspigot.yml");
                        continue;
                    }

                    PacketLimit packetLimit = new PacketLimit(
                        limitNode.node("interval").getDouble(),
                        limitNode.node("maxPacketRate").getDouble(),
                        limitNode.node("action").get(PacketLimit.ViolateAction.class)
                    );

                    if (packetLimit.packetLimitInterval > 0.0 && packetLimit.maxPacketRate > 0.0) {
                        //noinspection unchecked
                        packetSpecificLimits.put((Class<? extends Packet<?>>) clazz, packetLimit);
                    }
                }
            }

            return new PacketLimiterConfig(kickMessage, allPacketsLimit, packetSpecificLimits);
        }

        @Override
        public void serialize(Type type, PacketLimiterConfig config, ConfigurationNode target) throws SerializationException {
            if (config == null) {
                target.raw(null);
                return;
            }

            target.node("kickMessage").set(config.kickMessage);
            ConfigurationNode limitsNode = target.node("limits");
            if (config.allPacketsLimit != null) {
                limitsNode.node("all", "interval").set(config.allPacketsLimit.packetLimitInterval);
                limitsNode.node("all", "maxPacketRate").set(config.allPacketsLimit.maxPacketRate);
            }

            for (Map.Entry<Class<? extends Packet<?>>, PacketLimit> entry : config.packetSpecificLimits.entrySet()) {
                ConfigurationNode node = limitsNode.node(entry.getKey().getSimpleName());
                PacketLimit limit = entry.getValue();

                node.node("interval", limit.packetLimitInterval);
                node.node("maxPacketRate", limit.maxPacketRate);
                node.node("action").set(limit.violateAction);
            }
        }
    }
}
