#version 330 core

#define MAX_WEIGHTS 4 
#define MAX_BONES 128

layout(location = 0) in vec3 aPosition;
layout(location = 1) in vec3 aNormal;
layout(location = 2) in vec3 aTangent;
layout(location = 3) in vec2 aUV;
layout(location = 4) in vec4 aBoneWeights;
layout(location = 5) in ivec4 aBoneIDs;

uniform mat3 uNormalMatrix;
uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProjection;
uniform mat4 uBoneTransforms[MAX_BONES];

out vec3 ioFragPos;
out vec2 ioUV;
out mat3 ioTBN;

void main() {
    ivec4 boneIDs = ivec4(aBoneIDs);
    vec4 weights  = aBoneWeights;

    mat4 skinMatrix = mat4(0.0);

    for(int i = 0; i < MAX_WEIGHTS; i++) {
        if(weights[i] > 0.0) {
            skinMatrix += weights[i] * uBoneTransforms[aBoneIDs[i]];
        }
    }

    //Fallback if there are no valid weights
    //if(skinMatrix == mat4(0.0)) skinMatrix = mat4(1.0);

    mat3 skinMat3       = mat3(skinMatrix);
    vec3 skinnedNormal  = normalize(skinMat3 * aNormal);
    vec3 skinnedTangent = normalize(skinMat3 * aTangent);

    vec3 N = normalize(uNormalMatrix * skinnedNormal);
    vec3 T = normalize(uNormalMatrix * skinnedTangent);
    vec3 B = normalize(cross(N, T));

    ioTBN = mat3(T, B, N);
    ioUV  = aUV;

    vec4 skinnedPosition = skinMatrix * vec4(aPosition, 1.0);
    vec4 worldPosition   = uModel * skinnedPosition;

    ioFragPos = vec3(worldPosition);
    gl_Position = uProjection * uView * worldPosition;
}