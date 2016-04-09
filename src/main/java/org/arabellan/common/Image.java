package org.arabellan.common;

import lombok.Value;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

@Value
public class Image {

    private final int width;
    private final int height;
    private final ByteBuffer pixels;

    public Image(String filename) {
        Path path = Paths.get(filename);
        ByteBuffer data = resourceToByteBuffer(path);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer components = BufferUtils.createIntBuffer(1);

        int result = STBImage.stbi_info_from_memory(data, width, height, components);
        if (result == 0)
            throw new RuntimeException("Cannot read image info: " + STBImage.stbi_failure_reason());

        this.width = width.get(0);
        this.height = height.get(0);

        pixels = STBImage.stbi_load_from_memory(data, width, height, components, 0);
        if (pixels == null)
            throw new RuntimeException("Cannot read image data: " + STBImage.stbi_failure_reason());
    }

    private ByteBuffer resourceToByteBuffer(Path path) {
        try {
            FileInputStream stream = new FileInputStream(path.toFile());
            FileChannel channel = stream.getChannel();

            int size = (int) channel.size() + 1;
            ByteBuffer buffer = BufferUtils.createByteBuffer(size);

            channel.read(buffer);

            channel.close();
            stream.close();
            buffer.flip();

            return buffer;
        } catch (Exception e) {
            throw new RuntimeException("Cannot create a ByteBuffer from: " + path, e);
        }
    }
}
