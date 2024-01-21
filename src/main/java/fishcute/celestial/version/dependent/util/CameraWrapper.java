package fishcute.celestial.version.dependent.util;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;

public class CameraWrapper {
    public Camera camera;
    public CameraWrapper(Camera camera) {
        this.camera = camera;
    }

    public boolean doesFogBlockSky() {
        return camera.getFluidInCamera() != FogType.POWDER_SNOW && camera.getFluidInCamera() != FogType.LAVA;
    }

    public boolean doesMobEffectBlockSky() {
        Entity entity = Minecraft.getInstance().getCameraEntity();
        if (!(entity instanceof LivingEntity livingEntity)) {
            return false;
        } else {
            return livingEntity.hasEffect(MobEffects.BLINDNESS) || livingEntity.hasEffect(MobEffects.DARKNESS);
        }
    }
}
