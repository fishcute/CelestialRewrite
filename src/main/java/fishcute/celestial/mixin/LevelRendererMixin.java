package fishcute.celestial.mixin;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.*;
import fishcute.celestial.sky.*;
import fishcute.celestial.version.dependent.util.*;
import fishcute.celestial.version.independent.VersionLevelRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Shadow
    private VertexBuffer skyBuffer;
    @Shadow
    private VertexBuffer darkBuffer;

    @Shadow
    private ClientLevel level;
    @Shadow
    private BufferBuilder.RenderedBuffer drawStars(BufferBuilder buffer) {
        return null;
    }

    private PoseStackWrapper poseStackWrapper = new PoseStackWrapper(null);
    private Matrix4fWrapper matrix4fWrapper = new Matrix4fWrapper(null);
    private CameraWrapper cameraWrapper = new CameraWrapper(null);
    private VertexBufferWrapper skyBufferWrapper = new VertexBufferWrapper(null);
    private VertexBufferWrapper darkBufferWrapper = new VertexBufferWrapper(null);
    private LevelWrapper levelWrapper = new LevelWrapper(null);
    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    private void renderSky(PoseStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean bl, Runnable runnable, CallbackInfo info) {
        if (CelestialSky.doesDimensionHaveCustomSky()) {
            info.cancel();
            runnable.run();

            poseStackWrapper.matrices = matrices;
            matrix4fWrapper.matrix = projectionMatrix;
            cameraWrapper.camera = camera;
            skyBufferWrapper.buffer = skyBuffer;
            darkBufferWrapper.buffer = darkBuffer;
            levelWrapper.level = level;

            VersionLevelRenderer.renderLevel(matrix4fWrapper, poseStackWrapper, skyBufferWrapper, darkBufferWrapper, cameraWrapper, levelWrapper, tickDelta);
        }
    }
}
