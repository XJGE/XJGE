#version 330 core

uniform vec3 uColor;

out vec4 ioFragColor;

void main() {
    ioFragColor = vec4(uColor, 1.0f);
}