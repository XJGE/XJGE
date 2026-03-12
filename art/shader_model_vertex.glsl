#version 330 core

layout(location = 0) in vec3 aPosition;
layout(location = 1) in vec3 aNormal;
layout(location = 2) in vec2 aUV;
layout(location = 3) in vec3 aTangent;
layout(location = 4) in vec4 aBoneIDs;
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
    //Build skinning matrix from bone weights
    mat4 skinMatrix = 
        aBoneWeights.x * uBones[int(aBoneIDs.x)] +
        aBoneWeights.y * uBones[int(aBoneIDs.y)] +
        aBoneWeights.z * uBones[int(aBoneIDs.z)] +
        aBoneWeights.w * uBones[int(aBoneIDs.w)];

    //Apply skinning to position, normal, and tangent
    vec4 skinnedPosition = skinMatrix * vec4(aPosition, 1.0);
    vec3 skinnedNormal   = mat3(skinMatrix) * aNormal;
    vec3 skinnedTangent  = mat3(skinMatrix) * aTangent;
    
    //Transform to world space
    vec4 worldPos = uModel * skinnedPosition;
    ioFragPos = worldPos.xyz;

    //Transform normals to world space
    vec3 N = normalize(uNormalMatrix * skinnedNormal);
    vec3 T = normalize(uNormalMatrix * skinnedTangent);
    vec3 B = normalize(cross(N, T));
    ioTBN = mat3(T, B, N);

    //Pass UVs
    ioUV = aUV;

    //Final clip space position
    gl_Position = uProjection * uView * worldPos; //uProjection * uView * uModel * vec4(aPosition, 1.0);
}