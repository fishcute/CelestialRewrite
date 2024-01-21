package fishcute.celestial.version.dependent.util;

import com.mojang.blaze3d.vertex.*;

public class BufferBuilderWrapper {
    public BufferBuilder bufferBuilder;
    public BufferBuilderWrapper() {
        this.bufferBuilder = Tesselator.getInstance().getBuilder();
    }
    public void beginSky() {
        bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
    }
    public void beginObject() {
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
    }
    public void beginColorObject() {
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
    }
    public void vertex(Matrix4fWrapper matrix4f, float x, float y, float z, float r, float g, float b, float a) {
        bufferBuilder.vertex(matrix4f.matrix, x, y, z).color(r, g, b, a).endVertex();
    }
    public void vertexUv(Matrix4fWrapper matrix4f, float x, float y, float z, float u, float v, float r, float g, float b, float a) {
        bufferBuilder.vertex(matrix4f.matrix, x, y, z).uv(u, v).color(r, g, b, a).endVertex();
    }

    public void upload() {
        BufferUploader.drawWithShader(bufferBuilder.end());
    }
}
