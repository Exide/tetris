#version 400 core

in vec2 uv;
uniform sampler2D image;
uniform vec4 color;
out vec4 outputColor;

void main() {
    outputColor = texture(image, uv) * color;
}
