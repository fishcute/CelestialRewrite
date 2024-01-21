package fishcute.celestial.version.dependent;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import fishcute.celestial.version.dependent.util.ResourceLocationWrapper;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.level.biome.Biome;

public class VRenderSystem {
    public static void setShaderFogStart(float start) {
        RenderSystem.setShaderFogStart(start);
    }
    public static void setShaderFogEnd(float end) {
        RenderSystem.setShaderFogEnd(end);
    }
    public static int getBiomeFogColor(Biome biome) {
        return biome.getSpecialEffects().getFogColor();
    }
    public static void levelFogColor() {
        FogRenderer.levelFogColor();
    }
    public static void setupNoFog() {
        FogRenderer.setupNoFog();
    }
    public static void defaultBlendFunc() {
        RenderSystem.defaultBlendFunc();
    }

    public static void depthMask(boolean enable) {
        RenderSystem.depthMask(enable);
    }
    public static void setShaderColor(float f, float g, float h, float a) {
        RenderSystem.setShaderColor(f, g, h, a);
    }
    public static void clearColor(float f, float g, float h, float a) {
        RenderSystem.clearColor(f, g, h, a);
    }
    public static void unbindVertexBuffer() {
        VertexBuffer.unbind();
    }
    public static void toggleBlend(boolean enable) {
        if (enable)
            RenderSystem.enableBlend();
        else
            RenderSystem.disableBlend();
    }
    public static void defaultBlendFunction() {
        RenderSystem.defaultBlendFunc();
    }
    public static void setShaderPositionColor() {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
    }
    public static void setShaderPositionTex() {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
    }

    public static void toggleTexture(boolean texture) {
        if (texture)
            RenderSystem.enableTexture();
        else
            RenderSystem.disableTexture();
    }
    public static void blendFuncSeparate() {
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    }
    public static void setShaderTexture(int i, ResourceLocationWrapper j) {
        RenderSystem.setShaderTexture(i, j.resourceLocation);
    }
}
