package org.arabellan.lwjgl;

import lombok.Value;

import java.util.HashMap;
import java.util.Map;

import static org.arabellan.lwjgl.GLException.throwIfError;
import static org.arabellan.lwjgl.VertexBufferObject.Type;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * A Vertex Array Object (VAO) is an object which contains one or more
 * {@link VertexBufferObject}s and is designed to store the information for a complete
 * rendered object.
 */

@Value
public class VertexArrayObject {

    private final int id;
    private final Map<Type, VertexBufferObject> buffers;

    public static Builder builder() {
        return new Builder();
    }

    public VertexBufferObject getBuffer(Type type) {
        return buffers.get(type);
    }

    public static class Builder {

        private int id;
        private Map<Type, VertexBufferObject> buffers = new HashMap<>();

        public Builder() {
            id = glGenVertexArrays();
            throwIfError();
        }

        public Builder buffer(VertexBufferObject buffer) {
            addToVideoMemory(buffer);
            buffers.put(buffer.getType(), buffer);
            return this;
        }

        private void addToVideoMemory(VertexBufferObject buffer) {
            glBindVertexArray(id);
            throwIfError();

            // use these vertices to draw our object
            glBindBuffer(GL_ARRAY_BUFFER, buffer.getId());
            throwIfError();

            // read the vertices in this way
            glVertexAttribPointer(0, buffer.getDimensions(), GL_FLOAT, false, 0, 0);
            throwIfError();
        }

        public VertexArrayObject build() {
            return new VertexArrayObject(id, buffers);
        }
    }
}
