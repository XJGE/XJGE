#version 330 core

#define MAX_LIGHTS 128

struct Light {
    vec4 position_type;    // xyz = position OR direction, w = type (0=POINT,1=SPOT,2=WORLD)
    vec4 direction;        // xyz = spotlight direction
    vec4 color_brightness; // xyz = color, w = brightness
    vec4 parameters;       // x = range, y = falloff, z = innerCone, w = outerCone
};

layout(std140) uniform LightBlock {
    Light lights[MAX_LIGHTS];
};

struct Material {
    vec3  albedo;
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

//
// PBR Helper Functions
//

float DistributionGGX(vec3 N, vec3 H, float roughness) {
    float a  = roughness * roughness;
    float a2 = a * a;

    float NdotH  = max(dot(N, H), 0.0);
    float NdotH2 = NdotH * NdotH;

    float denom = (NdotH2 * (a2 - 1.0) + 1.0);
    return a2 / (PI * denom * denom + 0.0001);
}

float GeometrySchlickGGX(float NdotV, float roughness) {
    float r = roughness + 1.0;
    float k = (r * r) / 8.0;

    return NdotV / (NdotV * (1.0 - k) + k);
}

float GeometrySmith(vec3 N, vec3 V, vec3 L, float roughness) {
    float ggx1 = GeometrySchlickGGX(max(dot(N,V),0.0), roughness);
    float ggx2 = GeometrySchlickGGX(max(dot(N,L),0.0), roughness);
    return ggx1 * ggx2;
}

vec3 FresnelSchlick(float cosTheta, vec3 F0) {
    return F0 + (1.0 - F0) * pow(1.0 - cosTheta, 5.0);
}

void main()
{
    //
    // Material Sampling
    //

    vec3  albedo    = uMaterial.albedo;
    float metallic  = uMaterial.metallic;
    float roughness = clamp(uMaterial.roughness, 0.04, 1.0);

    if(uHasAlbedoMap == 1)
        albedo *= texture(uAlbedoMap, ioUV).rgb;

    if(uHasMetallicRoughnessMap == 1) {
        vec3 mr = texture(uMetallicRoughnessMap, ioUV).rgb;
        metallic  *= mr.b;
        roughness *= mr.g;
        roughness = clamp(roughness, 0.04, 1.0);
    }

    //
    // Normal Mapping
    //

    vec3 N = normalize(ioTBN[2]);

    if(uHasNormalMap == 1) {
        vec3 tangentNormal = texture(uNormalMap, ioUV).rgb * 2.0 - 1.0;
        N = normalize(ioTBN * tangentNormal);
    }

    vec3 V = normalize(uCamPos - ioFragPos);

    //
    // Base Reflectivity
    //

    vec3 F0 = mix(vec3(0.04), albedo, metallic);

    vec3 Lo = vec3(0.0);

    //
    // Lighting Loop
    //

    for(int i = 0; i < uNumLights; i++)
    {
        Light light = lights[i];
        float type = light.position_type.w;

        vec3 L;
        float attenuation = 1.0;

        //
        // Directional (WORLD)
        //
        if(type == 2.0)
        {
            L = normalize(light.position_type.xyz);
            attenuation = 1.0;
        }
        else
        {
            //
            // Point / Spot
            //
            vec3 lightPos = light.position_type.xyz;
            vec3 toFrag   = lightPos - ioFragPos;

            float distance = length(toFrag);
            L = normalize(toFrag);

            float range   = light.parameters.x;
            float falloff = light.parameters.y;

            float distFactor = clamp(1.0 - distance / range, 0.0, 1.0);
            attenuation = pow(distFactor, falloff);

            //
            // Spot Light
            //
            if(type == 1.0)
            {
                vec3 spotDir = normalize(light.direction.xyz);

                float theta = dot(L, -spotDir);

                float innerCos = light.parameters.z;
                float outerCos = light.parameters.w;

                float spotFactor = clamp(
                    (theta - outerCos) / (innerCos - outerCos),
                    0.0,
                    1.0
                );

                attenuation *= spotFactor;
            }
        }

        //
        // Radiance
        //

        vec3 radiance =
            light.color_brightness.rgb *
            light.color_brightness.w *
            attenuation;

        //
        // Cook-Torrance BRDF
        //

        vec3 H = normalize(V + L);

        float NDF = DistributionGGX(N, H, roughness);
        float G   = GeometrySmith(N, V, L, roughness);
        vec3  F   = FresnelSchlick(max(dot(H,V),0.0), F0);

        vec3 numerator = NDF * G * F;
        float denom = 4.0 *
                      max(dot(N,V),0.0) *
                      max(dot(N,L),0.0) + 0.001;

        vec3 specular = numerator / denom;

        vec3 kS = F;
        vec3 kD = (1.0 - kS) * (1.0 - metallic);

        float NdotL = max(dot(N,L), 0.0);

        Lo += (kD * albedo / PI + specular) *
              radiance *
              NdotL;
    }

    //
    // Ambient (temporary until IBL)
    //

    vec3 ambient = 0.03 * albedo;

    vec3 color = ambient + Lo;

    //
    // Tone Mapping + Gamma
    //

    color = color / (color + vec3(1.0));
    color = pow(color, vec3(1.0 / 2.2));

    ioFragColor = vec4(color, 1.0);
}