package net.minecraft.server;

public class SourceBlock implements ISourceBlock {

    private final World a;
    private final BlockPosition b;

    public SourceBlock(World world, BlockPosition blockposition) {
        this.a = world;
        this.b = blockposition;
    }

    public World getWorld() {
        return this.a;
    }

    public double getX() {
        return (double) this.b.getX() + 0.5D;
    }

    public double getY() {
        return (double) this.b.getY() + 0.5D;
    }

    public double getZ() {
        return (double) this.b.getZ() + 0.5D;
    }

    public BlockPosition getBlockPosition() {
        return this.b;
    }

    public int f() {
        IBlockData iblockdata = this.a.getType(this.b);

        return iblockdata.getBlock().toLegacyData(iblockdata);
    }

    public <T extends TileEntity> T getTileEntity() {
        return (T) this.a.getTileEntity(this.b);
    }
}
