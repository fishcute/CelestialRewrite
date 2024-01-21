package fishcute.celestial.sky;

import celestialexpressions.Expression;
import com.google.gson.JsonObject;
import fishcute.celestial.util.Util;
import fishcute.celestial.version.dependent.VMinecraftInstance;
import fishcute.celestial.version.dependent.util.ResourceLocationWrapper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class SkyBoxObjectProperties {
    public ArrayList<SkyBoxSideTexture> sides;
    public Expression skyBoxSize;

    public Expression textureSizeX;
    public Expression textureSizeY;

    public SkyBoxObjectProperties(ArrayList<SkyBoxSideTexture> sides, String skyBoxSize, String textureSizeX, String textureSizeY) {
        this.sides = sides;
        this.skyBoxSize = Util.compileExpression(skyBoxSize);
        this.textureSizeX = Util.compileExpression(textureSizeX);
        this.textureSizeY = Util.compileExpression(textureSizeY);
    }

    public static SkyBoxObjectProperties getSkyboxPropertiesFromJson(JsonObject o) {
        String texture;

        if (o.has("skybox"))
            texture = Util.getOptionalString(o.get("skybox").getAsJsonObject(), "texture", Util.getOptionalString(o, "texture", ""));
        else
            texture = Util.getOptionalString(o, "texture", "");

        int textureWidth = 0;
        int textureHeight = 0;
        try {
            BufferedImage b = ImageIO.read(VMinecraftInstance.getResource(texture));
            textureWidth = b.getWidth();
            textureHeight = b.getHeight();
        }
        catch (Exception ignored) {}

        if (!o.has("skybox")) {
            // Returns if there is no skybox entry
            return new SkyBoxObjectProperties(createDefaultSkybox(
                    new ResourceLocationWrapper(texture), (textureHeight / 2) + ""
            ),
                    Util.getOptionalString(o, "size", "100"),
                    Util.getOptionalString(o, "texture_width", textureWidth + ""),
                    Util.getOptionalString(o, "texture_height", textureHeight + ""));
        }

        JsonObject skybox = o.get("skybox").getAsJsonObject();

        if (skybox.has("sides")) {
            ArrayList<SkyBoxSideTexture> textures = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                if (skybox.get("sides").getAsJsonObject().has("all")) {
                    return new SkyBoxObjectProperties(
                            createSingleTextureSkybox(
                                    new ResourceLocationWrapper(Util.getOptionalString(skybox.get("sides").getAsJsonObject().getAsJsonObject("all"), "texture", texture)),
                                    Util.getOptionalString(skybox.get("sides").getAsJsonObject().getAsJsonObject("all"), "uv_x", "0"),
                                    Util.getOptionalString(skybox.get("sides").getAsJsonObject().getAsJsonObject("all"), "uv_y", "0"),
                                    Util.getOptionalString(skybox.get("sides").getAsJsonObject().getAsJsonObject("all"), "uv_width", "0"),
                                    Util.getOptionalString(skybox.get("sides").getAsJsonObject().getAsJsonObject("all"), "uv_height", "0")
                            ),
                            Util.getOptionalString(skybox, "size", "100"),
                            Util.getOptionalString(skybox, "texture_width", textureWidth + ""),
                            Util.getOptionalString(skybox, "texture_height", textureHeight + "")
                    );
                }
                else if (!skybox.get("sides").getAsJsonObject().has(String.valueOf(i))) {
                    textures.add(new SkyBoxSideTexture(
                            new ResourceLocationWrapper(texture), "-1", "-1", "-1", "-1"
                    ));
                }
                else {
                    JsonObject entry = skybox.get("sides").getAsJsonObject().getAsJsonObject(String.valueOf(i));
                    textures.add(new SkyBoxSideTexture(
                            new ResourceLocationWrapper(Util.getOptionalString(entry, "texture", texture)),
                            Util.getOptionalString(entry, "uv_x", "0"),
                            Util.getOptionalString(entry, "uv_y", "0"),
                            Util.getOptionalString(entry, "uv_width", "0"),
                            Util.getOptionalString(entry, "uv_height", "0")
                    ));
                }
            }
            // Returns skybox with custom format
            return new SkyBoxObjectProperties(
                    textures,
                    Util.getOptionalString(skybox, "size", "100"),
                    Util.getOptionalString(skybox, "texture_width", textureWidth + ""),
                    Util.getOptionalString(skybox, "texture_height", textureHeight + "")
            );
        }
        else {
            // Returns default format skybox
            return new SkyBoxObjectProperties(
                    createDefaultSkybox(
                            new ResourceLocationWrapper(texture), Util.getOptionalString(skybox, "uv_size", (textureHeight / 2) + "")
                    ),
                    Util.getOptionalString(skybox, "size", "100"),
                    Util.getOptionalString(skybox, "texture_width", textureWidth + ""),
                    Util.getOptionalString(skybox, "texture_height", textureHeight + "")
            );
        }
    }

    public static ArrayList<SkyBoxSideTexture> createDefaultSkybox(ResourceLocationWrapper texture, String textureSize) {
        ArrayList<SkyBoxSideTexture> textures = new ArrayList<>();

        // Bottom
        // #Green
        textures.add(new SkyBoxSideTexture(texture, textureSize, "0", textureSize, textureSize));

        // North
        // #Yellow
        textures.add(new SkyBoxSideTexture(texture, textureSize + " * 2", "0", textureSize, textureSize));

        // South
        // #Light Blue
        textures.add(new SkyBoxSideTexture(texture, textureSize, textureSize, textureSize, textureSize));


        // Up
        // #Red
        textures.add(new SkyBoxSideTexture(texture, "0", "0", textureSize, textureSize));

        // East
        // #Blue
        textures.add(new SkyBoxSideTexture(texture, "0", textureSize, textureSize, textureSize));

        // West
        // #Purple
        textures.add(new SkyBoxSideTexture(texture, textureSize + " * 2", textureSize, textureSize, textureSize));

        return textures;
    }

    public static ArrayList<SkyBoxSideTexture> createSingleTextureSkybox(ResourceLocationWrapper texture, String uvX, String uvY, String uvSizeX, String uvSizeY) {
        ArrayList<SkyBoxSideTexture> textures = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            textures.add(new SkyBoxSideTexture(texture, uvX, uvY, uvSizeX, uvSizeY));
        }

        return textures;
    }

    public static class SkyBoxSideTexture {
        public ResourceLocationWrapper texture;
        public Expression uvX;
        public Expression uvY;
        public Expression uvSizeX;
        public Expression uvSizeY;

        public SkyBoxSideTexture(ResourceLocationWrapper texture, String uvX, String uvY, String uvSizeX, String uvSizeY) {
            this.texture = texture;
            this.uvX = Util.compileExpression(uvX);
            this.uvY = Util.compileExpression(uvY);
            this.uvSizeX = Util.compileExpression(uvSizeX);
            this.uvSizeY = Util.compileExpression(uvSizeY);
        }
    }
}