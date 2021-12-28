#version 330 core

//Value should match the variable of the same name in the Scene class.
#define MAX_LIGHTS 32

in vec2 ioTexCoords;
in vec3 ioColor;
in vec3 ioNormal;
in vec3 ioFragPos;
in vec3 ioSkyTexCoords;
in vec4 ioLightFrag;

struct Light {
    float brightness;
    float contrast;
    float distance;
    vec3 position;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

uniform int uType;
uniform int uNumLights;
uniform int uPCFValue;
uniform int uShine;
uniform int uShadowMapActive;
uniform int uBloomOverride;
uniform float uOpacity;
uniform float uMinShadowBias;
uniform float uMaxShadowBias;
uniform float uBloomThreshold;
uniform vec3 uCamPos;
uniform sampler2D uTexture;
uniform sampler2D uDepthTexture;
uniform sampler2D uBloomTexture;
uniform samplerCube uSkyTexture;
uniform Light uLights[MAX_LIGHTS];

layout (location = 0) out vec4 ioFragColor;
layout (location = 1) out vec4 ioBrightColor;

/**
 * Enables the framebuffer texture attachments to better suit unconventional 
 * screen resolutions while maintaining a consistent pixelated look.
 */
float sharpen(float pixArray) {
    float normal  = (fract(pixArray) - 0.5) * 2.0;
    float normal2 = normal * normal;

    return floor(pixArray) + normal * pow(normal2, 2.0) / 2.0 + 0.5;
}

/**
 * Discards fragments whos alpha component is equal to zero. Used in conjunction 
 * with blending to produce transparent textures.
 */
void makeTransparent(float a) {
    if(a == 0) discard;
}

/**
 * Calculates fragment values that will be used to cast shadows on objects 
 * within a certain portion of the scene.
 */
float calcShadow(float dotLightNormal) {
    vec3 pos = ioLightFrag.xyz * 0.5 + 0.5;

    if(pos.z > 1) pos.z = 1;
    
    float depth = texture(uDepthTexture, pos.xy).r;
    float bias  = max(uMaxShadowBias * (1 - dotLightNormal), uMinShadowBias);
    
    if(uPCFValue > 0) {
        vec2 texelSize = 1.0 / textureSize(uDepthTexture, 0);
        float pcfValue = 0;
        float factor   = 1 / (uPCFValue * 2.0 + 1.0);

        for(int x = -uPCFValue; x <= uPCFValue; x++) {
            for(int y = -uPCFValue; y <= uPCFValue; y++) {
                float pcfDepth = texture(uDepthTexture, pos.xy + vec2(x, y) * texelSize).r; 

                if(pos.z + bias < pcfDepth) {
                    pcfValue += factor;
                }
            }
        }

        if(pcfValue > 1) pcfValue = 1;

        return pcfValue;
    } else {
        return (depth + bias) < pos.z ? 0 : 1;
    }
}

/**
 * Calculates the output of the single world light all 3D models will be 
 * illuminated by.
 */
vec3 calcWorldLight(Light light, vec3 normal) {
    vec3 lightDir = normalize(light.position);
    
    float diff   = max(dot(normal, lightDir), 0);
    vec3 diffuse = diff * uLights[0].diffuse * uLights[0].brightness;
    vec3 ambient = uLights[0].ambient * (1 - uLights[0].contrast);
    
    vec3 cameraDir  = normalize(uCamPos - ioFragPos);
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec      = pow(max(dot(cameraDir, reflectDir), 0), uShine);
    vec3 specular   = spec * light.specular;
    
    float dotLightNormal = dot(lightDir, normal);
    
    float shadow = (uShadowMapActive == 1) 
                 ? calcShadow(dotLightNormal)
                 : 1.0f;
    
    vec3 lighting = (uShine != 0) 
                  ? (shadow * diffuse + ambient + specular) * ioColor 
                  : (shadow * diffuse + ambient) * ioColor;
    
    return lighting;
}

/**
 * Calculates the output of individual point lights located throughout the 
 * scene. These are attenuated to have a sphere of influence on nearby 
 * models that decreases over distance.
 */
vec3 calcPointLight(Light light, vec3 normal, vec3 fragPos) {
    vec3 lightDir = normalize(light.position - ioFragPos);
    
    float diff   = max(dot(normal, lightDir), 0);
    vec3 ambient = light.ambient * (1 - light.contrast);
    vec3 diffuse = diff * light.diffuse * light.brightness;
    
    float linear    = 0.14f / light.distance;
    float quadratic = 0.07f / light.distance;
    float dist      = length(light.position - ioFragPos);
    float attenuate = 1.0f / (1.0f + linear * dist + quadratic * (dist * dist));
    
    ambient *= attenuate;
    diffuse *= attenuate;
    
    vec3 cameraDir  = normalize(uCamPos - ioFragPos);
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec      = pow(max(dot(cameraDir, reflectDir), 0), uShine);
    vec3 specular   = spec * light.specular;
    
    vec3 lighting = (uShine != 0) 
                  ? (diffuse + ambient + specular) * ioColor 
                  : (diffuse + ambient) * ioColor;
    
    return lighting;
}

void main() {
    switch(uType) {
        case 0: //Used for framebuffer texture attachments.
            vec2 vRes = textureSize(uTexture, 0);
            
            vec3 sceneColor = texture(uTexture, vec2(
                sharpen(ioTexCoords.x * vRes.x) / vRes.x,
                sharpen(ioTexCoords.y * vRes.y) / vRes.y
            )).rgb;
            
            sceneColor += texture(uBloomTexture, ioTexCoords).rgb;
            
            ioFragColor = vec4(sceneColor, 1);
            break;

        case 1: //Used for text rendering.
            ioFragColor = vec4(ioColor, texture(uTexture, ioTexCoords).a);
            break;

        case 2: case 3: case 10: //Used for rendering "bloom volumes" and UI shapes like polygons and rectangles.
            ioFragColor = vec4(ioColor, uOpacity);
            break;

        case 4: case 7: //Used for drawing icons and sprites.
            ioFragColor = texture(uTexture, ioTexCoords) * vec4(ioColor, 1.0);
            break;

        case 5: case 9: //Used for rendering 3D models and 2D sprites that exhbit lighting effects.
            vec3 lighting = calcWorldLight(uLights[0], normalize(ioNormal));
            
            for(int i = 1; i < uNumLights; i++) {
                lighting += calcPointLight(uLights[i], normalize(ioNormal), ioFragPos);
            }
            
            ioFragColor = texture(uTexture, ioTexCoords) * vec4(lighting, uOpacity);
            break;

        case 6: //Used for light source icons.
            float opacity = texture(uTexture, ioTexCoords).a;
            ioFragColor = texture(uTexture, ioTexCoords) * vec4(ioColor, opacity);
            break;

        case 8: //Used for drawing skyboxes.
            makeTransparent(texture(uSkyTexture, ioSkyTexCoords).a);
            ioFragColor = texture(uSkyTexture, ioSkyTexCoords);
            break;
    }
    
    if(uBloomOverride == 0) {
        float brightness = dot(ioFragColor.rgb, vec3(0.2126, 0.7152, 0.0722));
        ioBrightColor    = (brightness > uBloomThreshold) ? vec4(ioFragColor.rgb, 1) : vec4(0, 0, 0, 1);
    } else {
        ioBrightColor = ioFragColor;
    }
    
}