#version 330 core

layout(location = 0) in vec3 aPosition;
layout(location = 1) in vec3 aNormal;
layout(location = 2) in vec3 aTangent;
layout(location = 3) in vec2 aUV;
layout(location = 4) in vec4 aBoneWeights;
layout(location = 5) in ivec4 aBoneIDs;

uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProjection;
uniform mat3 uNormalMatrix;

out vec3 ioFragPos;
out vec2 ioUV;
out mat3 ioTBN;

void main() {
    vec3 N = normalize(uNormalMatrix * aNormal);
    vec3 T = normalize(uNormalMatrix * aTangent);
    vec3 B = normalize(cross(N, T));

    ioTBN = mat3(T, B, N);

    ioFragPos = vec3(uModel * vec4(aPosition, 1.0));
    ioUV = aUV;

    gl_Position = uProjection * uView * vec4(ioFragPos, 1.0);
}