package fishcute.celestial.util;

import fishcute.celestial.CelestialClient;
import fishcute.celestial.sky.CelestialSky;
import fishcute.celestial.version.dependent.VMinecraftInstance;

public class ClientTick {

    public static boolean dimensionHasCustomSky = false;

    static void updateStars() {
    }

    public static void reload() {
        CelestialSky.loadResources();
        Util.errorList.clear();
    }

    public static void tick() {
        if (VMinecraftInstance.doesLevelExist())
            worldTick();
    }

    public static void worldTick() {
        dimensionHasCustomSky = CelestialSky.dimensionSkyMap.containsKey(VMinecraftInstance.getLevelPath());
        updateStars();
        CelestialSky.updateVariableValues();

        if (CelestialSky.doesDimensionHaveCustomSky()) {
            CelestialSky.getDimensionRenderInfo().environment.skyColor.setInheritColor(Util.getSkyColor());
            CelestialSky.getDimensionRenderInfo().environment.fogColor.setInheritColor(Util.getFogColor());
        }

        while (CelestialClient.reloadSky.consumeClick()) {
            CelestialSky.loadResources();
            if (!CelestialClient.hasShownWarning) {
                CelestialClient.hasShownWarning = true;
                VMinecraftInstance.sendErrorMessage("Note: This will not reload textures. Use F3-T to reload textures.");
            }
        }
    }
}
