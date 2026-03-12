#version 330 core

layout(location = 0) in vec3 aPosition;
layout(location = 1) in vec3 aNormal;
layout(location = 2) in vec2 aUV;
layout(location = 3) in vec3 aTangent;
layout(location = 4) in ivec4 aBoneIDs;
layout(location = 5) in vec4 aBoneWeights;

uniform mat3 uNormalMatrix;
uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProjection;
uniform mat4 uBones[128];

out vec2 ioUV;
out vec3 ioFragPos;
out mat3 ioTBN;

void main() {
    // --- Build weighted skinning matrices ---
    mat4 skinMatrix = mat4(0.0);
    mat3 skinRot = mat3(0.0);

    // Accumulate weighted transforms
    for(int i = 0; i < 4; i++) {
        int bone = aBoneIDs[i];
        float weight = aBoneWeights[i];
        if(weight > 0.0) {
            mat4 boneMat = uBones[bone];
            skinMatrix += weight * boneMat;

            // Extract rotation part for normal/tangent
            mat3 boneRot = mat3(boneMat);
            skinRot += weight * boneRot;
        }
    }

    // --- Apply skinning ---
    vec4 skinnedPosition = skinMatrix * vec4(aPosition, 1.0);
    vec3 skinnedNormal   = normalize(skinRot * aNormal);
    vec3 skinnedTangent  = normalize(skinRot * aTangent);

    // --- Transform to world space ---
    vec4 worldPos = uModel * skinnedPosition;
    ioFragPos = worldPos.xyz;

    vec3 N = normalize(uNormalMatrix * skinnedNormal);
    vec3 T = normalize(uNormalMatrix * skinnedTangent);
    vec3 B = normalize(cross(N, T));
    ioTBN = mat3(T, B, N);

    // --- UVs ---
    ioUV = aUV;

    // --- Final clip-space position ---
    gl_Position = uProjection * uView * worldPos;
}