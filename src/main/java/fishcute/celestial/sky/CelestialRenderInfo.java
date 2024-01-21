package fishcute.celestial.sky;

import java.util.ArrayList;

public class CelestialRenderInfo {
    public final ArrayList<CelestialObject> skyObjects;

    public final CelestialEnvironmentRenderInfo environment;
    public CelestialRenderInfo(ArrayList<CelestialObject> skyObjects, CelestialEnvironmentRenderInfo environment) {
        this.skyObjects = skyObjects;
        this.environment = environment;

    }
}
