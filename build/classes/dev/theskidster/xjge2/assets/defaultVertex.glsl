#version 330 core

layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec3 aColor;

uniform int uType;
uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProjection;

void main() {
    switch(uType) {
        case 1:
            ioColor     = aColor;
            gl_Position = uProjection * uView * uModel * vec4(aPosition, 1);
            break;
    }
}