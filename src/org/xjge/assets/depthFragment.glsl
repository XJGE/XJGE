#version 330 core

in vec2 ioTexCoords;

uniform sampler2D uTexture;

out vec4 ioResult;

void makeTransparent(float a) {
    if(a == 0) discard;
}

void main() {
    makeTransparent(texture(uTexture, ioTexCoords).a);
    ioResult = texture(uTexture, ioTexCoords);
}