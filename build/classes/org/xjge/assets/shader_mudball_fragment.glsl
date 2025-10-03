#version 330 core

uniform vec3 uColor;
uniform sampler2D uTexture;

in vec2 ioTexCoords;

void main() {
    gl_FragColor = vec4(texture(uTexture, ioTexCoords).xyz * uColor, texture(uTexture, ioTexCoords).a);
}