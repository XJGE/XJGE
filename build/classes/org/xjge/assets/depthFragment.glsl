#version 330 core

in vec2 ioTexCoords;
in vec4 ioFragPos;

uniform sampler2D uTexture;

out vec4 ioResult;

void main() {
    if(texture(uTexture, ioTexCoords).a == 0) discard;
    ioResult = vec4(1.0);
}