#version 330

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
in vec2 position;

void main() {
    vec4 vertex = vec4(position, 0, 1);
    gl_Position = projection * view * model * vertex;
}
