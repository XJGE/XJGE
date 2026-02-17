#version 330 core

layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec3 aNormal;

uniform mat3 uNormal;
uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProjection;

out vec3 ioNormal;

void main() {
    ioNormal    = uNormal * aNormal;
    gl_Position = uProjection * uView * uModel * vec4(aPosition, 1);
}