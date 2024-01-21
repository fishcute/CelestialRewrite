package fishcute.celestial.util;

import celestialexpressions.Expression;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fishcute.celestial.sky.CelestialSky;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.ArrayList;

public class ColorEntry {
    public Expression red = () -> 1.0;
    public Expression green = () -> 1.0;
    public Expression blue = () -> 1.0;
    public boolean isBasicColor = false;
    public ArrayList<MutablePair<Color, Expression>> colors;
    public Color baseColor = new Color(255, 255, 255);
    public String cloneColor = null;
    public boolean inheritColor;
    public boolean ignoreSkyEffects;
    public Color storedColor = new Color(255, 255, 255);
    public int updateFrequency;
    public int updateTick;

    public static ColorEntry createColorEntry(JsonObject o, String elementName, ColorEntry defaultEntry, boolean optionalSkyEffects) {
        if (o == null)
            return defaultEntry;
        try {
            o.get(elementName).getAsJsonObject().getAsJsonArray(elementName);
        }
        catch (Exception e) {
            if (o.has(elementName)) {

                String color = Util.getOptionalString(o, elementName, "#ffffff");
                return color.equals("inherit") ? new ColorEntry(new ArrayList<>(), "inherit", 0, false, "1", "1", "1") : new ColorEntry(Util.decodeColor(color));
            }
            return defaultEntry;
        }
        JsonObject colorObject = o.get(elementName).getAsJsonObject();

        ArrayList<MutablePair<Color, Expression>> colors = new ArrayList<>();
        String baseColor = Util.getOptionalString(colorObject, "base_color", "#ffffff");

        if (colorObject.has("colors")) {
            try {
                for (JsonElement color : colorObject.getAsJsonArray("colors")) {
                    colors.add(new MutablePair<>(
                            Util.decodeColor(Util.getOptionalString(color.getAsJsonObject(), "color", "#ffffff")),
                            Util.compileExpression(Util.getOptionalString(color.getAsJsonObject(), "alpha", "0"))
                    ));
                }
            } catch (Exception e) {
                Util.sendErrorInGame("Failed to parse color entry \"" + elementName + "\".", false);
            }
        }

        return new ColorEntry(colors, baseColor, Util.getOptionalInteger(colorObject, "update_frequency", 0), optionalSkyEffects && Util.getOptionalBoolean(colorObject, "ignore_sky_effects", false),
                Util.getOptionalString(colorObject, "red", "1"), Util.getOptionalString(colorObject, "green", "1"), Util.getOptionalString(colorObject, "blue", "1"));
    }
    public ColorEntry(ArrayList<MutablePair<Color, Expression>> colors, String baseColor, int updateFrequency, boolean ignoreSkyEffects, String r, String g, String b) {
        this.colors = colors;
        if (baseColor.equals("inherit"))
            this.inheritColor = true;
        else if (baseColor.equals("#skyColor") || baseColor.equals("#fogColor") || baseColor.equals("#cloudColor") || baseColor.equals("#twilightColor"))
            this.cloneColor = baseColor;
        else
            this.baseColor = Util.decodeColor(baseColor);
        this.updateFrequency = updateFrequency;
        this.ignoreSkyEffects = ignoreSkyEffects;
        this.red = Util.compileExpression(r);
        this.green = Util.compileExpression(g);
        this.blue = Util.compileExpression(b);
    }

    public ColorEntry(Color color) {
        this.storedColor = color;
        this.isBasicColor = true;
        this.inheritColor = false;
        this.ignoreSkyEffects = false;
    }

    public void tick() {
        if (this.isBasicColor)
            return;
        if (this.updateTick <= 0 || CelestialSky.forceUpdateVariables) {
            this.updateTick = this.updateFrequency;
            this.updateColor();
        }
        else
            this.updateTick--;
    }

    public void setInheritColor(Color c) {
        if (this.inheritColor)
            this.baseColor = c;
    }

    public void updateColor() {
        if (this.cloneColor != null && CelestialSky.doesDimensionHaveCustomSky()) {
            this.baseColor = Util.decodeColor(this.cloneColor);
        }
        this.storedColor = getResultColor();
    }

    public Color getResultColor() {
        if (colors.size() == 0)
            return new Color((int) (baseColor.getRed() * this.red.invoke().floatValue()), (int) (baseColor.getGreen() * this.green.invoke().floatValue()), (int) (baseColor.getBlue() * this.blue.invoke().floatValue()));

        int r = baseColor.getRed(), g = baseColor.getGreen(), b = baseColor.getBlue();

        double value;
        for (Pair<Color, Expression> color : this.colors) {
            value = color.getValue().invoke().floatValue();
            if (value > 1)
                value = 1;
            else if (value <= 0)
                continue;

            if (r <= 0)
                r = 1;
            if (g <= 0)
                g = 1;
            if (b <= 0)
                b = 1;

            r = (int) (r * (1 - (((float) (r - color.getKey().getRed()) / r) * value)));
            g = (int) (g * (1 - (((float) (g - color.getKey().getGreen()) / g) * value)));
            b = (int) (b * (1 - (((float) (b - color.getKey().getBlue()) / b) * value)));
        }

        return new Color((int) (r * this.red.invoke().floatValue()), (int) (g * this.green.invoke().floatValue()), (int) (b * this.blue.invoke().floatValue()));
    }
}
