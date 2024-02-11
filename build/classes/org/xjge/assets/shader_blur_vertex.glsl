#version 330 core

layout (location = 0) in vec3 aPosition;
layout (location = 2) in vec2 aTexCoords;

uniform mat4 uProjection;

out vec2 ioTexCoords;

void main() {
    ioTexCoords = aTexCoords;
    gl_Position = uProjection * vec4(aPosition, 1);
}