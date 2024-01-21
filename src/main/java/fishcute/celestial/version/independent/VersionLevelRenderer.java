package fishcute.celestial.version.independent;


import com.mojang.blaze3d.systems.RenderSystem;
import fishcute.celestial.sky.*;
import fishcute.celestial.util.Util;
import fishcute.celestial.version.dependent.*;
import fishcute.celestial.version.dependent.util.*;

import java.util.ArrayList;
import java.util.HashMap;

public class VersionLevelRenderer {
    private static HashMap<String, Util.DynamicValue> replaceMap = new HashMap<>();

    private static Matrix4fWrapper setRotation(PoseStackWrapper matrices, float i, float j, float k, float x, float y, float z) {
        matrices.pushPose();

        if (i != 0) {
            matrices.mulPose(PoseStackWrapper.Axis.X, i);
        }
        if (j != 0) {
            matrices.mulPose(PoseStackWrapper.Axis.Y, j);
        }
        if (k != 0) {
            matrices.mulPose(PoseStackWrapper.Axis.Z, k);
        }

        matrices.translate(x, y, z);

        Matrix4fWrapper matrix4f = matrices.lastPose();
        matrices.popPose();

        return matrix4f;
    }

    private static void setupReplaceMap() {
        replaceMap = Util.getReplaceMapNormal();
        Util.DynamicValue v = new Util.DynamicValue() {
            @Override
            public double getValue() {
                return 0;
            }
        };

        replaceMap.put("#populateDegreesX", v);
        replaceMap.put("#populateDegreesY", v);
        replaceMap.put("#populateDegreesZ", v);
        replaceMap.put("#populatePosX", v);
        replaceMap.put("#populatePosY", v);
        replaceMap.put("#populatePosZ", v);
        replaceMap.put("#populateDistance", v);
        replaceMap.put("#populateScale", v);
        replaceMap.put("#populateId", v);
    }

    private static final Util.MutableDynamicValue a1 = new Util.MutableDynamicValue();
    private static final Util.MutableDynamicValue b1 = new Util.MutableDynamicValue();
    private static final Util.MutableDynamicValue c1 = new Util.MutableDynamicValue();
    private static final Util.MutableDynamicValue d1 = new Util.MutableDynamicValue();
    private static final Util.MutableDynamicValue e1 = new Util.MutableDynamicValue();
    private static final Util.MutableDynamicValue f1 = new Util.MutableDynamicValue();
    private static final Util.MutableDynamicValue g1 = new Util.MutableDynamicValue();
    private static final Util.MutableDynamicValue h1 = new Util.MutableDynamicValue();
    private static final Util.MutableDynamicValue i1 = new Util.MutableDynamicValue();

    public static void renderTwilight(ShaderInstanceWrapper shader, BufferBuilderWrapper bufferBuilder, float tickDelta, Matrix4fWrapper projectionMatrix, PoseStackWrapper matrices, VertexBufferWrapper skyBuffer, LevelWrapper level) {
        VRenderSystem.unbindVertexBuffer();
        VRenderSystem.toggleBlend(true);
        VRenderSystem.defaultBlendFunction();

        skyBuffer.bind();
        skyBuffer.drawWithShader(matrices.lastPose(), projectionMatrix, shader);
        float[] fs = level.getSunriseColor(tickDelta);
        if (fs != null) {
            VRenderSystem.setShaderPositionColor();
            VRenderSystem.toggleTexture(false);
            VRenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            matrices.pushPose();
            matrices.mulPose(PoseStackWrapper.Axis.X, 90.0F);
            float f3 = Math.sin(level.getSunAngle(tickDelta)) < 0.0F ? 180.0F : 0.0F;
            matrices.mulPose(PoseStackWrapper.Axis.Z, f3);
            matrices.mulPose(PoseStackWrapper.Axis.Z, 90.0F);
            float j = fs[0];
            float k = fs[1];
            float l = fs[2];
            Matrix4fWrapper matrix4f = matrices.lastPose();
            bufferBuilder.beginSky();
            bufferBuilder.vertex(matrix4f, 0.0F, 100.0F, 0.0F, j, k, l, fs[3]);

            for (int n = 0; n <= 16; ++n) {
                float o = (float) n * 6.2831855F / 16.0F;
                float p = VMth.sin(o);
                float q = VMth.cos(o);
                bufferBuilder.vertex(matrix4f, p * 120.0F, q * 120.0F, -q * 40.0F * fs[3], fs[0], fs[1], fs[2], 0.0F);
            }

            bufferBuilder.upload();
            matrices.popPose();
        }
    }

