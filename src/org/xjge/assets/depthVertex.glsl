#version 330 core

layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec2 aTexCoords;

uniform vec2 uTexCoords;
uniform mat4 uModel;
uniform mat4 uLightSpace;

out vec2 ioTexCoords;

void main() {
    ioTexCoords = aTexCoords + uTexCoords;
    gl_Position = uLightSpace * uModel * vec4(aPosition, 1);
}