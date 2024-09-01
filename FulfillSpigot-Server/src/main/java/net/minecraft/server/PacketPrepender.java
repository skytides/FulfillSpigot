package net.minecraft.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import xyz.tavenservices.spigot.network.VarIntUtil;

@io.netty.channel.ChannelHandler.Sharable // PandaSpigot
public class PacketPrepender extends MessageToByteEncoder<ByteBuf> {

    public static final PacketPrepender INSTANCE = new PacketPrepender(); // PandaSpigot
    private PacketPrepender() {} // PandaSpigot - private

    protected void a(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, ByteBuf bytebuf1) throws Exception {
        // PandaSpigot start
        VarIntUtil.writeVarInt(bytebuf1, bytebuf.readableBytes());
        bytebuf1.writeBytes(bytebuf);
        // PandaSpigot end
    }

    protected void encode(ChannelHandlerContext channelhandlercontext, ByteBuf object, ByteBuf bytebuf) throws Exception {
        this.a(channelhandlercontext, (ByteBuf) object, bytebuf);
    }
    // PandaSpigot start
    @Override
    protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBuf msg, boolean preferDirect) throws Exception {
        int anticipatedRequiredCapacity = VarIntUtil.varIntBytes(msg.readableBytes())
            + msg.readableBytes();
        
        return ctx.alloc().directBuffer(anticipatedRequiredCapacity);
    }
    // PandaSpigot end
}
