package net.minecraft.server;

import java.io.IOException;

public interface Packet<T extends PacketListener> {

    void a(PacketDataSerializer packetdataserializer) throws IOException;

    void b(PacketDataSerializer packetdataserializer) throws IOException;

    void a(T t0);
    // PandaSpigot start
    /**
     * @param player {@code null} if not at {@link EnumProtocol#PLAY} yet.
     */
    default void onPacketDispatch(EntityPlayer player) {}

    /**
     * @param player {@code null} if not at {@link EnumProtocol#PLAY} yet.
     * @param future Can be {@code null} if packet was cancelled.
     */
    default void onPacketDispatchFinish(EntityPlayer player, io.netty.channel.ChannelFuture future) {}

    /**
     * @return Whether {@link #onPacketDispatchFinish(EntityPlayer, io.netty.channel.ChannelFuture)} should
     * be called after this packet has been dispatched.
     */
    default boolean hasFinishListener() { return false; }

    /**
     * Checks whether this packet is ready to be sent.
     * <p>
     * If this returns {@code false}, the packet will be added to the queue, and checked every tick until it is ready.
     *
     * @return Whether this packet is ready.
     */
    default boolean isReady() { return true; }

    /**
     * @return A list of extra packets to be sent after this packet.
     */
    default java.util.List<Packet<?>> getExtraPackets() { return null; }
    // PandaSpigot end
}
