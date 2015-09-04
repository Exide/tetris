#version 330

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
in vec2 vertex;

void main() {
    gl_Position = projection * view * model * vec4(vertex, 0, 1);
}
