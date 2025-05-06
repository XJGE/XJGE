#version 330 core

layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec2 aTexCoords;
layout (location = 2) in vec3 aPosOffset;
layout (location = 3) in vec2 aTexOffset;
layout (location = 4) in vec4 aColOffset;

uniform int  uType;
uniform mat4 uProjection;

out vec2 ioTexCoords;
out vec4 ioColor;

void main() {
    switch(uType) {
        case 0: //Used for rendering fonts
            ioTexCoords = aTexCoords + aTexOffset;
            ioColor     = aColOffset;
            gl_Position = uProjection * vec4(aPosition + aPosOffset, 1);
            break;
    }
}