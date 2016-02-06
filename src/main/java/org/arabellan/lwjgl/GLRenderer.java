package org.arabellan.lwjgl;

import lombok.extern.slf4j.Slf4j;
import org.arabellan.tetris.Renderable;
import org.arabellan.tetris.domain.BlockMatrix;
import org.arabellan.tetris.scenes.Scene;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;

import static org.arabellan.lwjgl.GLException.throwIfError;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.stb.STBEasyFont.stb_easy_font_print;

@Slf4j
public class GLRenderer {

    private final int BYTES_PER_CHARACTER = 272;
    private int textVAO;
    private int textTriangleCount;
    // TODO: move all this shit into a Block class maybe?
    private int blockVAO;
    private int blockSize = 20;
    private ShaderProgram shader;
    private Camera camera;

    public void initialize(int width, int height) {
        createGLContext();
        initializeGLState();
        createBlockVAO(getBlockVertexData());
        createTextVAO("test");
        shader = createDefaultShader();
        camera = new Camera(width, height);
    }

    private float[] getBlockVertexData() {
        float p = 1f * (blockSize / 2);
        return new float[]{
                -p, -p, // A    E--D
                -p, +p, // B     \ |    counter-clockwise
                +p, -p, // C      'F
                +p, +p, // D    B.
                -p, +p, // E    | \     clockwise
                +p, -p, // F    A--C
        };
    }

    private void createBlockVAO(float[] vertices) {
        ByteBuffer blockBuffer = BufferUtils.createByteBuffer(vertices.length * Float.BYTES);
        blockBuffer.asFloatBuffer().put(vertices).flip();
        blockVAO = loadMesh(blockBuffer);
    }

    private void createTextVAO(String text) {
        ByteBuffer charBuffer = BufferUtils.createByteBuffer(text.length() * BYTES_PER_CHARACTER);
        // More info: https://github.com/nothings/stb/blob/master/stb_easy_font.h#L24-L55
        int textQuadCount = stb_easy_font_print(0, 0, text, null, charBuffer);
        textTriangleCount = textQuadCount * 2;
        ByteBuffer vertexBuffer = getTriangles(charBuffer);
        textVAO = loadMesh(vertexBuffer);
    }

    private void createGLContext() {
        GL.createCapabilities();
        throwIfError();

        log.info("GL version: " + glGetString(GL_VERSION));
        throwIfError();

        log.info("GLSL version: " + glGetString(GL_SHADING_LANGUAGE_VERSION));
        throwIfError();
    }

    private void initializeGLState() {
        glClearColor(0, 0, 0, 0);
        throwIfError();
    }

    private int loadMesh(ByteBuffer buffer) {
        int vbo = createVBO(buffer);
        return createVAO(vbo);
    }

    private int createVBO(ByteBuffer buffer) {
        int id = glGenBuffers();
        throwIfError();

        glBindBuffer(GL_ARRAY_BUFFER, id);
        throwIfError();

        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        throwIfError();

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        throwIfError();

        return id;
    }

    private int createVAO(int vbo) {
        int id = glGenVertexArrays();
        throwIfError();

        glBindVertexArray(id);
        throwIfError();

        // use these vertices to draw our object
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        throwIfError();

        // read the vertices in this way
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        throwIfError();

        return id;
    }

    private ShaderProgram createDefaultShader() {
        Shader vertex = new Shader("shaders/vertex.glsl", GL_VERTEX_SHADER);
        Shader fragment = new Shader("shaders/fragment.glsl", GL_FRAGMENT_SHADER);
        return new ShaderProgram(Arrays.asList(vertex, fragment));
    }

    public void draw(Scene scene) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        throwIfError();

        if (scene.getRenderables().isEmpty()) return;

