package fishcute.celestial.version.independent;

import com.mojang.blaze3d.systems.RenderSystem;
import fishcute.celestial.sky.CelestialSky;
import fishcute.celestial.util.Util;

import fishcute.celestial.version.dependent.Vector;
import fishcute.celestial.version.dependent.VMinecraftInstance;
import fishcute.celestial.version.dependent.VRenderSystem;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

public class VersionSky {
    public static int getFogColor(int defaultBiomeColor, int biomeColor) {
        if (CelestialSky.doesDimensionHaveCustomSky() && !VMinecraftInstance.disableFogChanges()) {
            if (Util.getRealFogColor) {
                return biomeColor;
            }
            else
                return Util.getDecimal(CelestialSky.getDimensionRenderInfo().environment.fogColor.storedColor);
        }
        return defaultBiomeColor;
    }
    public static int getSkyColor(int defaultSkyColor) {
        if (CelestialSky.doesDimensionHaveCustomSky() && !VMinecraftInstance.disableFogChanges()) {
            if (Util.getRealSkyColor) {
                return defaultSkyColor;
            }
            else
                return Util.getDecimal(CelestialSky.getDimensionRenderInfo().environment.skyColor.storedColor);
        }
        return defaultSkyColor;
    }
    public static double[] getCloudColor(double[] defaultCloudColor, float f) {
        if (CelestialSky.doesDimensionHaveCustomSky()) {
            if (CelestialSky.getDimensionRenderInfo().environment.cloudColor.ignoreSkyEffects) {
                CelestialSky.getDimensionRenderInfo().environment.cloudColor.setInheritColor(new Color(255,  255, 255));
                return new double[]{CelestialSky.getDimensionRenderInfo().environment.cloudColor.storedColor.getRed() / 255.0F, CelestialSky.getDimensionRenderInfo().environment.cloudColor.storedColor.getGreen() / 255.0F, CelestialSky.getDimensionRenderInfo().environment.cloudColor.storedColor.getBlue() / 255.0F};
            }
            else
                CelestialSky.getDimensionRenderInfo().environment.cloudColor.setInheritColor(new Color(255, 255, 255));
        }
        return defaultCloudColor;
    }
    public static float getCloudColorRed(float previousRed) {
        if (CelestialSky.doesDimensionHaveCustomSky())
            return (CelestialSky.getDimensionRenderInfo().environment.cloudColor.storedColor.getRed() / 255f) * previousRed;
        return previousRed;
    }
    public static float getCloudColorGreen(float previousGreen) {
        if (CelestialSky.doesDimensionHaveCustomSky())
            return (CelestialSky.getDimensionRenderInfo().environment.cloudColor.storedColor.getGreen() / 255f) * previousGreen;
        return previousGreen;
    }
    public static float getCloudColorBlue(float previousBlue) {
        if (CelestialSky.doesDimensionHaveCustomSky())
            return (CelestialSky.getDimensionRenderInfo().environment.cloudColor.storedColor.getBlue() / 255f) * previousBlue;
        return previousBlue;
    }
    public static double[] getClientLevelSkyColor(double[] defaultSkyColor) {
        if (CelestialSky.doesDimensionHaveCustomSky() && CelestialSky.getDimensionRenderInfo().environment.skyColor.ignoreSkyEffects) {
            return new double[]{((double) CelestialSky.getDimensionRenderInfo().environment.skyColor.storedColor.getRed()) / 255,
                    ((double) CelestialSky.getDimensionRenderInfo().environment.skyColor.storedColor.getGreen()) / 255,
                    ((double) CelestialSky.getDimensionRenderInfo().environment.skyColor.storedColor.getBlue()) / 255};
        }
        return defaultSkyColor;
    }

    public static void getCloudHeight(CallbackInfoReturnable<Float> info) {
        if (VMinecraftInstance.doesLevelExist() &&
                CelestialSky.doesDimensionHaveCustomSky())
            info.setReturnValue(CelestialSky.getDimensionRenderInfo().environment.cloudHeight.invoke().floatValue());
    }

    public static void getSunriseColor(float skyAngle, float tickDelta, CallbackInfoReturnable<float[]> info) {
        if (CelestialSky.doesDimensionHaveCustomSky()) {
            float[] rgba = new float[4];

            float g = (float) (Math.cos(skyAngle * 6.2831855F) - 0.0F);
            if (g >= -0.4F && g <= 0.4F) {
                float i = (g + 0.0F) / 0.4F * 0.5F + 0.5F;
                float j = (float) (1.0F - (1.0F - Math.sin(i * 3.1415927F)) * 0.99F);
                j *= j;

                CelestialSky.getDimensionRenderInfo().environment.twilightColor.setInheritColor(new Color(
                        i * 0.3F + 0.7F, i * i * 0.7F + 0.2F, i * i * 0.0F + 0.2F
                ));

                rgba[0] = i * 0.3F + (CelestialSky.getDimensionRenderInfo().environment.twilightColor.storedColor.getRed() / 255.0F);
                rgba[1] = i * i * 0.7F + (CelestialSky.getDimensionRenderInfo().environment.twilightColor.storedColor.getGreen() / 255.0F);
                rgba[2] = i * i * 0.0F + (CelestialSky.getDimensionRenderInfo().environment.twilightColor.storedColor.getBlue() / 255.0F);

                rgba[3] = Math.min(j, CelestialSky.getDimensionRenderInfo().environment.twilightAlpha.invoke().floatValue());
                info.setReturnValue(rgba);
            } else {
                info.setReturnValue(null);
            }
        }
    }

    public static boolean checkThickFog(boolean thickFog) {
        if (CelestialSky.doesDimensionHaveCustomSky() && CelestialSky.getDimensionRenderInfo().environment.useSimpleFog() && !VMinecraftInstance.disableFogChanges())
            return CelestialSky.getDimensionRenderInfo().environment.hasThickFog;
        return thickFog;
    }

    public static void setupFog() {
        if (CelestialSky.doesDimensionHaveCustomSky() && !CelestialSky.getDimensionRenderInfo().environment.useSimpleFog() && !VMinecraftInstance.disableFogChanges()) {
            VRenderSystem.setShaderFogStart(CelestialSky.getDimensionRenderInfo().environment.fogStart.invoke().floatValue());
            VRenderSystem.setShaderFogEnd(CelestialSky.getDimensionRenderInfo().environment.fogEnd.invoke().floatValue());
        }
    }

    public static boolean canModifyFogColor() {
        return CelestialSky.getDimensionRenderInfo().environment.fogColor.ignoreSkyEffects && !VMinecraftInstance.disableFogChanges();

    }

    public static float[] getFogColor() {
        float[] colors = new float[3];
        if (canModifyFogColor()) {
            colors[0] = CelestialSky.getDimensionRenderInfo().environment.fogColor.storedColor.getRed() / 255.0F;
            colors[1] = CelestialSky.getDimensionRenderInfo().environment.fogColor.storedColor.getGreen() / 255.0F;
            colors[2] = CelestialSky.getDimensionRenderInfo().environment.fogColor.storedColor.getBlue() / 255.0F;
        }
        else {
            return null;
        }
        return colors;
    }

    public static float[] setupFogColor() {
        if (!CelestialSky.doesDimensionHaveCustomSky())
            return null;
        float[] color = getFogColor();
        if (color != null) {
            VRenderSystem.clearColor(color[0], color[1], color[2], 0);
        }
        return color;
    }
}
