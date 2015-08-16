package org.arabellan.lwjgl;

import org.joml.Vector3f;

public class Camera {
    Vector3f position;
    Vector3f focus;
    Vector3f up;

    float fieldOfView;
    float aspectRatio;
    float nearClip;
    float farClip;

    Camera(int screenWidth, int screenHeight) {
        position = new Vector3f(0, 0, 6);
        focus = new Vector3f(0, 0, 0);
        up = new Vector3f(0, 1, 0);

        fieldOfView = 90f;
//        aspectRatio = 1.6f; // 16:10
//        aspectRatio = screenWidth / screenHeight;
        aspectRatio = 1.0f;
        nearClip = 1f;
        farClip = 10f;
    }
}