        shader.enable();
        shader.enableAttribute("vertex");
        shader.setUniform("view", getViewMatrix(camera));
        shader.setUniform("projection", getProjectionMatrix(camera));
        scene.getRenderables().forEach(this::drawRenderable);
        renderText(new Vector3f());
        shader.disableAttribute("vertex");
        shader.disable();
    }

    private void drawRenderable(Renderable renderable) {
        Vector2f renderablePosition = getRenderableScreenPosition(renderable.getPosition(), renderable.getMatrix());
        renderable.getMatrix().forEach((matrixCoord, block) -> {
            if (block == 1) {
                Vector3f blockPosition = getBlockScreenPosition(renderablePosition, matrixCoord);
                renderBlock(blockPosition);
            }
        });
    }

    private void renderBlock(Vector3f position) {
        shader.setUniform("model", getModelMatrix(position));

        glBindVertexArray(blockVAO);
        throwIfError();

        glDrawArrays(GL_TRIANGLES, 0, 6);
        throwIfError();
    }

    private void renderText(Vector3f position) {
        // TODO: make this not broken
        shader.setUniform("model", getModelMatrix(position));

        glBindVertexArray(textVAO);
        throwIfError();

        glDrawArrays(GL_TRIANGLES, 0, textTriangleCount);
        throwIfError();
    }

    private ByteBuffer getTriangles(ByteBuffer input) {
        // input assumed to be interleaved array:
        // x:float, y:float, z:float, color:uint8[4]

        int vertexBufferSize = (input.capacity() / 4) * 3;
        ByteBuffer output = BufferUtils.createByteBuffer(vertexBufferSize);

        while (input.hasRemaining()) {
            byte[] vertices = getVertices(input);
            byte[] color = getColor(input);
            output.put(vertices);
        }

        output.flip();
        return output;
    }

    private byte[] getVertices(ByteBuffer buffer) {
        int size = Float.BYTES;
        byte[] x = new byte[size];
        byte[] y = new byte[size];
        byte[] z = new byte[size];
        buffer.get(x, 0, size).get(y, 0, size).get(z, 0, size);
        byte[] vertices = new byte[size * 3];
        System.arraycopy(x, 0, vertices, 0, size);
        System.arraycopy(y, 0, vertices, size, size);
        System.arraycopy(z, 0, vertices, size * 2, size);
        return vertices;
    }

    private byte[] getColor(ByteBuffer buffer) {
        byte[] r = new byte[1];
        byte[] g = new byte[1];
        byte[] b = new byte[1];
        byte[] a = new byte[1];
        buffer.get(r).get(g).get(b).get(a);
        byte[] color = new byte[4];
        System.arraycopy(r, 0, color, 0, 1);
        System.arraycopy(g, 0, color, 1, 1);
        System.arraycopy(b, 0, color, 2, 1);
        System.arraycopy(a, 0, color, 3, 1);
        return color;
    }

    private Vector2f getRenderableScreenPosition(Vector2f position, BlockMatrix matrix) {
        float x = position.x - ((float) matrix.width() / 2);
        float y = position.y + ((float) matrix.height() / 2);
        return new Vector2f(x, y);
    }

    private Vector3f getBlockScreenPosition(Vector2f position, Vector2i matrixCoord) {
        float x = (position.x + matrixCoord.x) * blockSize;
        float y = (position.y - matrixCoord.y) * blockSize;
        return new Vector3f(x, y, 0);
    }

    private FloatBuffer getModelMatrix(Vector3f position) {
        Matrix4f model = new Matrix4f().translate(position);
        return matrixAsBuffer(model);
    }

    private FloatBuffer getViewMatrix(Camera camera) {
        Matrix4f view = new Matrix4f().lookAt(camera.position, camera.focus, camera.up);
        return matrixAsBuffer(view);
    }

    private FloatBuffer getProjectionMatrix(Camera camera) {
        Matrix4f projection = new Matrix4f().setOrtho(camera.left, camera.right, camera.bottom, camera.top, camera.nearClip, camera.farClip);
        return matrixAsBuffer(projection);
    }

    private FloatBuffer matrixAsBuffer(Matrix4f matrix) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        matrix.get(buffer);
        return buffer;
    }
}
