package net.minecraft.server;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.AbstractEventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.crypto.SecretKey;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import xyz.tavenservices.spigot.FulfillSpigot;
import xyz.tavenservices.spigot.config.FulfillSpigotConfig;
import xyz.tavenservices.spigot.config.PacketLimiterConfig;
import xyz.tavenservices.spigot.util.IntervalledCounter;

public class NetworkManager extends SimpleChannelInboundHandler<Packet> {

    private static final Logger g = LogManager.getLogger();
    public static final Marker a = MarkerManager.getMarker("NETWORK");
    public static final Marker b = MarkerManager.getMarker("NETWORK_PACKETS", NetworkManager.a);
    public static final AttributeKey<EnumProtocol> ATTRIBUTE_PROTOCOL = AttributeKey.valueOf("protocol");
    private final ReentrantReadWriteLock j = new ReentrantReadWriteLock();

    public static final AttributeKey<EnumProtocol> c = AttributeKey.valueOf("protocol");
    public static final LazyInitVar<NioEventLoopGroup> d = new LazyInitVar() {
        protected NioEventLoopGroup a() {
            return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Client IO #%d").setDaemon(true).build());
        }

        protected Object init() {
            return this.a();
        }
    };
    public static final LazyInitVar<EpollEventLoopGroup> e = new LazyInitVar() {
        protected EpollEventLoopGroup a() {
            return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
        }

        protected Object init() {
            return this.a();
        }
    };
    public static final LazyInitVar<LocalEventLoopGroup> f = new LazyInitVar() {
        protected LocalEventLoopGroup a() {
            return new LocalEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Local Client IO #%d").setDaemon(true).build());
        }

        protected Object init() {
            return this.a();
        }
    };
    private final EnumProtocolDirection h;
    private final Queue<NetworkManager.QueuedPacket> i = Queues.newConcurrentLinkedQueue();
    //private final ReentrantReadWriteLock j = new ReentrantReadWriteLock(); // PandaSpigot - Remove packet queue locking
    public Channel channel;
    // Spigot Start // PAIL
    public SocketAddress l;
    public java.util.UUID spoofedUUID;
    public com.mojang.authlib.properties.Property[] spoofedProfile;
    public boolean preparing = true;
    // Spigot End
    private PacketListener m;
    private IChatBaseComponent n;
    private boolean o;
    private boolean p;
    private static boolean enableExplicitFlush = Boolean.getBoolean("paper.explicit-flush"); // PandaSpigot - Disable explicit flushing
    // PandaSpigot start - Optimize network
    public boolean isPending = true;
    public boolean queueImmunity = false;
    public EnumProtocol protocol;
    // PandaSpigot end

    // FulfillSpigot start - allow controlled flushing
    volatile boolean canFlush = true;
    private final java.util.concurrent.atomic.AtomicInteger packetWrites = new java.util.concurrent.atomic.AtomicInteger();
    private int flushPacketsStart;
    private final Object flushLock = new Object();

    public void disableAutomaticFlush() {
        synchronized (this.flushLock) {
            this.flushPacketsStart = this.packetWrites.get(); // must be volatile and before canFlush = false
            this.canFlush = false;
        }
    }

    public void enableAutomaticFlush() {
        synchronized (this.flushLock) {
            this.canFlush = true;
            if (this.packetWrites.get() != this.flushPacketsStart) { // must be after canFlush = true
                this.flush(); // only make the flush call if we need to
            }
        }
    }

    private void flush() {
        if (this.channel.eventLoop().inEventLoop()) {
            this.channel.flush();
        } else {
            this.channel.eventLoop().execute(() -> {
                this.channel.flush();
            });
        }
    }
    // FulfillSpigot end

    // FulfillSpigot start - faster packets
    private final Queue<NetworkManager.QueuedPacket> fastPackets = Queues.newConcurrentLinkedQueue();
    public static final Set<Class<? extends Packet<?>>> FAST_ELIGIBLE = new LinkedHashSet<>();

