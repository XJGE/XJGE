#version 330 core

in vec3 ioColor;
in vec2 ioTexCoords;

uniform int uType;
uniform sampler2D uTexture;

out vec4 ioResult;

float sharpen(float pixArray) {
    float normal  = (fract(pixArray) - 0.5) * 2.0;
    float normal2 = normal * normal;

    return floor(pixArray) + normal * pow(normal2, 2.0) / 2.0 + 0.5;
}

void main() {
    switch(uType) {
        case 0: //Used for viewport framebuffer texture attachments.
            vec2 vRes = textureSize(uTexture, 0);
            
            ioResult = texture(uTexture, vec2(
                sharpen(ioTexCoords.x * vRes.x) / vRes.x,
                sharpen(ioTexCoords.y * vRes.y) / vRes.y
            ));
            break;

        case 1:
            ioResult = vec4(ioColor, 0);
            break;
    }
}