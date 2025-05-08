#version 330 core

layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec2 aTexCoords;
layout (location = 2) in vec3 aPosOffset;
layout (location = 3) in vec2 aTexOffset;
layout (location = 4) in vec4 aColOffset;

uniform int   uType;
uniform float uOpacity;
uniform vec2  uTexCoords;
uniform vec3  uColor;
uniform mat4  uModel;
uniform mat4  uProjection;

out vec2 ioTexCoords;
out vec4 ioColorRGBA;

void main() {
    switch(uType) {
        case 0: //Used for rendering fonts
            ioTexCoords = aTexCoords + aTexOffset;
            ioColorRGBA = aColOffset;
            gl_Position = uProjection * vec4(aPosition + aPosOffset, 1);
            break;

        case 1: //Used for rendering rectangles
            ioColorRGBA = aColOffset;
            gl_Position = uProjection * vec4(aPosition, 1);
            break;

        case 3: //Used for rendering icons
            ioTexCoords = aTexCoords + uTexCoords;
            ioColorRGBA = vec4(uColor, uOpacity);
            gl_Position = uProjection * uModel * vec4(aPosition, 1);
            break;
    }
}