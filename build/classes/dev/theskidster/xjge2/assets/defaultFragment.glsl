#version 330 core

in vec3 ioColor;

uniform int uType;

out vec4 ioResult;

void main() {
    switch(uType) {
        case 1:
            ioResult = vec4(ioColor, 0);
            break;
    }
}