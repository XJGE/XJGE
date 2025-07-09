#version 330 core

in vec2 ioTexCoords;

uniform sampler2D uTexture;

void main() {
    gl_FragColor = texture(uTexture, ioTexCoords);
}