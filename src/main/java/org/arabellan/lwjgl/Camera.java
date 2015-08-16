package org.arabellan.lwjgl;

import org.joml.Vector3f;

public class Camera {
    Vector3f position;
    Vector3f focus;
    Vector3f up;

    float left;
    float right;
    float top;
    float bottom;

    float fieldOfView;
    float aspectRatio;
    float nearClip;
    float farClip;

    Camera(int screenWidth, int screenHeight) {
        position = new Vector3f(0, 0, 3);
        focus = new Vector3f(0, 0, 0);
        up = new Vector3f(0, 1, 0);

        int halfWidth = screenWidth / 2;
        int halfHeight = screenHeight / 2;

        left = -halfWidth;
        right = halfWidth;
        top = halfHeight;
        bottom = -halfHeight;

        fieldOfView = 60f;
        aspectRatio = screenWidth / screenHeight;
        nearClip = 1f;
        farClip = 5f;
    }
}
