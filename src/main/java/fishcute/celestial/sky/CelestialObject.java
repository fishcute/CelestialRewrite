package fishcute.celestial.sky;

import celestialexpressions.Expression;
import com.google.gson.JsonObject;
import fishcute.celestial.util.ColorEntry;
import fishcute.celestial.util.Util;
import fishcute.celestial.version.dependent.util.ResourceLocationWrapper;

import java.util.ArrayList;

public class CelestialObject {
    public final Expression scale;
    public final CelestialObjectType type;
    public Expression posX;
    public Expression posY;
    public Expression posZ;
    public final Expression distance;
    public Expression degreesX;
    public Expression degreesY;
    public Expression degreesZ;
    public final Expression baseDegreesX;
    public final Expression baseDegreesY;
    public final Expression baseDegreesZ;

    public double populateDegreesX;
    public double populateDegreesY;
    public double populateDegreesZ;
    public double populateScaleAdd;
    public double populateDistanceAdd;
    public double populatePosX;
    public double populatePosY;
    public double populatePosZ;

    public ColorEntry solidColor;

    public ResourceLocationWrapper texture;

    public SkyBoxObjectProperties skyBoxProperties;

    public final CelestialObjectProperties celestialObjectProperties;

    public final ArrayList<Util.VertexPoint> vertexList;

    public CelestialObject(CelestialObjectType type, String texturePath, String scale, String posX, String posY, String posZ, String distance, String degreesX, String degreesY, String degreesZ, String baseDegreesX, String baseDegreesY, String baseDegreesZ, CelestialObjectProperties celestialObjectProperties, String parent, String dimension, ColorEntry color, ArrayList<Util.VertexPoint> vertexList, SkyBoxObjectProperties skyBoxProperties) {
        this.type = type;
        if (parent != null) {
            CelestialObject o = createSkyObjectFromJson(CelestialSky.getFile("celestial:sky/" + dimension + "/objects/" + parent + ".json"), parent, dimension);
            this.posX = Util.compileExpression(o.posX + "+" + posX);
            this.posY = Util.compileExpression(o.posY + "+" + posY);
            this.posZ = Util.compileExpression(o.posZ + "+" + posZ);
            this.distance = Util.compileExpression(o.distance + "+" + distance);
            this.degreesX = Util.compileExpression(o.degreesX + "+" + degreesX);
            this.degreesY = Util.compileExpression(o.degreesY + "+" + degreesY);
            this.degreesZ = Util.compileExpression(o.degreesZ + "+" + degreesZ);
            this.baseDegreesX = Util.compileExpression(o.baseDegreesX + "+" + baseDegreesX);
            this.baseDegreesY = Util.compileExpression(o.baseDegreesY + "+" + baseDegreesY);
            this.baseDegreesZ = Util.compileExpression(o.baseDegreesZ + "+" + baseDegreesZ);
        }
        else {
            this.posX = Util.compileExpression(posX);
            this.posY = Util.compileExpression(posY);
            this.posZ = Util.compileExpression(posZ);
            this.distance = Util.compileExpression(distance);
            this.degreesX = Util.compileExpression(degreesX);
            this.degreesY = Util.compileExpression(degreesY);
            this.degreesZ = Util.compileExpression(degreesZ);
            this.baseDegreesX = Util.compileExpression(baseDegreesX);
            this.baseDegreesY = Util.compileExpression(baseDegreesY);
            this.baseDegreesZ = Util.compileExpression(baseDegreesZ);
        }
        this.scale = Util.compileExpression(scale);
        this.celestialObjectProperties = celestialObjectProperties;
        this.vertexList = vertexList;
        this.skyBoxProperties = skyBoxProperties;
        if (texturePath != null)
            this.texture = new ResourceLocationWrapper(texturePath);
        if (color != null)
            this.solidColor = color;
    }

    // Used for populate objects only
    public CelestialObject(CelestialObjectType type, ResourceLocationWrapper texture, Expression scale, double scaleAdd, double posX, double posY, double posZ, Expression distance, double distanceAdd, double degreesX, double degreesY, double degreesZ, Expression baseDegreesX, Expression baseDegreesY, Expression baseDegreesZ, CelestialObjectProperties celestialObjectProperties, ColorEntry color, ArrayList<Util.VertexPoint> vertexList) {
        this.type = type;
        this.texture = texture;
        this.scale = scale;
        this.populatePosX = posX;
        this.populatePosY = posY;
        this.populatePosZ = posZ;
        this.populateScaleAdd = scaleAdd;
        this.populateDistanceAdd = distanceAdd;
        this.distance = distance;
        this.populateDegreesX = degreesX;
        this.populateDegreesY = degreesY;
        this.populateDegreesZ = degreesZ;
        this.baseDegreesX = baseDegreesX;
        this.baseDegreesY = baseDegreesY;
        this.baseDegreesZ = baseDegreesZ;
        this.celestialObjectProperties = celestialObjectProperties;
        this.vertexList = vertexList;
        this.solidColor = color;
        this.skyBoxProperties = null;
    }

