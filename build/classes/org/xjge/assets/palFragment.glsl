#version 330 core

in vec2 ioTexCoords;

uniform sampler2D uTexture;

out vec4 ioFragColor;

void main() {
    ioFragColor = texture(uTexture, ioTexCoords);
}