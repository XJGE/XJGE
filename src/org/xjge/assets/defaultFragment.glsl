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
    vec3 position;
    vec3 ambient;
    vec3 diffuse;
};

uniform int uType;
uniform int uNumLights;
uniform int uPCFRadius;
uniform float uOpacity;
uniform sampler2D uTexture;
uniform sampler2D uShadowMap; //TODO: rename to uShadowMapTexture
uniform samplerCube uSkyTexture;
uniform Light uLights[MAX_LIGHTS];

out vec4 ioResult;

/**
 * Enables the framebuffer texture attachments to better suit unconventional 
 * screen resolutions while maintaining a consistent pixelated look.
 */
float sharpen(float pixArray) {
    float normal  = (fract(pixArray) - 0.5) * 2.0;
    float normal2 = normal * normal;

    return floor(pixArray) + normal * pow(normal2, 2.0) / 2.0 + 0.5;
}

float calcShadow2(float dotLightNormal) {
    vec3 projCoords = ioLightFrag.xyz / ioLightFrag.w;
    
    projCoords = projCoords * 0.5 + 0.5;
    
    float closestDepth = texture(uShadowMap, projCoords.xy).r;
    float currentDepth = projCoords.z;
    float bias = max(0.05 * (1 - dotLightNormal), 0.005);
    
    float shadow   = 0.0;
    vec2 texelSize = 1.0 / textureSize(uShadowMap, 0);
    
    //TODO:
    // Fix the following undesirable behavior:
    // - supplying a PCFRadius value less than 3 darkens eveything
    // - the walls near the ceiling inside the starting areas are not 
    //   properly shaded, might be a result of some erroneous depth 
    //   calculations here?
    //
    
    for(int x = -uPCFRadius; x <= uPCFRadius; ++x) {
        for(int y = -uPCFRadius; y <= uPCFRadius; ++y) {
            float pcfDepth = texture(uShadowMap, projCoords.xy + vec2(x, y) * texelSize).r;
            shadow += currentDepth - bias > pcfDepth ? 0.0 : 1.0;
        }
    }
    
    shadow /= (uLights[0].contrast * 100);
    
    if(projCoords.z > 1.0) shadow = 0.0;
    
    return shadow;
}

/**
 * Allows texture transparency by discarding the fragments produced by its alpha 
 * channel.
 */
void makeTransparent(float a) {
    if(a == 0) discard;
}

float calcShadow(float dotLightNormal) {
    vec3 pos = ioLightFrag.xyz * 0.5 + 0.5;

    if(pos.z > 1) pos.z = 1;

    float depth = texture(uShadowMap, pos.xy).r;

    float bias = max(0.0009 * (1 - dotLightNormal), 0.00003);
    return (depth + bias) < pos.z ? 0 : 1;
}

/**
 * Calculates the output of the single world light all 3D models will be 
 * illuminated by.
 */
vec3 calcWorldLight(Light light, vec3 normal) {
    vec3 direction = normalize(light.position);
    
    float diff   = max(dot(normal, direction), 0);
    vec3 diffuse = diff * uLights[0].diffuse * uLights[0].brightness;
    vec3 ambient = uLights[0].ambient * uLights[0].contrast;
    
    float dotLightNormal = dot(direction, normal);
    float shadow         = calcShadow2(dotLightNormal); //calcShadow(dotLightNormal); //TODO: add option to toggle PCF filtering
    vec3 lighting        = (shadow * diffuse + ambient) * ioColor;
    
    return lighting;
}

/**
 * Calculates the output of individual point lights located throughout the 
 * scene. These are attenuated to have a sphere of influence on nearby 
 * models that decreases over distance.
 */
vec3 calcPointLight(Light light, vec3 normal, vec3 fragPos) {
    vec3 ambient = light.ambient;

    vec3 direction = normalize(light.position - ioFragPos);
    float diff     = max(dot(normal, direction), -light.contrast);
    vec3 diffuse   = diff * light.diffuse;

    float linear    = 0.0014f / light.brightness;
    float quadratic = 0.000007f / light.brightness;
    float dist      = length(light.position - ioFragPos);
    float attenuate = 1.0f / (1.0f + linear * dist + quadratic * (dist * dist));

    ambient *= attenuate;
    diffuse *= attenuate;

    return (ambient + diffuse) * light.brightness;
}

void main() {
    switch(uType) {
        case 0: //Used for framebuffer texture attachments.
            vec2 vRes = textureSize(uTexture, 0);
            
            ioResult = texture(uTexture, vec2(
                sharpen(ioTexCoords.x * vRes.x) / vRes.x,
                sharpen(ioTexCoords.y * vRes.y) / vRes.y
            ));
            break;

        case 1: //Used for text rendering.
            ioResult = vec4(ioColor, texture(uTexture, ioTexCoords).a);
            break;

        case 2: case 3: //Used for rendering shapes on the UI like polygons and rectangles.
            ioResult = vec4(ioColor, uOpacity);
            break;

        case 4: case 7: //Used for drawing icons and sprites.
            ioResult = texture(uTexture, ioTexCoords) * vec4(ioColor, 1.0);
            break;

        case 5: case 9: //Used for rendering 3D models and 2D sprites that exhbit lighting effects.
            vec3 lighting = calcWorldLight(uLights[0], normalize(ioNormal));
            
            for(int i = 1; i < uNumLights; i++) {
                lighting += calcPointLight(uLights[i], normalize(ioNormal), ioFragPos);
            }
            
            ioResult = texture(uTexture, ioTexCoords) * vec4(lighting, uOpacity);
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