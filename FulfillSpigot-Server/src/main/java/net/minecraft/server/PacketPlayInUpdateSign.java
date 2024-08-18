package net.minecraft.server;

import java.io.IOException;

public class PacketPlayInUpdateSign implements Packet<PacketListenerPlayIn> {

    private BlockPosition a;
    private IChatBaseComponent[] b;

    public PacketPlayInUpdateSign() {}

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.c();
        this.b = new IChatBaseComponent[4];

        for (int i = 0; i < 4; ++i) {
            String s = packetdataserializer.c(384);
            IChatBaseComponent ichatbasecomponent = IChatBaseComponent.ChatSerializer.a(s);

            this.b[i] = ichatbasecomponent;
        }

    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a);

        for (int i = 0; i < 4; ++i) {
            IChatBaseComponent ichatbasecomponent = this.b[i];
            String s = IChatBaseComponent.ChatSerializer.a(ichatbasecomponent);

            packetdataserializer.a(s);
        }

    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public BlockPosition a() {
        return this.a;
    }

    public IChatBaseComponent[] b() {
        return this.b;
    }
}
