#version 330 core

in vec2 ioTexCoords;

layout (location = 0) out vec4 ioFragColor;

uniform sampler2D uTexture;
uniform sampler2D uBloomTexture;

/**
 * Allows the framebuffer texture attachments to better suit unconventional screen resolutions while maintaining a consistent pixelated look. This is 
 * only apparent when the engines internal resolution is smaller than the screen resolution (retro-look).
 */
float sharpen(float pixArray) {
    float normal  = (fract(pixArray) - 0.5) * 2.0;
    float normal2 = normal * normal;

    return floor(pixArray) + normal * pow(normal2, 2.0) / 2.0 + 0.5;
}

void main() {
    vec2 vRes = textureSize(uTexture, 0);
            
    vec3 sceneColor = texture(uTexture, vec2(
        sharpen(ioTexCoords.x * vRes.x) / vRes.x,
        sharpen(ioTexCoords.y * vRes.y) / vRes.y
    )).rgb;
    
    sceneColor += texture(uBloomTexture, ioTexCoords).rgb;

    ioFragColor = vec4(sceneColor, 1);

}