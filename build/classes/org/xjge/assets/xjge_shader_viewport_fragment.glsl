#version 330 core

in vec2 ioTexCoords;

layout (location = 0) out vec4 ioFragColor;
layout (location = 1) out vec4 ioBrightColor;

uniform int uBloomOverride;
uniform float uBloomThreshold;
uniform sampler2D uTexture;
uniform sampler2D uBloomTexture;

/**
 * Enables the framebuffer texture attachments to better suit unconventional 
 * screen resolutions while maintaining a consistent pixelated look.
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

    if(uBloomOverride == 0) {
        float brightness = dot(ioFragColor.rgb, vec3(0.2126, 0.7152, 0.0722));
        ioBrightColor    = (brightness > uBloomThreshold) ? vec4(ioFragColor.rgb, 1) : vec4(0, 0, 0, 1);
    } else {
        ioBrightColor = ioFragColor;
    }
}