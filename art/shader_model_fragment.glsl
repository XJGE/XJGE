#version 330 core

#define MAX_LIGHTS 128

struct Light {
    vec4 position_type;
    vec4 direction;
    vec4 color_brightness;
    vec4 parameters;
};

layout(std140) uniform LightBlock {
    Light lights[MAX_LIGHTS];
};

struct Material {
    vec3 albedo;
    float metallic;
    float roughness;
};

uniform Material uMaterial;

uniform sampler2D uAlbedoMap;
uniform sampler2D uNormalMap;
uniform sampler2D uMetallicRoughnessMap;

uniform int uHasAlbedoMap;
uniform int uHasNormalMap;
uniform int uHasMetallicRoughnessMap;

uniform vec3 uCamPos;
uniform int uNumLights;

in vec3 ioFragPos;
in vec2 ioUV;
in mat3 ioTBN;

out vec4 ioFragColor;

const float PI = 3.14159265359;

float DistributionGGX(vec3 N, vec3 H, float roughness) {
    float a = roughness * roughness;
    float a2 = a * a;
    float NdotH = max(dot(N, H), 0.0);
    float denom = (NdotH * NdotH * (a2 - 1.0) + 1.0);
    return a2 / (PI * denom * denom);
}

float GeometrySchlickGGX(float NdotV, float roughness) {
    float r = roughness + 1.0;
    float k = (r * r) / 8.0;
    return NdotV / (NdotV * (1.0 - k) + k);
}

float GeometrySmith(vec3 N, vec3 V, vec3 L, float roughness) {
    return GeometrySchlickGGX(max(dot(N,V),0.0), roughness) *
           GeometrySchlickGGX(max(dot(N,L),0.0), roughness);
}

vec3 FresnelSchlick(float cosTheta, vec3 F0) {
    return F0 + (1.0 - F0) * pow(1.0 - cosTheta, 5.0);
}

void main() {

    vec3 albedo = uMaterial.albedo;
    float metallic = uMaterial.metallic;
    float roughness = uMaterial.roughness;

    if(uHasAlbedoMap == 1)
        albedo *= texture(uAlbedoMap, ioUV).rgb;

    if(uHasMetallicRoughnessMap == 1) {
        vec3 mr = texture(uMetallicRoughnessMap, ioUV).rgb;
        metallic *= mr.b;
        roughness *= mr.g;
    }

    vec3 N = normalize(ioTBN[2]);
    if(uHasNormalMap == 1) {
        vec3 tangentNormal = texture(uNormalMap, ioUV).rgb * 2.0 - 1.0;
        N = normalize(ioTBN * tangentNormal);
    }

    vec3 V = normalize(uCamPos - ioFragPos);

    vec3 F0 = mix(vec3(0.04), albedo, metallic);

    vec3 Lo = vec3(0.0);

    for(int i = 0; i < uNumLights; i++) {

        vec3 L = normalize(lights[i].position_type.xyz - ioFragPos);
        vec3 H = normalize(V + L);

        float distance = length(lights[i].position_type.xyz - ioFragPos);
        float attenuation = 1.0 / (distance * distance);
        vec3 radiance = lights[i].color_brightness.rgb *
                        lights[i].color_brightness.w *
                        attenuation;

        float NDF = DistributionGGX(N, H, roughness);
        float G   = GeometrySmith(N, V, L, roughness);
        vec3 F    = FresnelSchlick(max(dot(H,V),0.0), F0);

        vec3 numerator = NDF * G * F;
        float denom = 4.0 * max(dot(N,V),0.0) * max(dot(N,L),0.0) + 0.001;
        vec3 specular = numerator / denom;

        vec3 kS = F;
        vec3 kD = (1.0 - kS) * (1.0 - metallic);

        float NdotL = max(dot(N,L), 0.0);

        Lo += (kD * albedo / PI + specular) * radiance * NdotL;
    }

    vec3 ambient = 0.03 * albedo;

    vec3 color = ambient + Lo;

    color = color / (color + vec3(1.0)); // tone mapping
    color = pow(color, vec3(1.0/2.2));   // gamma

    ioFragColor = vec4(color, 1.0);
}