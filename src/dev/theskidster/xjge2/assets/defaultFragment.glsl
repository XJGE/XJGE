#version 330 core

in vec3 ioColor;
in vec2 ioTexCoords;
in vec3 ioSkyTexCoords;

uniform int uType;
uniform float uOpacity;
uniform sampler2D uTexture;
uniform samplerCube uSkyTexture;

out vec4 ioResult;

float sharpen(float pixArray) {
    float normal  = (fract(pixArray) - 0.5) * 2.0;
    float normal2 = normal * normal;

    return floor(pixArray) + normal * pow(normal2, 2.0) / 2.0 + 0.5;
}

void makeTransparent(float a) {
    if(a == 0) discard;
}

void main() {
    switch(uType) {
        case 0: //Used for viewport framebuffer texture attachments.
            vec2 vRes = textureSize(uTexture, 1);
            
            ioResult = texture(uTexture, vec2(
                sharpen(ioTexCoords.x * vRes.x) / vRes.x,
                sharpen(ioTexCoords.y * vRes.y) / vRes.y
            ));
            break;

        case 1: //Used for rendering text.
            ioResult = vec4(ioColor, texture(uTexture, ioTexCoords).a);
            break;

        case 2: case 3: case -1: //Used for rendering polygons and rectangles.
            ioResult = vec4(ioColor, uOpacity);
            break;

        case 4: case 7: //Used for rendering icons and animatied 2D sprites.
            ioResult = texture(uTexture, ioTexCoords);
            break;

        case 5:
            break;

        case 6: //Used for light source icons.
            float opacity = texture(uTexture, ioTexCoords).a;
            ioResult = texture(uTexture, ioTexCoords) * vec4(ioColor, opacity);
            break;

        case 8: //Used for drawing skyboxes.
            makeTransparent(texture(uSkyTexture, ioSkyTexCoords).a);
            ioResult = texture(uSkyTexture, ioSkyTexCoords);
            break;
    }
}