package net.minecraft.server;

public abstract class EntityFlying extends EntityInsentient {

    public EntityFlying(World world) {
        super(world);
    }

    public void e(float f, float f1) {}

    protected void a(double d0, boolean flag, Block block, BlockPosition blockposition) {}

    @Override
    public void g(float f, float f1) {
        if (this.V()) {
            this.a(f, f1, 0.02F);
            this.move(this.motX, this.motY, this.motZ);
            this.motX *= 0.800000011920929D;
            this.motY *= 0.800000011920929D;
            this.motZ *= 0.800000011920929D;
        } else if (this.ab()) {
            this.a(f, f1, 0.02F);
            this.move(this.motX, this.motY, this.motZ);
            this.motX *= 0.5D;
            this.motY *= 0.5D;
            this.motZ *= 0.5D;
        } else {
            float f2 = 0.91F;

            if (this.onGround) {
                f2 = this.world.getType(new BlockPosition(MathHelper.floor(this.locX), MathHelper.floor(this.getBoundingBox().b) - 1, MathHelper.floor(this.locZ))).getBlock().frictionFactor * 0.91F;
            }

            float f3 = 0.16277136F / (f2 * f2 * f2);

            this.a(f, f1, this.onGround ? 0.1F * f3 : 0.02F);
            f2 = 0.91F;
            if (this.onGround) {
                f2 = this.world.getType(new BlockPosition(MathHelper.floor(this.locX), MathHelper.floor(this.getBoundingBox().b) - 1, MathHelper.floor(this.locZ))).getBlock().frictionFactor * 0.91F;
            }

            this.move(this.motX, this.motY, this.motZ);
            this.motX *= (double) f2;
            this.motY *= (double) f2;
            this.motZ *= (double) f2;
        }

        this.aA = this.aB;
        double d0 = this.locX - this.lastX;
        double d1 = this.locZ - this.lastZ;
        float f4 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;

        if (f4 > 1.0F) {
            f4 = 1.0F;
        }

        this.aB += (f4 - this.aB) * 0.4F;
        this.aC += this.aB;
    }

    @Override
    public boolean k_() {
        return false;
    }
}
