package org.arabellan.lwjgl;

import java.nio.FloatBuffer;

import static org.arabellan.lwjgl.GLException.throwIfError;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;

public class VertexBufferObject {
    int id;

    VertexBufferObject() {
        id = glGenBuffers();
        throwIfError();
    }

    void uploadMesh(FloatBuffer mesh) {
        glBindBuffer(GL_ARRAY_BUFFER, id);
        throwIfError();

        glBufferData(GL_ARRAY_BUFFER, mesh, GL_STATIC_DRAW);
        throwIfError();

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        throwIfError();
    }
}
