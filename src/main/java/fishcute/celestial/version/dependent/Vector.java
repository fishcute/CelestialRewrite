package fishcute.celestial.version.dependent;

import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class Vector {
    private BlockPos.MutableBlockPos blockPos;
    public static Vector fromVec(Vec3 v) {
        return new Vector(v.x, v.y, v.z);
    }

    public Vec3 toVec() {
        return new Vec3(this.x, this.y, this.z);
    }
    public double x;
    public double y;
    public double z;
    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        blockPos = new BlockPos.MutableBlockPos(x, y, z);
    }
    public void set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.blockPos.set(x, y, z);
    }
    public BlockPos toBlockPos() {
        return this.blockPos;
    }
    // Putting this here to keep vec things in one file
    public static Vec3 toVecFromArray(double[] a) {
        return new Vec3(a[0], a[1], a[2]);
    }
}
