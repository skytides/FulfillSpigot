package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.GenericFutureListener;
import io.papermc.paper.network.ChannelInitializeListenerHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spigotmc.SpigotConfig;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerConnection {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final WriteBufferWaterMark SERVER_WRITE_MARK = new WriteBufferWaterMark(0x100000, 0x200000);
    private final EventGroupType eventGroupType;
    public static LazyInitVar<EventLoopGroup> a;
    public static LazyInitVar<EventLoopGroup> b;
    public static final LazyInitVar<DefaultEventLoopGroup> c;
    private final MinecraftServer server;
    public volatile boolean started;
    private final List<ChannelFuture> g = Collections.synchronizedList(Lists.newArrayList());
    private final List<NetworkManager> h = Collections.synchronizedList(Lists.newArrayList());
    public final Queue<NetworkManager> pending = new ConcurrentLinkedQueue<>();

    public ServerConnection(MinecraftServer minecraftserver) {
        this.server = minecraftserver;
        this.started = true;
        if (this.server.ai()) {
            if (Epoll.isAvailable()) {
                this.eventGroupType = EventGroupType.EPOLL;
                return;
            }
            if (KQueue.isAvailable()) {
                this.eventGroupType = EventGroupType.KQUEUE;
                return;
            }
        }
        this.eventGroupType = this.server.getTransport();
    }

    private void addPending() {
        NetworkManager manager;
        while ((manager = this.pending.poll()) != null) {
            this.getConnectedChannels().add(manager);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void a(InetAddress ip, int port) throws IOException {
        List<ChannelFuture> list = this.getListeningChannels();
        synchronized (list) {
            Class channel = null;
            int workerThreadCount = Runtime.getRuntime().availableProcessors();
            switch (this.eventGroupType) {
                default: {
                    LOGGER.info("Finding best event group type using fall-through");
                }
                case EPOLL: {
                    if (Epoll.isAvailable()) {
                        a = new LazyInitVar<>(() -> new EpollEventLoopGroup(2));
                        b = new LazyInitVar<>(() -> new EpollEventLoopGroup(workerThreadCount));
                        channel = EpollServerSocketChannel.class;
                        LOGGER.info("Using epoll");
                        break;
                    }
                }
                case KQUEUE: {
                    if (KQueue.isAvailable()) {
                        a = new LazyInitVar<>(() -> new KQueueEventLoopGroup(2));
                        b = new LazyInitVar<>(() -> new KQueueEventLoopGroup(workerThreadCount));
                        channel = KQueueServerSocketChannel.class;
                        LOGGER.info("Using kqueue");
                        break;
                    }
                }
                case NIO: {
                    a = new LazyInitVar<>(() -> new NioEventLoopGroup(2));
                    b = new LazyInitVar<>(() -> new NioEventLoopGroup(workerThreadCount));
                    channel = NioServerSocketChannel.class;
                    LOGGER.info("Using NIO");
                }
            }
            this.g.add(((ServerBootstrap) ((ServerBootstrap) new ServerBootstrap().channel(channel)).childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, SERVER_WRITE_MARK).childHandler(new ChannelInitializer() {

                @Override
                protected void initChannel(Channel channel) throws Exception {
                    try {
                        ChannelConfig config = channel.config();
                        config.setOption(ChannelOption.TCP_NODELAY, true);
                        config.setOption(ChannelOption.TCP_FASTOPEN, 1);
                        config.setOption(ChannelOption.TCP_FASTOPEN_CONNECT, true);
                        config.setOption(ChannelOption.IP_TOS, 24);
                        config.setAllocator(ByteBufAllocator.DEFAULT);
                    } catch (ChannelException e) {
                        e.printStackTrace();
                    }
                    channel.pipeline().addLast("timeout", (ChannelHandler) new ReadTimeoutHandler(30)).addLast("legacy_query", (ChannelHandler) new LegacyPingHandler(ServerConnection.this)).addLast("splitter", (ChannelHandler) new PacketSplitter()).addLast("decoder", (ChannelHandler) new PacketDecoder(EnumProtocolDirection.SERVERBOUND)).addLast("prepender", (ChannelHandler) PacketPrepender.INSTANCE).addLast("encoder", (ChannelHandler) new PacketEncoder(EnumProtocolDirection.CLIENTBOUND));
                    NetworkManager networkmanager = new NetworkManager(EnumProtocolDirection.SERVERBOUND);
                    ServerConnection.this.h.add(networkmanager);
                    channel.pipeline().addLast("packet_handler", (ChannelHandler) networkmanager);
                    networkmanager.a(new HandshakeListener(ServerConnection.this.server, networkmanager));
                    ChannelInitializeListenerHolder.callListeners(channel);
                }
            }).group(a.get(), b.get()).localAddress(ip, port)).bind().syncUninterruptibly());
        }
    }

    public void stopServer() throws InterruptedException {
        this.started = false;
        LOGGER.info("Shutting down event loops");
        for (ChannelFuture channelfuture : this.g) {
            try {
                channelfuture.channel().close().sync();
            } finally {
                a.get().shutdownGracefully();
                b.get().shutdownGracefully();
                c.get().shutdownGracefully();
            }
        }
    }

    public void b() {
        try {
            this.stopServer();
        } catch (InterruptedException e) {
            LOGGER.info("Error occurred while flushing and closing netty channels!");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void c() {
        List<NetworkManager> list = this.h;
        synchronized (list) {
            this.addPending();
            if (SpigotConfig.playerShuffle > 0 && MinecraftServer.currentTick % SpigotConfig.playerShuffle == 0) {
                Collections.shuffle(this.h);
            }
            Iterator<NetworkManager> iterator = this.h.iterator();
            while (iterator.hasNext()) {
                final NetworkManager networkmanager = iterator.next();
                if (networkmanager.h()) continue;
                if (!networkmanager.g()) {
                    if (networkmanager.preparing) continue;
                    iterator.remove();
                    networkmanager.l();
                    continue;
                }
                try {
                    networkmanager.a();
                } catch (Exception exception) {
                    if (networkmanager.c()) {
                        CrashReport crashreport = CrashReport.a(exception, "Ticking memory connection");
                        CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Ticking connection");
                        crashreportsystemdetails.a("Connection", new Callable() {

                            public String a() throws Exception {
                                return networkmanager.toString();
                            }

                            @Override
                            public Object call() throws Exception {
                                return this.a();
                            }
                        });
                        throw new ReportedException(crashreport);
                    }
                    LOGGER.warn("Failed to handle packet for " + networkmanager.getSocketAddress(), (Throwable) exception);
                    ChatComponentText chatcomponenttext = new ChatComponentText("Internal server error");
                    networkmanager.handleConnectionPacket(new PacketPlayOutKickDisconnect(chatcomponenttext), future -> networkmanager.close(chatcomponenttext), new GenericFutureListener[0]);
                    networkmanager.k();
                }
            }
        }
    }

    public MinecraftServer d() {
        return this.server;
    }

    public List<ChannelFuture> getListeningChannels() {
        return this.g;
    }

    public List<NetworkManager> getConnectedChannels() {
        return this.h;
    }

    public List<NetworkManager> getNetworkManagers() {
        return this.h;
    }

    public List<ChannelFuture> getListeners() {
        return this.g;
    }

    static {
        c = new LazyInitVar<>(() -> new DefaultEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Server IO #%d").setDaemon(true).build()));
    }

    public static enum EventGroupType {
        EPOLL,
        KQUEUE,
        NIO,
        DEFAULT

    }
}
