#version 330 core

in vec2 ioTexCoords;
in vec3 ioColor;

uniform sampler2D uTexture;

out vec4 ioFragColor;

void main() {
    vec4 texColor = texture(uTexture, ioTexCoords);
    if(texColor.a == 0) discard;
    
    ioFragColor = vec4(ioColor, 1.0) * texColor;
}