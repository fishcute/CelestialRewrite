package fishcute.celestial.version.dependent.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

public class PoseStackWrapper {
    public PoseStack matrices;
    public PoseStackWrapper(PoseStack matrices) {
        this.matrices = matrices;
    }

    public Matrix4fWrapper lastPose() {
        return new Matrix4fWrapper(matrices.last().pose());
    }
    public void pushPose() {
        matrices.pushPose();
    }
    public void popPose() {
        matrices.popPose();
    }
    public void translate(double x, double y, double z) {
        matrices.translate(x, y, z);
    }
    public void mulPose(Axis a, float rot) {
        switch (a) {
            case X: matrices.mulPose(Vector3f.XP.rotationDegrees(rot));
            break;
            case Y: matrices.mulPose(Vector3f.YP.rotationDegrees(rot));
            break;
            case Z: matrices.mulPose(Vector3f.ZP.rotationDegrees(rot));
            break;
        }
    }

    public enum Axis {
        X,
        Y,
        Z
    }
}