    static {
        FAST_ELIGIBLE.add(PacketPlayOutPosition.class);
        FAST_ELIGIBLE.add(PacketPlayOutEntityVelocity.class);
        FAST_ELIGIBLE.add(PacketPlayOutEntityTeleport.class);
        FAST_ELIGIBLE.add(PacketPlayOutEntity.PacketPlayOutEntityLook.class);
        FAST_ELIGIBLE.add(PacketPlayOutEntity.PacketPlayOutRelEntityMove.class);
        FAST_ELIGIBLE.add(PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook.class);
        FAST_ELIGIBLE.add(PacketPlayOutAnimation.class);
    }
    // FulfillSpigot end
    // FulfillSpigot start - packet limiter
    protected final Object PACKET_LIMIT_LOCK = new Object();
    protected final IntervalledCounter allPacketCounts = FulfillSpigotConfig.get().packetLimiter.getAllPacketsLimit() != null ? new IntervalledCounter(
        (long) (FulfillSpigotConfig.get().packetLimiter.getAllPacketsLimit().packetLimitInterval * 1.0e9)
    ) : null;
    protected final java.util.Map<Class<? extends Packet<?>>, IntervalledCounter> packetSpecificLimits = new java.util.HashMap<>();
    private boolean stopReadingPackets;
    private void killForPacketSpam() {
        IChatBaseComponent[] reason = org.bukkit.craftbukkit.util.CraftChatMessage.fromString(org.bukkit.ChatColor.translateAlternateColorCodes('&', FulfillSpigotConfig.get().packetLimiter.getKickMessage()));
        this.a(new PacketPlayOutKickDisconnect(reason[0]), future -> {
            this.close(reason[0]);
            this.k();
            this.stopReadingPackets = true;
        }, null);
    }
    // PandaSpigot end - packet limiter
    public NetworkManager(EnumProtocolDirection enumprotocoldirection) {
        this.h = enumprotocoldirection;
    }

    public void channelActive(ChannelHandlerContext channelhandlercontext) throws Exception {
        super.channelActive(channelhandlercontext);
        this.channel = channelhandlercontext.channel();
        this.l = this.channel.remoteAddress();
        // Spigot Start
        this.preparing = false;
        // Spigot End

        try {
            this.a(EnumProtocol.HANDSHAKING);
        } catch (Throwable throwable) {
            NetworkManager.g.fatal(throwable);
        }

    }

    public void setProtocol(EnumProtocol protocol) {
        a(protocol);
    }

    public void a(EnumProtocol enumprotocol) {
        this.protocol = enumprotocol; // PandaSpigot
        this.channel.attr(NetworkManager.c).set(enumprotocol);
        this.channel.config().setAutoRead(true);
        NetworkManager.g.debug("Enabled auto read");
    }

    public void tick() {
        this.sendPacketQueue();
        if (this.m instanceof IUpdatePlayerListBox) {
            ((IUpdatePlayerListBox) this.m).c();
        }

        this.channel.flush();
    }

    public void channelInactive(ChannelHandlerContext channelhandlercontext) throws Exception {
        this.close(new ChatMessage("disconnect.endOfStream", new Object[0]));
    }

