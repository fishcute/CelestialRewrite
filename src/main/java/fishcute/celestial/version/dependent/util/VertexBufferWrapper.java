package fishcute.celestial.version.dependent.util;

import com.mojang.blaze3d.vertex.VertexBuffer;

public class VertexBufferWrapper {
    public VertexBuffer buffer;
    public VertexBufferWrapper(VertexBuffer buffer) {
        this.buffer = buffer;
    }
    public void bind() {
        buffer.bind();
    }
    public void drawWithShader(Matrix4fWrapper matrix, Matrix4fWrapper projectionMatrix, ShaderInstanceWrapper shader) {
        buffer.drawWithShader(matrix.matrix, projectionMatrix.matrix, shader.shaderInstance);
    }
}
