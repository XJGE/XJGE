#version 330 core

layout(location = 1) out vec4 ioBrightColor;

in vec2 ioTexCoords;
in vec3 ioColor;

uniform sampler2D uTexture;

out vec4 ioFragColor;

void main() {
    vec4 texColor = texture(uTexture, ioTexCoords);
    if(texColor.a == 0) discard;
    
    if(texColor.g == 1f && texColor.b == 0f) {
        ioFragColor = vec4(ioColor, 1f);
    } else {
        ioFragColor = texColor;
    }

    ioBrightColor = vec4(0);
    
}