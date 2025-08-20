#version 330 core

layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec3 aColor;
layout (location = 2) in vec2 aTexCoords;
layout (location = 3) in vec3 aPositionOffset;
layout (location = 4) in vec3 aColorOffset;

uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProjection;

out vec2 ioTexCoords;
out vec3 ioColor;

void main() {
    ioTexCoords = aTexCoords;
    ioColor     = aColorOffset;
    gl_Position = uProjection * uView * uModel * vec4(aPosition + aPositionOffset, 1);
}