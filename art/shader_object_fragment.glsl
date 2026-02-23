#version 330 core

#define MAX_LIGHTS 128 //This should match the value in LightingSystem.java

struct Light {
    vec4 position_type;    // xyz = position, w = type (0=POINT, 1=SPOT, 2=WORLD)
    vec4 direction;        // xyz = spotlight direction, w = unused
    vec4 color_brightness; // xyz = color, w = brightness
    vec4 parameters;       // x = range, y = falloff, z = innerCone, w = outerCone
};

struct Material {
    float roughness;
    //float metallic;
    float ambientFactor;
    vec3 albedo;
    vec3 specular;
};

layout (std140) uniform LightBlock {
    Light lights[MAX_LIGHTS];
};

in vec3 ioNormal;
in vec3 ioFragPos;

uniform int uNumLights;
uniform vec3 uColor;
uniform vec3 uCamPos;
uniform Material uMaterial;

out vec4 ioFragColor;

void main() {
    vec3 norm    = normalize(ioNormal);
    vec3 viewDir = normalize(uCamPos - ioFragPos); 
    vec3 result  = vec3(0.0);

    for(int i = 0; i < uNumLights; i++) {
        Light light = lights[i];
        vec3 lightDir;
        float attenuation = 1f;
        float type = light.position_type.w;

        if(type == 2) {
            lightDir = normalize(light.position_type.xyz);
        } else {
            vec3 lightPos  = light.position_type.xyz;
            vec3 toFrag    = lightPos - ioFragPos;
            float distance = length(toFrag);

            lightDir = normalize(toFrag);

            float range      = light.parameters.x;
            float falloff    = light.parameters.y;
            float distFactor = clamp(1.0 - distance / range, 0.0, 1.0);

            attenuation = pow(distFactor, falloff);

            if(type == 1) {
                vec3 spotDir   = normalize(light.direction.xyz);
                float theta    = dot(lightDir, -spotDir);
                float innerCos = light.parameters.z;
                float outerCos = light.parameters.w;
                float spotFactor = clamp((theta - outerCos) / (innerCos - outerCos), 0.0, 1.0);

                attenuation *= spotFactor;
            }
        }

        vec3 lightColor  = light.color_brightness.rgb;
        float brightness = light.color_brightness.w;
        float diff       = max(dot(norm, lightDir), 0.0);

        vec3 reflectDir = reflect(-lightDir, norm);
        float shininess = mix(4.0, 128.0, 1.0 - uMaterial.roughness);
        float spec      = pow(max(dot(viewDir, reflectDir), 0.0), shininess);

        vec3 ambient  = uMaterial.ambientFactor * uMaterial.albedo * lightColor;
        vec3 diffuse  = diff * uMaterial.albedo * lightColor * brightness;
        vec3 specular = spec * uMaterial.specular * lightColor;

        result += (ambient + diffuse + specular) * attenuation;
    }

    ioFragColor = vec4(uColor * result, 1.0);
}