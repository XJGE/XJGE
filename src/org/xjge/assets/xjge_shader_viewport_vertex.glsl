#version 330 core

uniform mat4 uProjection;

out vec2 ioTexCoords;

void main() {
    ioTexCoords = aTexCoords;
    gl_Position = uProjection * vec4(aPosition, 1);
}