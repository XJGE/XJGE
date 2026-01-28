#version 330 core

in vec3 ioTexCoords;

uniform samplerCube uSkyTexture;

layout (location = 0) out vec4 ioFragColor;

void main() {
    if(texture(uSkyTexture, ioTexCoords).a == 0) discard;
    ioFragColor = texture(uSkyTexture, ioTexCoords);
}