    public boolean isPopulation() {
        return false;
    }

    public static CelestialObject createSkyObjectFromJson(JsonObject o, String name, String dimension) {
        if (o == null) {
            Util.warn("Failed to load celestial object \"" + name + ".json\", as it did not exist.");
            return null;
        }

        CelestialObjectType type = findObjectType(o);

        if (type == CelestialObjectType.SKYBOX)
            return createSkyBoxObjectFromJson(o, name, dimension);

        JsonObject display = o.getAsJsonObject("display");
        JsonObject rotation = o.getAsJsonObject("rotation");
        //I love parameters
        CelestialObject object = new CelestialObject(
                type,
                Util.getOptionalString(o, "texture", null),
                Util.getOptionalString(display, "scale", "0"),
                Util.getOptionalString(display, "pos_x", "0"),
                Util.getOptionalString(display, "pos_y", "0"),
                Util.getOptionalString(display, "pos_z", "0"),
                Util.getOptionalString(display, "distance", "0"),
                Util.getOptionalString(rotation, "degrees_x", "0"),
                Util.getOptionalString(rotation, "degrees_y", "0"),
                Util.getOptionalString(rotation, "degrees_z", "0"),
                Util.getOptionalString(rotation, "base_degrees_x", "-90"),
                Util.getOptionalString(rotation, "base_degrees_y", "0"),
                Util.getOptionalString(rotation, "base_degrees_z", "-90"),
                CelestialObjectProperties.createCelestialObjectPropertiesFromJson(o.getAsJsonObject("properties")),
                Util.getOptionalString(o, "parent", null),
                dimension,
                ColorEntry.createColorEntry(o, "solid_color", null, false),
                Util.convertToPointUvList(o, "vertex"),
                null
        );

        //Check if it's normal
        if (!o.has("populate")) {
            return object;
        }
        //Or... it's not :(
        else {
            return CelestialPopulateProperties.getPopulationPropertiesFromJson(o.getAsJsonObject("populate")).generatePopulateObjects(object,
                    o.getAsJsonObject("populate"));
        }
    }

    public static CelestialObject createSkyBoxObjectFromJson(JsonObject o, String name, String dimension) {
        JsonObject display = o.getAsJsonObject("display");
        JsonObject rotation = o.getAsJsonObject("rotation");
        //I love parameters

        CelestialObject object = new CelestialObject(
                findObjectType(o),
                Util.getOptionalString(o, "texture", null),
                Util.getOptionalString(display, "scale", "0"),
                Util.getOptionalString(display, "pos_x", "0"),
                Util.getOptionalString(display, "pos_y", "0"),
                Util.getOptionalString(display, "pos_z", "0"),
                Util.getOptionalString(display, "distance", "0"),
                Util.getOptionalString(rotation, "degrees_x", "0"),
                Util.getOptionalString(rotation, "degrees_y", "0"),
                Util.getOptionalString(rotation, "degrees_z", "0"),
                Util.getOptionalString(rotation, "base_degrees_x", "-90"),
                Util.getOptionalString(rotation, "base_degrees_y", "0"),
                Util.getOptionalString(rotation, "base_degrees_z", "-90"),
                CelestialObjectProperties.createCelestialObjectPropertiesFromJson(o.getAsJsonObject("properties")),
                Util.getOptionalString(o, "parent", null),
                dimension,
                ColorEntry.createColorEntry(o, "solid_color", null, false),
                null,
                SkyBoxObjectProperties.getSkyboxPropertiesFromJson(o)
        );
        return object;
    }

    public enum CelestialObjectType {
        DEFAULT,
        COLOR,
        SKYBOX
    }

    public static CelestialObjectType findObjectType(JsonObject o) {
        String objectType = Util.getOptionalString(o, "type", "default");
        if (!objectType.equals("skybox")) {
            if (o.has("texture"))
                return CelestialObjectType.DEFAULT;
            else if (o.has("solid_color"))
                return CelestialObjectType.COLOR;
        }
        return getCelestialObjectType(objectType);
    }
    public static CelestialObjectType getCelestialObjectType(String i) {
        switch (i) {
            case "color":
                return CelestialObjectType.COLOR;
            case "skybox":
                return CelestialObjectType.SKYBOX;
            default:
                return CelestialObjectType.DEFAULT;
        }
    }
}
