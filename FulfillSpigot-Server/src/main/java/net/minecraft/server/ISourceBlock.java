package net.minecraft.server;

public interface ISourceBlock extends ILocationSource {

    double getX();

    double getY();

    double getZ();

    BlockPosition getBlockPosition();

    int f();

    <T extends TileEntity> T getTileEntity();
}
