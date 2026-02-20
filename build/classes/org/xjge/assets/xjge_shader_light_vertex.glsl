#version 330 core

//Regular attributes
layout(location = 0) in vec2 aPosition;
layout(location = 1) in vec2 aTexCoords;

//Instanced attributes
layout(location = 3) in vec3 iPosition;
layout(location = 4) in vec2 iTexCoords;
layout(location = 5) in vec3 iColor;

uniform mat4 uView;
uniform mat4 uProjection;
uniform float uIconScale;

out vec2 ioTexCoords;
out vec3 ioColor;

void main() {
    vec3 worldPos = vec3(aPosition * uIconScale, 0.0) + iPosition;
    gl_Position   = uProjection * uView * vec4(worldPos, 1.0);
    ioTexCoords   = aTexCoords + iTexCoords;
    ioColor       = iColor;
}