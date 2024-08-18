package net.minecraft.server;

import java.io.IOException;

public class PacketPlayInArmAnimation implements Packet<PacketListenerPlayIn> {
    // FulfillSpigot start
    private static long cachedTimestamp = -1;
    private static long lastCacheUpdateTime = 0;
    private static final long CACHE_UPDATE_INTERVAL = 50;

    public long timestamp;

    public PacketPlayInArmAnimation() {
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastCacheUpdateTime >= CACHE_UPDATE_INTERVAL) {
            cachedTimestamp = currentTime;
            lastCacheUpdateTime = currentTime;
        }

        timestamp = cachedTimestamp;
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        // Empty as the packet does not require serialization in this direction
    }

    @Override
    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this); //forward
    }
    // FulfillSpigot end
}