    public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) throws Exception {
        ChatMessage chatmessage;

        if (throwable instanceof TimeoutException) {
            chatmessage = new ChatMessage("disconnect.timeout", new Object[0]);
        } else {
            chatmessage = new ChatMessage("disconnect.genericReason", new Object[] { "Internal Exception: " + throwable});
        }

        this.close(chatmessage);
        if (MinecraftServer.getServer().isDebugging()) throwable.printStackTrace(); // Spigot
    }

    protected void a(ChannelHandlerContext channelhandlercontext, Packet packet) throws Exception {
        if (this.channel.isOpen()) {
            // PandaSpigot start - packet limiter
            if (this.stopReadingPackets) {
                return;
            }
            if (this.allPacketCounts != null ||
                FulfillSpigotConfig.get().packetLimiter.getPacketSpecificLimits().containsKey(packet.getClass())) {
                long time = System.nanoTime();
                synchronized (PACKET_LIMIT_LOCK) {
                    if (this.allPacketCounts != null) {
                        this.allPacketCounts.updateAndAdd(1, time);
                        if (this.allPacketCounts.getRate() >= FulfillSpigotConfig.get().packetLimiter.getAllPacketsLimit().maxPacketRate) {
                            this.killForPacketSpam();
                            return;
                        }
                    }

                    for (Class<?> check = packet.getClass(); check != Object.class; check = check.getSuperclass()) {
                        PacketLimiterConfig.PacketLimit packetSpecificLimit = FulfillSpigotConfig.get().packetLimiter.getPacketSpecificLimits().get(check);
                        if (packetSpecificLimit == null) {
                            continue;
                        }
                       IntervalledCounter counter = this.packetSpecificLimits.computeIfAbsent((Class) check, clazz -> {
                            return new IntervalledCounter((long) (packetSpecificLimit.packetLimitInterval * 1.0e9));
                        });
                        counter.updateAndAdd(1, time);
                        if (counter.getRate() >= packetSpecificLimit.maxPacketRate) {
                            switch (packetSpecificLimit.violateAction) {
                                case DROP:
                                    return;
                                case KICK:
                                    this.killForPacketSpam();
                                    return;
                            }
                        }
                    }
                }
            }
            // PandaSpigot end - packet limiter
            try {
                packet.a(this.m);
            } catch (CancelledPacketHandleException cancelledpackethandleexception) {
                ;
            }
        }

    }

    public void a(PacketListener packetlistener) {
        Validate.notNull(packetlistener, "packetListener", new Object[0]);
        NetworkManager.g.debug("Set listener of {} to {}", new Object[] { this, packetlistener});
        this.m = packetlistener;
    }

    // PandaSpigot start
    public EntityPlayer getPlayer() {
        if (this.m instanceof PlayerConnection) {
            return ((PlayerConnection) this.m).player;
        } else {
            return null;
        }
    }
    private static class InnerUtil { // Attempt to hide these methods from ProtocolLib so it doesn't accidently pick them up.
        private static java.util.List<Packet> buildExtraPackets(Packet packet) {
            java.util.List<Packet> extra = packet.getExtraPackets();
            if (extra == null || extra.isEmpty()) {
               return null;
            }
            java.util.List<Packet> ret = new java.util.ArrayList<>(1 + extra.size());
            buildExtraPackets0(extra, ret);
            return ret;
        }

        private static void buildExtraPackets0(java.util.List<Packet> extraPackets, java.util.List<Packet> into) {
            for (Packet extra : extraPackets) {
                into.add(extra);
                java.util.List<Packet> extraExtra = extra.getExtraPackets();
                if (extraExtra != null && !extraExtra.isEmpty()) {
                    buildExtraPackets0(extraExtra, into);
                }
            }
        }
        private static boolean canSendImmediate(NetworkManager networkManager, Packet<?> packet) {
            return networkManager.isPending || networkManager.protocol != EnumProtocol.PLAY ||
                    packet instanceof PacketPlayOutKeepAlive ||
                    packet instanceof PacketPlayOutChat ||
                    packet instanceof PacketPlayOutTabComplete ||
                    packet instanceof PacketPlayOutTitle;
        }
    }
    // PandaSpigot end

    public void handle(Packet<?> packet) {
        this.a(packet, null, (GenericFutureListener<? extends Future<? super Void>>) null); // PandaSpigot
    }
    public void a(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> genericfuturelistener, GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener) {
        // PandaSpigot start - handle oversized packets better
        GenericFutureListener<? extends Future<? super Void>>[] listeners = null;
        if (genericfuturelistener != null || agenericfuturelistener != null) { // cannot call ArrayUtils.add with both null arguments
            listeners = ArrayUtils.add(agenericfuturelistener, 0, genericfuturelistener);
        }
        boolean connected = this.isConnected();
        if (!connected && !preparing) {
            return; // Do nothing
        }
        packet.onPacketDispatch(getPlayer());
        if (connected && (InnerUtil.canSendImmediate(this, packet) || (
            MinecraftServer.getServer().isMainThread() && packet.isReady() && this.i.isEmpty() &&
                        (packet.getExtraPackets() == null || packet.getExtraPackets().isEmpty())
        ))) {
            this.dispatchPacket(packet, listeners, null);
            return;
        }
        // write the packets to the queue, then flush - antixray hooks there already
        java.util.List<Packet> extraPackets = InnerUtil.buildExtraPackets(packet);
        boolean hasExtraPackets = extraPackets != null && !extraPackets.isEmpty();
        if (!hasExtraPackets) {
            this.i.add(new NetworkManager.QueuedPacket(packet, listeners));
        } else {
            java.util.List<NetworkManager.QueuedPacket> packets = new java.util.ArrayList<>(1 + extraPackets.size());
            packets.add(new NetworkManager.QueuedPacket(packet, (GenericFutureListener<? extends Future<? super Void>>) null)); // delay the future listener until the end of the extra packets

            for (int i = 0, len = extraPackets.size(); i < len;) {
                Packet extra = extraPackets.get(i);
                boolean end = ++i == len;
                packets.add(new NetworkManager.QueuedPacket(extra, end ? listeners : null)); // append listener to the end
            }
            this.i.addAll(packets);
        }

        this.sendPacketQueue();
        // PandaSpigot end
    }

    public void dispatchPacket(final Packet<?> packet, final GenericFutureListener<? extends Future<? super Void>>[] listeners, Boolean flushConditional) {
        this.packetWrites.getAndIncrement(); // must be before using canFlush
        boolean effectiveFlush = flushConditional == null ? this.canFlush : flushConditional;
        final boolean flush = effectiveFlush || packet instanceof PacketPlayOutKeepAlive || packet instanceof PacketPlayOutKickDisconnect || FAST_ELIGIBLE.contains(packet.getClass()); // no delay for certain packets
        final EnumProtocol enumprotocol = EnumProtocol.getProtocolForPacket(packet);
        final EnumProtocol enumprotocol1 = this.channel.attr(NetworkManager.ATTRIBUTE_PROTOCOL).get();
        if (enumprotocol1 != enumprotocol) {
            this.channel.config().setAutoRead(false);
        }
        if (this.channel.eventLoop().inEventLoop()) {
            if (enumprotocol != enumprotocol1) {
                this.setProtocol(enumprotocol);
            }
            ChannelFuture channelfuture = flush ? this.channel.writeAndFlush(packet) : this.channel.write(packet);
            if (listeners != null) {
                channelfuture.addListeners(listeners);
            }
            channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
        else {
            // FulfillSpigot start - optimise packets that are not flushed
            Runnable choice1 = null;
            AbstractEventExecutor.LazyRunnable choice2 = null;
            // note: since the type is not dynamic here, we need to actually copy the old executor code
            // into two branches. On conflict, just re-copy - no changes were made inside the executor code.
            if (flush) {
                choice1 = () -> {
                    if (enumprotocol != enumprotocol1) {
                        this.setProtocol(enumprotocol);
                    }
                    try {
                        ChannelFuture channelfuture1 = (flush) ? this.channel.writeAndFlush(packet) : this.channel.write(packet); // Tuinity - add flush parameter
                        if (listeners != null) {
                            channelfuture1.addListeners(listeners);
                        }
                        channelfuture1.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                    } catch (Exception e) {
                        FulfillSpigot.LOGGER.error("NetworkException: {}", getPlayer(), e);
                        close(new ChatMessage("disconnect.genericReason", "Internal Exception: " + e.getMessage()));;
                    }
                };
            } else {
                // explicitly declare a variable to make the lambda use the type
                choice2 = () -> {
                    if (enumprotocol != enumprotocol1) {
                        this.setProtocol(enumprotocol);
                    }
                    try {
                        // Nacho - why not remove the check below if the check is done above? just code duplication...
                        // even IntelliJ screamed at me for doing leaving it like that :shrug:
                        ChannelFuture channelfuture1 = this.channel.write(packet); // Nacho - see above // Tuinity - add flush parameter
                        if (listeners != null) {
                            channelfuture1.addListeners(listeners);
                        }
                        channelfuture1.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                    } catch (Exception e) {
                        FulfillSpigot.LOGGER.error("NetworkException: " + getPlayer(), e);
                        close(new ChatMessage("disconnect.genericReason", "Internal Exception: " + e.getMessage()));;
                    }
                };
            }
            this.channel.eventLoop().execute(choice1 != null ? choice1 : choice2);
            // FulfillSpigot end - optimise packets that are not flushed
        }
    }
    private void a(final Packet packet, final GenericFutureListener<? extends Future<? super Void>>[] agenericfuturelistener) {
        // PandaSpigot start - add flush parameter
        this.writePacket(packet, agenericfuturelistener, Boolean.TRUE);
    }
    // FulfillSpigot start - fast packets processing
    private final Object fastPacketsLock = new Object();

    public void processFastPackets() {
        if (this.fastPackets.isEmpty()) // Don't lock
            return;

        synchronized (fastPacketsLock) {
            if (this.channel != null && this.channel.isOpen()) {
                this.j.readLock().lock();
                boolean needsFlush = this.canFlush;
                boolean hasWrotePacket = false;
                try {
                    Iterator<QueuedPacket> iterator = this.fastPackets.iterator();
                    while (iterator.hasNext()) {
                        QueuedPacket queued = iterator.next();
                        Packet packet = queued.a;
                        if (hasWrotePacket && (needsFlush || this.canFlush)) flush();
                        iterator.remove();
                        this.dispatchPacket(packet, queued.b, (!iterator.hasNext() && (needsFlush || this.canFlush)) ? Boolean.TRUE : Boolean.FALSE);
                        hasWrotePacket = true;
                    }
                } finally {
                    this.j.readLock().unlock();
                }
            }
        }
    }
    // FulfillSpigot end
    private void writePacket(Packet packet, final GenericFutureListener<? extends Future<? super Void>>[] agenericfuturelistener, Boolean flushConditional) {
        this.packetWrites.getAndIncrement(); // must be before using canFlush
        boolean effectiveFlush = flushConditional == null ? this.canFlush : flushConditional;
        final boolean flush = effectiveFlush || packet instanceof PacketPlayOutKeepAlive || packet instanceof PacketPlayOutKickDisconnect; // no delay for certain packets
        // PandaSpigot end - add flush parameter
        final EnumProtocol enumprotocol = EnumProtocol.a(packet);
        final EnumProtocol enumprotocol1 = (EnumProtocol) this.channel.attr(NetworkManager.c).get();

        if (enumprotocol1 != enumprotocol) {
            NetworkManager.g.debug("Disabled auto read");
            this.channel.config().setAutoRead(false);
        }

        EntityPlayer player = getPlayer(); // PandaSpigot
        if (this.channel.eventLoop().inEventLoop()) {
            if (enumprotocol != enumprotocol1) {
                this.a(enumprotocol);
            }

            // PandaSpigot start
            if (!isConnected()) {
                packet.onPacketDispatchFinish(player, null);
                return;
            }
            try {
            // PandaSpigot end
            ChannelFuture channelfuture = (flush) ? this.channel.writeAndFlush(packet) : this.channel.write(packet); // PandaSpigot - add flush parameter

            if (agenericfuturelistener != null) {
                channelfuture.addListeners(agenericfuturelistener);
            }

            // PandaSpigot start
            if (packet.hasFinishListener()) {
                channelfuture.addListener((ChannelFutureListener) channelFuture -> packet.onPacketDispatchFinish(player, channelFuture));
            }
            // PandaSpigot end
            channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            // PandaSpigot start
            } catch (Exception e) {
                g.error("NetworkException: " + player, e);
                close(new ChatMessage("disconnect.genericReason", "Internal Exception: " + e.getMessage()));;
                packet.onPacketDispatchFinish(player, null);
            }
            // PandaSpigot end
        } else {
            Runnable command = () -> { // PandaSpigot - optimise packets that are not flushed
                    if (enumprotocol != enumprotocol1) {
                        NetworkManager.this.a(enumprotocol);
                    }

                // PandaSpigot start
                if (!isConnected()) {
                    packet.onPacketDispatchFinish(player, null);
                    return;
                }
                try {
                // PandaSpigot end
                    ChannelFuture channelfuture = (flush) ? NetworkManager.this.channel.writeAndFlush(packet) : NetworkManager.this.channel.write(packet); // PandaSpigot - add flush parameter

                    if (agenericfuturelistener != null) {
                        channelfuture.addListeners(agenericfuturelistener);
                    }

                // PandaSpigot start
                if (packet.hasFinishListener()) {
                    channelfuture.addListener((ChannelFutureListener) channelFuture -> packet.onPacketDispatchFinish(player, channelFuture));
                }
                // PandaSpigot end
                    channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                // PandaSpigot start
                } catch (Exception e) {
                    g.error("NetworkException: " + player, e);
                    close(new ChatMessage("disconnect.genericReason", "Internal Exception: " + e.getMessage()));;
                    packet.onPacketDispatchFinish(player, null);
                }
                // PandaSpigot end
            };
            // PandaSpigot start - optimise packets that are not flushed
            if (!flush) {
                io.netty.util.concurrent.AbstractEventExecutor.LazyRunnable run = command::run;
                this.channel.eventLoop().execute(run);
           } else {
                this.channel.eventLoop().execute(command);
           }
            // PandaSpigot end
        }

    }

    // PandaSpigot start - rewrite this to be safer if ran off main thread
    private boolean sendPacketQueue() { return this.m(); } // OBFHELPER // void -> boolean
    private boolean m() { // void -> boolean
        if (!isConnected()) {
            return true;
        }
        if (MinecraftServer.getServer().isMainThread()) {
            return processQueue();
        } else if (isPending) {
            // Should only happen during login/status stages
            synchronized (this.i) {
                return this.processQueue();
            }
        }
        return false;
    }
    private boolean processQueue() {
        if (this.i.isEmpty()) return true;
        // PandaSpigot start - make only one flush call per sendPacketQueue() call
        final boolean needsFlush = this.canFlush; // make only one flush call per sendPacketQueue() call
        boolean hasWrotePacket = false;
        // PandaSpigot end
        // If we are on main, we are safe here in that nothing else should be processing queue off main anymore
        // But if we are not on main due to login/status, the parent is synchronized on packetQueue
        java.util.Iterator<QueuedPacket> iterator = this.i.iterator();
        while (iterator.hasNext()) {
            NetworkManager.QueuedPacket queued = iterator.next(); // poll -> peek
            // Fix NPE (Spigot bug caused by handleDisconnection())
            if (false && queued == null) { // PandaSpigot - diff on change, this logic is redundant: iterator guarantees ret of an element - on change, hook the flush logic here
                return true;
            }

            Packet<?> packet = queued.getPacket();
            if (!packet.isReady()) {
                // PandaSpigot start - make only one flush call per sendPacketQueue() call
                if (hasWrotePacket && (needsFlush || this.canFlush)) {
                    this.flush();
                }
                // PandaSpigot end
                return false;
            } else {
                iterator.remove();
                // PandaSpigot start - make only one flush call per sendPacketQueue() call
                this.writePacket(packet, queued.getGenericFutureListeners(), (!iterator.hasNext() && (needsFlush || this.canFlush)) ? Boolean.TRUE : Boolean.FALSE);
                hasWrotePacket = true;
                // PandaSpigot end
            }
        }
        return true;
    }
    // PandaSpigot end

    public void a() {
        this.m();
        if (this.m instanceof IUpdatePlayerListBox) {
            ((IUpdatePlayerListBox) this.m).c();
        }

        if (enableExplicitFlush) this.channel.eventLoop().execute(() -> this.channel.flush()); // PandaSpigot - we don't need to explicit flush here, but allow opt-in in case issues are found to a better version
    }

    public SocketAddress getSocketAddress() {
        return this.l;
    }

    // PandaSpigot start
    public void clearPacketQueue() {
        EntityPlayer player = getPlayer();
        i.forEach(queuedPacket -> {
            Packet<?> packet = queuedPacket.getPacket();
            if (packet.hasFinishListener()) {
                packet.onPacketDispatchFinish(player, null);
            }
        });
        i.clear();
    } // PandaSpigot end
    public void close(IChatBaseComponent ichatbasecomponent) {
        this.i.clear(); // FulfillSpigot - Memory Leak fix
        // Spigot Start
        this.preparing = false;
        clearPacketQueue(); // PandaSpigot
        // Spigot End
        if (this.channel.isOpen()) {
            this.channel.close(); // We can't wait as this may be called from an event loop.
            this.n = ichatbasecomponent;
        }

    }

    public boolean c() {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }

    public void a(SecretKey secretkey) {
        this.o = true;
        this.channel.pipeline().addBefore("splitter", "decrypt", new PacketDecrypter(MinecraftEncryption.a(2, secretkey)));
        this.channel.pipeline().addBefore("prepender", "encrypt", new PacketEncrypter(MinecraftEncryption.a(1, secretkey)));
    }

    public boolean isConnected() { return this.g(); } // PandaSpigot - OBFHELPER
    public boolean g() {
        return this.channel != null && this.channel.isOpen();
    }

    public boolean h() {
        return this.channel == null;
    }

    public PacketListener getPacketListener() {
        return this.m;
    }

    public IChatBaseComponent j() {
        return this.n;
    }

    public void k() {
        this.channel.config().setAutoRead(false);
    }

    public void a(int i) {
        if (i >= 0) {
            if (this.channel.pipeline().get("decompress") instanceof PacketDecompressor) {
                ((PacketDecompressor) this.channel.pipeline().get("decompress")).a(i);
            } else {
                this.channel.pipeline().addBefore("decoder", "decompress", new PacketDecompressor(i));
            }

            if (this.channel.pipeline().get("compress") instanceof PacketCompressor) {
                ((PacketCompressor) this.channel.pipeline().get("decompress")).a(i);
            } else {
                this.channel.pipeline().addBefore("encoder", "compress", new PacketCompressor(i));
            }
        } else {
            if (this.channel.pipeline().get("decompress") instanceof PacketDecompressor) {
                this.channel.pipeline().remove("decompress");
            }

            if (this.channel.pipeline().get("compress") instanceof PacketCompressor) {
                this.channel.pipeline().remove("compress");
            }
        }

    }

    public void l() {
        if (this.channel != null && !this.channel.isOpen()) {
            if (!this.p) {
                this.p = true;
                if (this.j() != null) {
                    this.getPacketListener().a(this.j());
                } else if (this.getPacketListener() != null) {
                    this.getPacketListener().a(new ChatComponentText("Disconnected"));
                }
                clearPacketQueue(); // PandaSpigot
            } else {
                //NetworkManager.g.warn("handleDisconnection() called twice"); // PandaSpigot - Do not log useless message
            }

        }
    }

    protected void channelRead0(ChannelHandlerContext channelhandlercontext, Packet object) throws Exception { // CraftBukkit - fix decompile error
        this.a(channelhandlercontext, (Packet) object);
    }

    static class QueuedPacket {

        private final Packet a; private final Packet<?> getPacket() { return this.a; } // PandaSpigot - OBFHELPER
        private final GenericFutureListener<? extends Future<? super Void>>[] b; private final GenericFutureListener<? extends Future<? super Void>>[] getGenericFutureListeners() { return this.b; } // PandaSpigot - OBFHELPER

        public QueuedPacket(Packet packet, GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener) {
            this.a = packet;
            this.b = agenericfuturelistener;
        }
    }

    // Spigot Start
    public SocketAddress getRawAddress()
    {
        // PandaSpigot start - This can be null in the case of a Unix domain socket, so if it is, fake something
        if (this.channel.remoteAddress() == null) {
            return new java.net.InetSocketAddress(java.net.InetAddress.getLoopbackAddress(), 0);
        }
        // PandaSpigot end
        return this.channel.remoteAddress();
    }
    // Spigot End
}
