#version 330 core

in vec3 ioNormal;

uniform vec3 uColor;

out vec4 ioFragColor;

void main() {
    vec3 lightPos = vec3(1, 4, 2.5);
    vec3 lightDir = normalize(lightPos);
    
    vec3 norm    = normalize(ioNormal);
    float diff   = max(dot(norm, lightDir), -0.6);
    vec3 diffuse = (diff * uColor * uColor) + 0.3;

    ioFragColor = vec4(uColor + diffuse, 1) * 0.5;
}