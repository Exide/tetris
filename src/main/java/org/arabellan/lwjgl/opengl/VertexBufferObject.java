package org.arabellan.lwjgl.opengl;

import lombok.Value;

import java.nio.ByteBuffer;

import static org.arabellan.lwjgl.GLException.throwIfError;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;

/**
 * A Vertex Buffer Object (VBO) is a memory buffer in the high speed memory of
 * your video card designed to hold information about vertices.
 * <p>
 * VBOs can also store information such as normals, texcoords, indices, etc.
 */

@Value
public class VertexBufferObject {

    private final int id;
    private final Type type;
    private final int dimensions;
    private final ByteBuffer data;

    public static Builder builder() {
        return new Builder();
    }

    public int getVertexCount() {
        return data.capacity() / Float.BYTES / dimensions;
    }

    public enum Type {VERTICES, INDICES, COLORS, TEXCOORDS, NORMALS}

    public static class Builder {

        private int id;
        private Type type;
        private int dimensions;
        private ByteBuffer data;

        public Builder() {
            id = glGenBuffers();
            throwIfError();
        }

        public Builder data(ByteBuffer data) {
            addToVideoMemory(data);
            this.data = data;
            return this;
        }

        private void addToVideoMemory(ByteBuffer buffer) {
            glBindBuffer(GL_ARRAY_BUFFER, id);
            throwIfError();

            glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
            throwIfError();

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            throwIfError();
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder dimensions(int dimensions) {
            this.dimensions = dimensions;
            return this;
        }

        public VertexBufferObject build() {
            return new VertexBufferObject(id, type, dimensions, data);
        }
    }
}
