#version 330 core

in vec3 ioTexCoords;

layout (location = 0) out vec4 ioFragColor;
layout (location = 1) out vec4 ioBrightColor;

uniform samplerCube uSkyTexture;
uniform float uBloomThreshold;

void main() {
    if(texture(uSkyTexture, ioTexCoords).a == 0) discard;
    ioFragColor = texture(uSkyTexture, vec3(-ioTexCoords.x, ioTexCoords.yz));

    float brightness = dot(ioFragColor.rgb, vec3(0.2126, 0.7152, 0.0722));
    ioBrightColor    = (brightness > uBloomThreshold) ? vec4(ioFragColor.rgb, 1) : vec4(0, 0, 0, 1);
}