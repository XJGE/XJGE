#version 330 core

in vec2 ioTexCoords;
in vec3 ioColor;

uniform sampler2D uTexture;

out vec4 ioResult;

void main() {
    ioResult = vec4(texture(uTexture, ioTexCoords).xyz * ioColor, 1);
}