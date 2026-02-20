#version 330 core

//Regular attributes
layout(location = 0) in vec2 aPosition;
layout(location = 1) in vec2 aTexCoords;

//Instanced attributes
layout(location = 2) in vec3  iPosition;
layout(location = 3) in vec2  iTexCoords;
layout(location = 4) in vec3  iColor;
layout(location = 5) in float iScale;

uniform mat4 uView;
uniform mat4 uProjection;

out vec2 ioTexCoords;
out vec3 ioColor;

void main() {
    //Obtain right and up vectors from view matrix for billboarding effect
    vec3 camRight = vec3(uView[0][0], uView[1][0], uView[2][0]);
    vec3 camUp    = vec3(uView[0][1], uView[1][1], uView[2][1]);
    vec3 worldPos = iPosition + (aPosition.x * camRight + aPosition.y * camUp) * iScale;

    gl_Position = uProjection * uView * vec4(worldPos, 1.0);
    ioTexCoords = aTexCoords + iTexCoords;
    ioColor     = iColor;
}