#version 330 core

#define MAX_LIGHTS 128 //This should match the value in LightingSystem.java

struct Light {
    vec4 position_type;    // xyz = position/direction, w = type (0=POINT, 1=SPOT, 2=WORLD)
    vec4 color_brightness; // xyz = color, w = brightness
    vec4 parameters;       // x = range, y = falloff, zw = unused
};

layout (std140) uniform LightBlock {
    Light lights[MAX_LIGHTS];
};

in vec3 ioNormal;
in vec3 ioFragPos;

uniform int uNumLights;
uniform vec3 uColor;

out vec4 ioFragColor;

void main() {
    vec3 norm = normalize(ioNormal);
    vec3 result = vec3(0.0);

    for(int i = 0; i < uNumLights; i++) {
        Light light = lights[i];
        vec3 lightDir;

        if(light.position_type.w == 2.0) {
            //WORLD (directional)
            lightDir = normalize(light.position_type.xyz);
        } else {
            //POINT
            vec3 lightPos = light.position_type.xyz;
            lightDir = normalize(lightPos - ioFragPos);
        }

        float diff = max(dot(norm, lightDir), 0.0);
        vec3 color = light.color_brightness.rgb;
        float brightness = light.color_brightness.w;
        
        result += diff * color * brightness;
    }

    ioFragColor = vec4(uColor * result, 1.0);
}