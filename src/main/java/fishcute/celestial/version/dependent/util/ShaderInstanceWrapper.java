package fishcute.celestial.version.dependent.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.ShaderInstance;

public class ShaderInstanceWrapper {
    public ShaderInstance shaderInstance;
    public ShaderInstanceWrapper() {
        this.shaderInstance = RenderSystem.getShader();
    }
}
