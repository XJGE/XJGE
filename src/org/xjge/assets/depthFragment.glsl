#version 330 core

in vec2 ioTexCoords;

uniform sampler2D uTexture;

out vec4 ioResult;

void main() {
    float alpha = texture(uTexture, ioTexCoords).a;
    
    if(alpha < 0.5) {
        discard;
    }
    
    ioResult = vec4(1.0);
}