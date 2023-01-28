#version 330 core

in vec2 ioTexCoords;

uniform sampler2D uTexture;
uniform vec3[] uPalette;

float sharpen(float pixArray) {
    float normal  = (fract(pixArray) - 0.5) * 2.0;
    float normal2 = normal * normal;

    return floor(pixArray) + normal * pow(normal2, 2.0) / 2.0 + 0.5;
}

void main() {
    vec2 vRes = textureSize(uTexture, 0);
    
    vec3 sceneColor = texture(uTexture, vec2(sharpen(ioTexCoords.x * vRes.x) / vRes.x, sharpen(ioTexCoords.y * vRes.y) / vRes.y)).rgb;

    //vec3 changeColor = any(greaterThan(texColor, vec3(0.5f))) ? vec3(1) : vec3(0);

    gl_FragColor = vec4(sceneColor, 1);
}