    public static void renderLevel(Matrix4fWrapper projectionMatrix, PoseStackWrapper matrices, VertexBufferWrapper skyBuffer, VertexBufferWrapper darkBuffer, CameraWrapper camera, LevelWrapper level, float tickDelta) {
        if (camera.doesFogBlockSky() && !(camera.doesMobEffectBlockSky())) {
            VRenderSystem.toggleTexture(false);
            Vector Vector3d = level.getSkyColor(tickDelta);
            float f = (float) Vector3d.x;
            float g = (float) Vector3d.y;
            float h = (float) Vector3d.z;
            VRenderSystem.levelFogColor();
            BufferBuilderWrapper bufferBuilder = new BufferBuilderWrapper();
            VRenderSystem.depthMask(false);
            VRenderSystem.setShaderColor(f, g, h, 1.0F);

            ShaderInstanceWrapper shader = new ShaderInstanceWrapper();

            renderTwilight(shader, bufferBuilder, tickDelta, projectionMatrix, matrices, skyBuffer, level);

            CelestialRenderInfo renderInfo = CelestialSky.getDimensionRenderInfo();

            VRenderSystem.toggleTexture(true);
            VRenderSystem.toggleBlend(true);

            VRenderSystem.blendFuncSeparate();
            int count;

            Matrix4fWrapper rotMatrix = matrices.lastPose();

            for (CelestialObject c : renderInfo.skyObjects) {
                count = 0;

                matrices.pushPose();

                if (c.isPopulation()) {
                    matrices.mulPose(PoseStackWrapper.Axis.X, ((CelestialObjectPopulation) c).baseObject.baseDegreesX.invoke().floatValue());
                    matrices.mulPose(PoseStackWrapper.Axis.Y, ((CelestialObjectPopulation) c).baseObject.baseDegreesY.invoke().floatValue());
                    matrices.mulPose(PoseStackWrapper.Axis.Z, ((CelestialObjectPopulation) c).baseObject.baseDegreesZ.invoke().floatValue());

                    // Checks if replace map has special values yet
                    if (!replaceMap.containsKey("#populateId")) {
                        setupReplaceMap();
                    }

                    Object[] dataArray = getObjectDataArray(((CelestialObjectPopulation) c).baseObject, replaceMap);

                    for (CelestialObject c2 : ((CelestialObjectPopulation) c).population) {
                        if (((CelestialObjectPopulation) c).perObjectCalculation) {
                            a1.value = ((float) dataArray[0] + c2.populateDegreesX);
                            replaceMap.put("#populateDegreesX", a1);
                            b1.value = ((float) dataArray[1] + c2.populateDegreesY);
                            replaceMap.put("#populateDegreesY", b1);
                            c1.value = ((float) dataArray[2] + c2.populateDegreesZ);
                            replaceMap.put("#populateDegreesZ", c1);

                            d1.value = ((float) dataArray[3] + c2.populatePosX);
                            replaceMap.put("#populatePosX", d1);
                            e1.value = ((float) dataArray[4] + c2.populatePosY);
                            replaceMap.put("#populatePosY", e1);
                            f1.value = ((float) dataArray[5] + c2.populatePosZ);
                            replaceMap.put("#populatePosZ", f1);

                            g1.value = ((float) dataArray[7] + c2.populateDistanceAdd);
                            replaceMap.put("#populateDistance", g1);
                            h1.value = ((float) dataArray[8] + c2.populateScaleAdd);
                            replaceMap.put("#populateScale", h1);

                            i1.value = count;
                            replaceMap.put("#populateId", i1);

                            dataArray = getObjectDataArray(((CelestialObjectPopulation) c).baseObject, replaceMap);
                        }
                        rotMatrix = setRotation(matrices, (float) ((float) dataArray[0] + c2.populateDegreesX),
                                (float) ((float) dataArray[1] + c2.populateDegreesY),
                                (float) ((float) dataArray[2] + c2.populateDegreesZ),
                                (float) ((float) dataArray[3] + c2.populatePosX),
                                (float) ((float) dataArray[4] + c2.populatePosY),
                                (float) ((float) dataArray[5] + c2.populatePosZ));

                        renderSkyObject(bufferBuilder, matrices, rotMatrix, ((CelestialObjectPopulation) c).baseObject,
                                (Vector) dataArray[11],
                                (Vector) dataArray[12],
                                (float) dataArray[6],
                                (float) ((float) dataArray[7] + c2.populateDistanceAdd),
                                (float) ((float) dataArray[8] + c2.populateScaleAdd),
                                (int) dataArray[9],
                                (ArrayList<Util.VertexPointValue>) dataArray[10],
                                Util.getReplaceMapNormal()
                        );

                        count++;
                    }
                } else {
                    if (!c.type.equals(CelestialObject.CelestialObjectType.SKYBOX)) {
                        matrices.mulPose(PoseStackWrapper.Axis.X, c.baseDegreesX.invoke().floatValue());
                        matrices.mulPose(PoseStackWrapper.Axis.Y, c.baseDegreesY.invoke().floatValue());
                        matrices.mulPose(PoseStackWrapper.Axis.Z, c.baseDegreesZ.invoke().floatValue());
                    }

                    Object[] dataArray = getObjectDataArray(c);

                    if (!c.type.equals(CelestialObject.CelestialObjectType.SKYBOX))
                        rotMatrix = setRotation(matrices, (float) dataArray[0], (float) dataArray[1], (float) dataArray[2],
                                (float) dataArray[3], (float) dataArray[4], (float) dataArray[5]);

                    renderSkyObject(bufferBuilder, matrices, rotMatrix, c,
                            (Vector) dataArray[11],
                            (Vector) dataArray[12],
                            (float) dataArray[6],
                            (float) dataArray[7],
                            (float) dataArray[8],
                            (int) dataArray[9],
                            (ArrayList<Util.VertexPointValue>) dataArray[10],
                            Util.getReplaceMapNormal()
                    );
                }

                matrices.popPose();
            }

            VRenderSystem.levelFogColor();

            VRenderSystem.toggleBlend(false);
            VRenderSystem.toggleTexture(false);
            VRenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);

            double d = VMinecraftInstance.getPlayerEyePosition().y - level.getHorizonHeight();
            if (d < 0.0) {
                matrices.pushPose();
                matrices.translate(0.0, 12.0 + renderInfo.environment.voidCullingLevel.invoke().floatValue(), 0.0);
                darkBuffer.bind();
                darkBuffer.drawWithShader(matrices.lastPose(), projectionMatrix, shader);
                VRenderSystem.unbindVertexBuffer();
                matrices.popPose();
            }

            if (level.hasGround()) {
                VRenderSystem.setShaderColor(f * 0.2F + 0.04F, g * 0.2F + 0.04F, h * 0.6F + 0.1F, 1.0F);
            } else {
                VRenderSystem.setShaderColor(f, g, h, 1.0F);
            }

            VRenderSystem.toggleTexture(true);
            VRenderSystem.depthMask(true);
        }
    }

    private static Object[] getObjectDataArray(CelestialObject c) {
        return getObjectDataArray(c, Util.getReplaceMapNormal());
    }

    private static Object[] getObjectDataArray(CelestialObject c, HashMap<String, Util.DynamicValue> replaceMap) {

        Object[] dataArray = new Object[13];

        //degrees x 0
        dataArray[0] = c.degreesX.invoke().floatValue();

        //degrees y 1
        dataArray[1] = c.degreesY.invoke().floatValue();

        //degrees z 2
        dataArray[2] = c.degreesZ.invoke().floatValue();

        //pos x 3
        dataArray[3] = c.posX.invoke().floatValue();

        //pos y 4
        dataArray[4] = c.posY.invoke().floatValue();

        //pos z 5
        dataArray[5] = c.posZ.invoke().floatValue();

        //alpha 6
        dataArray[6] = c.celestialObjectProperties.alpha.invoke().floatValue();

        //distance 7
        dataArray[7] = c.distance.invoke().floatValue();

        //scale 8
        dataArray[8] = c.scale.invoke().floatValue();

        //moon phase 9
        dataArray[9] = c.celestialObjectProperties.moonPhase.invoke().intValue();

        ArrayList<Util.VertexPointValue> vertexList = new ArrayList<>();

        if (c.vertexList != null && c.vertexList.size() > 0)
            for (Util.VertexPoint v : c.vertexList)
                vertexList.add(new Util.VertexPointValue(v));

        // vertex list 10
        dataArray[10] = (vertexList);

        // colors 11
        dataArray[11] = (new Vector(
                c.celestialObjectProperties.getRed(),
                c.celestialObjectProperties.getGreen(),
                c.celestialObjectProperties.getBlue()));

        //solid colors 12
        if (c.solidColor != null)
            dataArray[12] = (new Vector(
                    (c.solidColor.storedColor.getRed()) * (((Vector) dataArray[11]).x),
                    (c.solidColor.storedColor.getGreen()) * (((Vector) dataArray[11]).y),
                    (c.solidColor.storedColor.getBlue()) * (((Vector) dataArray[11]).z)));
        else
            dataArray[12] = (null);
        return dataArray;
    }

    private static void renderSkyObject(BufferBuilderWrapper bufferBuilder, PoseStackWrapper matrices, Matrix4fWrapper matrix4f2, CelestialObject c, Vector color, Vector colorsSolid, float alpha, float distancePre, float scalePre, int moonPhase, ArrayList<Util.VertexPointValue> vertexList, HashMap<String, Util.DynamicValue> objectReplaceMap) {
        VRenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        float distance = (float) (distancePre + c.populateDistanceAdd);

        float scale = (float) (scalePre + c.populateScaleAdd);

        VRenderSystem.toggleBlend(true);

        // Set texture
        if (c.texture != null)
            VRenderSystem.setShaderTexture(0, c.texture);

        if (c.celestialObjectProperties.ignoreFog)
            VRenderSystem.setupNoFog();

        else
            VRenderSystem.levelFogColor();

        if (c.celestialObjectProperties.isSolid)
            VRenderSystem.defaultBlendFunc();

        if (c.type.equals(CelestialObject.CelestialObjectType.DEFAULT)) {
            VRenderSystem.setShaderPositionTex();
            VRenderSystem.setShaderColor((float) color.x, (float) color.y, (float) color.z, alpha);

            if (c.celestialObjectProperties.hasMoonPhases) {
                int l = (moonPhase % 4);
                int i1 = (moonPhase / 4 % 2);
                float f13 = l / 4.0F;
                float f14 = i1 / 2.0F;
                float f15 = (l + 1) / 4.0F;
                float f16 = (i1 + 1) / 2.0F;
                bufferBuilder.beginObject();
                bufferBuilder.vertexUv(matrix4f2, -scale, distance, (distance < 0 ? scale : -scale),
                        f15, f16, (float) color.x, (float) color.y, (float) color.z, alpha);
                bufferBuilder.vertexUv(matrix4f2, scale, distance, (distance < 0 ? scale : -scale),
                        f13, f16, (float) color.x, (float) color.y, (float) color.z, alpha);
                bufferBuilder.vertexUv(matrix4f2, scale, distance, (distance < 0 ? -scale : scale),
                        f13, f14, (float) color.x, (float) color.y, (float) color.z, alpha);
                bufferBuilder.vertexUv(matrix4f2, -scale, distance, (distance < 0 ? -scale : scale),
                        f15, f14, (float) color.x, (float) color.y, (float) color.z, alpha);
            } else if (vertexList.size() > 0) {
                bufferBuilder.beginObject();
                for (Util.VertexPointValue v : vertexList) {
                    bufferBuilder.vertexUv(matrix4f2, (float) v.pointX, (float) v.pointY, (float) v.pointZ,
                            (float) v.uvX, (float) v.uvY, (float) color.x, (float) color.y, (float) color.z, alpha);
                }
            } else {
                bufferBuilder.beginObject();
                bufferBuilder.vertexUv(matrix4f2, -scale, distance, (distance < 0 ? scale : -scale),
                        0.0F, 0.0F, (float) color.x, (float) color.y, (float) color.z, alpha);
                bufferBuilder.vertexUv(matrix4f2, scale, distance, (distance < 0 ? scale : -scale),
                        1.0F, 0.0F, (float) color.x, (float) color.y, (float) color.z, alpha);
                bufferBuilder.vertexUv(matrix4f2, scale, distance, (distance < 0 ? -scale : scale),
                        1.0F, 1.0F, (float) color.x, (float) color.y, (float) color.z, alpha);
                bufferBuilder.vertexUv(matrix4f2, -scale, distance, (distance < 0 ? -scale : scale),
                        0.0F, 1.0F, (float) color.x, (float) color.y, (float) color.z, alpha);
            }

            bufferBuilder.upload();
        } else if (c.type.equals(CelestialObject.CelestialObjectType.COLOR)) {
            VRenderSystem.setShaderPositionColor();
            VRenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
            VRenderSystem.toggleTexture(false);

            if (vertexList.size() > 0) {
                bufferBuilder.beginColorObject();

                for (Util.VertexPointValue v : vertexList) {
                    bufferBuilder.vertex(matrix4f2, (float) v.pointX, (float) v.pointY, (float) v.pointZ,
                            (float) (colorsSolid.x / 255.0F), (float) (colorsSolid.y / 255.0F), (float) (colorsSolid.z / 255.0F), alpha);
                }
            } else {
                bufferBuilder.beginColorObject();
                bufferBuilder.vertex(matrix4f2, -scale, distance, (distance < 0 ? scale : -scale),
                        (float) (colorsSolid.x / 255.0F), (float) (colorsSolid.y / 255.0F), (float) (colorsSolid.z / 255.0F), alpha);
                bufferBuilder.vertex(matrix4f2, scale, distance, (distance < 0 ? scale : -scale),
                        (float) (colorsSolid.x / 255.0F), (float) (colorsSolid.y / 255.0F), (float) (colorsSolid.z / 255.0F), alpha);
                bufferBuilder.vertex(matrix4f2, scale, distance, (distance < 0 ? -scale : scale),
                        (float) (colorsSolid.x / 255.0F), (float) (colorsSolid.y / 255.0F), (float) (colorsSolid.z / 255.0F), alpha);
                bufferBuilder.vertex(matrix4f2, -scale, distance, (distance < 0 ? -scale : scale),
                        (float) (colorsSolid.x / 255.0F), (float) (colorsSolid.y / 255.0F), (float) (colorsSolid.z / 255.0F), alpha);
            }

            bufferBuilder.upload();

            VRenderSystem.toggleTexture(true);
        } else if (c.type.equals(CelestialObject.CelestialObjectType.SKYBOX)) {
            matrices.popPose();

            SkyBoxObjectProperties.SkyBoxSideTexture side;
            float size;
            float textureX;
            float textureY;
            float textureScaleX;
            float textureScaleY;

            float uvX;
            float uvY;
            float uvSizeX;
            float uvSizeY;
            float textureSizeX = c.solidColor != null ? 0 : c.skyBoxProperties.textureSizeX.invoke().floatValue();
            float textureSizeY = c.solidColor != null ? 0 : c.skyBoxProperties.textureSizeY.invoke().floatValue();

            for (int l = 0; l < 6; ++l) {
                matrices.pushPose();
                side = c.skyBoxProperties.sides.get(l);
                if (c.solidColor == null) {
                    VRenderSystem.setShaderTexture(0, side.texture);
                    VRenderSystem.setShaderPositionTex();
                } else {
                    VRenderSystem.setShaderPositionColor();
                }
                if (l == 0) {
                    matrices.mulPose(PoseStackWrapper.Axis.Y, 180);
                }
                if (l == 1) {
                    matrices.mulPose(PoseStackWrapper.Axis.X, 90);
                }

                if (l == 2) {
                    matrices.mulPose(PoseStackWrapper.Axis.X, -90);
                    matrices.mulPose(PoseStackWrapper.Axis.Y, 180);
                }

                if (l == 3) {
                    matrices.mulPose(PoseStackWrapper.Axis.X, 180);
                    matrices.mulPose(PoseStackWrapper.Axis.Y, 180);
                }

                if (l == 4) {
                    matrices.mulPose(PoseStackWrapper.Axis.Y, 90);
                    matrices.mulPose(PoseStackWrapper.Axis.Z, -90);
                }

                if (l == 5) {
                    matrices.mulPose(PoseStackWrapper.Axis.Y, -90);
                    matrices.mulPose(PoseStackWrapper.Axis.Z, 90);
                }

                size = c.skyBoxProperties.skyBoxSize.invoke().floatValue();

                Matrix4fWrapper matrix4f3 = matrices.lastPose();

                if (c.solidColor != null) {
                    VRenderSystem.toggleTexture(false);
                    bufferBuilder.beginColorObject();

                    bufferBuilder.vertex(matrix4f3, -size, size, (size < 0 ? size : -size), (float) (colorsSolid.x / 255.0F), (float) (colorsSolid.y / 255.0F), (float) (colorsSolid.z / 255.0F), alpha);
                    bufferBuilder.vertex(matrix4f3, size, size, (size < 0 ? size : -size), (float) (colorsSolid.x / 255.0F), (float) (colorsSolid.y / 255.0F), (float) (colorsSolid.z / 255.0F), alpha);
                    bufferBuilder.vertex(matrix4f3, size, size, (size < 0 ? -size : size), (float) (colorsSolid.x / 255.0F), (float) (colorsSolid.y / 255.0F), (float) (colorsSolid.z / 255.0F), alpha);
                    bufferBuilder.vertex(matrix4f3, -size, size, (size < 0 ? -size : size), (float) (colorsSolid.x / 255.0F), (float) (colorsSolid.y / 255.0F), (float) (colorsSolid.z / 255.0F), alpha);
                    bufferBuilder.upload();

                    VRenderSystem.toggleTexture(true);
                } else {
                    uvX = side.uvX.invoke().floatValue();
                    uvY = side.uvY.invoke().floatValue();
                    uvSizeX = side.uvSizeX.invoke().floatValue();
                    uvSizeY = side.uvSizeY.invoke().floatValue();

                    textureX = (uvX / textureSizeX);
                    textureY = (uvY / textureSizeY);
                    textureScaleX = textureX + (uvSizeX / textureSizeX);
                    textureScaleY = textureY + (uvSizeY / textureSizeY);

                    if (textureX >= 0 && textureY >= 0 && textureScaleX >= 0 && textureScaleY >= 0) {
                        bufferBuilder.beginObject();
                        bufferBuilder.vertexUv(matrix4f3, -size, -size, -size, textureX, textureY, (float) color.x, (float) color.y, (float) color.z, alpha);
                        bufferBuilder.vertexUv(matrix4f3, -size, -size, size, textureX, textureScaleY, (float) color.x, (float) color.y, (float) color.z, alpha);
                        bufferBuilder.vertexUv(matrix4f3, size, -size, size, textureScaleX, textureScaleY, (float) color.x, (float) color.y, (float) color.z, alpha);
                        bufferBuilder.vertexUv(matrix4f3, size, -size, -size, textureScaleX, textureY, (float) color.x, (float) color.y, (float) color.z, alpha);
                        bufferBuilder.upload();
                    }
                }
                matrices.popPose();
            }
            matrices.pushPose();
        }

        if (c.celestialObjectProperties.isSolid)
            VRenderSystem.blendFuncSeparate();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
