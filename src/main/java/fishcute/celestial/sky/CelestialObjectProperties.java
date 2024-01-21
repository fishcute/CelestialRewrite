package fishcute.celestial.sky;

import celestialexpressions.Expression;
import com.google.gson.JsonObject;
import fishcute.celestial.util.ColorEntry;
import fishcute.celestial.util.Util;

public class CelestialObjectProperties {
    public final boolean hasMoonPhases;
    public final Expression moonPhase;
    public final boolean isSolid;
    private final Expression red;
    private final Expression green;
    private final Expression blue;
    public final Expression alpha;
    public final boolean ignoreFog;
    public final ColorEntry color;

    public float getRed() {
        if (this.color == null)
            return red.invoke().floatValue();
        return (color.storedColor.getRed() / 255.0F) * red.invoke().floatValue();
    }
    public float getGreen() {
        if (this.color == null)
            return green.invoke().floatValue();
        return (color.storedColor.getGreen() / 255.0F) * green.invoke().floatValue();
    }
    public float getBlue() {
        if (this.color == null)
            return blue.invoke().floatValue();
        return (color.storedColor.getBlue() / 255.0F) * blue.invoke().floatValue();
    }

    public CelestialObjectProperties(boolean hasMoonPhases, String moonPhase, boolean isSolid, String red, String green, String blue, String alpha, boolean ignoreFog, ColorEntry color) {
        this.hasMoonPhases = hasMoonPhases;
        this.moonPhase = Util.compileExpression(moonPhase);
        this.isSolid = isSolid;
        this.red = Util.compileExpression(red);
        this.green = Util.compileExpression(green);
        this.blue = Util.compileExpression(blue);
        this.alpha = Util.compileExpression(alpha);
        this.ignoreFog = ignoreFog;
        this.color = color;
    }
    public static CelestialObjectProperties createCelestialObjectPropertiesFromJson(JsonObject o) {
        return new CelestialObjectProperties(
                Util.getOptionalBoolean(o, "has_moon_phases", false),
                Util.getOptionalString(o, "moon_phase", "#moonPhase"),
                Util.getOptionalBoolean(o, "is_solid", false),
                Util.getOptionalString(o, "red", "1"),
                Util.getOptionalString(o, "green", "1"),
                Util.getOptionalString(o, "blue", "1"),
                Util.getOptionalString(o, "alpha", "1"),
                Util.getOptionalBoolean(o, "ignore_fog", false),
                ColorEntry.createColorEntry(o, "color", null, false)
        );
    }
}
