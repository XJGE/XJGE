#version 330 core

//Values correspond to the varaibles of the same name in the Model class. 
#define MAX_WEIGHTS 4
#define MAX_BONES 128

//Non-instanced attributes
layout (location = 0) in vec3  aPosition;
layout (location = 1) in vec3  aColor;
layout (location = 2) in vec2  aTexCoords;
layout (location = 3) in vec3  aNormal;
layout (location = 7) in ivec4 aBoneIDs;
layout (location = 8) in vec4  aWeights;

//Instanced attributes
layout (location = 4) in vec3 aPosOffset;
layout (location = 5) in vec2 aTexOffset;
layout (location = 6) in vec3 aColOffset;

uniform int  uType;
uniform vec2 uTexCoords;
uniform vec3 uColor;
uniform mat3 uNormal;
uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProjection;
uniform mat4 uBoneTransforms[MAX_BONES];
uniform mat4 uLightSpace;

out vec2 ioTexCoords;
out vec3 ioColor;
out vec3 ioNormal;
out vec3 ioFragPos;
out vec3 ioSkyTexCoords;
out vec4 ioLightFrag;

void main() {
    switch(uType) {
        case 0: //Used for framebuffer texture attachments.
            ioTexCoords = aTexCoords;
            gl_Position = uProjection * vec4(aPosition, 1);
            break;

        case 1: //Used for text rendering.
            ioTexCoords = aTexCoords + aTexOffset;
            ioColor     = aColOffset;
            gl_Position = uProjection * vec4(aPosition + aPosOffset, 1);
            break;

        case 2: //Used for drawing polygon objects on the UI.
            ioColor     = uColor;
            gl_Position = uProjection * uModel * vec4(aPosition, 1);
            break;

        case 3: //Used for batch rendering rectangles on the UI.
            ioColor     = aColor;
            gl_Position = uProjection * vec4(aPosition, 1);
            break;

        case 4: //Used for icons that appear as part of the UI.
            ioTexCoords = aTexCoords + uTexCoords;
            ioColor     = uColor;
            gl_Position = uProjection * uModel * vec4(aPosition, 1);
            break;

        case 5: //Used for rendering 3D models.
            vec4 initPos    = vec4(0, 0, 0, 0);
            vec4 initNormal = vec4(0, 0, 0, 0);
            int count       = 0;

            for(int i = 0; i < MAX_WEIGHTS; i++) {
                float weight = aWeights[i];

                if(weight > 0) {
                    count++;

                    int boneID  = aBoneIDs[i];
                    vec4 tmpPos = uBoneTransforms[boneID] * vec4(aPosition, 1);
                    initPos     += weight * tmpPos;

                    vec4 tmpNormal = uBoneTransforms[boneID] * vec4(aNormal, 0);
                    initNormal     += weight * tmpNormal;
                }

                if(count == 0) {
                    initPos    = vec4(aPosition, 1);
                    initNormal = vec4(aNormal, 0);
                }
            }

            mat4 modelViewMatrix = uView * uModel;
            vec4 mvPos = modelViewMatrix * initPos;
            gl_Position = uProjection * mvPos;

            ioColor     = uColor;
            ioTexCoords = aTexCoords;
            ioNormal    = uNormal * initNormal.xyz;
            ioFragPos   = vec3(uModel * vec4(aPosition, 1));
            //ioLightFrag = uLightSpace * vec4(ioFragPos, 1);
            break;

        case 6: //Used for light source icons.
            ioTexCoords = aTexCoords + uTexCoords;
            ioColor     = uColor;
            gl_Position = uProjection * uView * uModel * vec4(aPosition, 1);
            break;

        case 7: //Used for animated 2D sprites.
            ioTexCoords = aTexCoords + uTexCoords;
            ioColor     = uColor;
            gl_Position = uProjection * uView * uModel * vec4(aPosition, 1);
            break;

        case 8: //Used for drawing skyboxes.
            ioSkyTexCoords = aPosition;
            gl_Position    = (uProjection * uView * uModel * vec4(aPosition, 1));
            break;
        
        case 9: //TODO: temp, used for test objects Plane and Cube.
            ioColor     = uColor;
            ioFragPos   = vec3(uModel * vec4(aPosition, 1));
            ioNormal    = uNormal * aNormal;
            ioLightFrag = uLightSpace * vec4(ioFragPos, 1);
            gl_Position = uProjection * uView * uModel * vec4(aPosition, 1);
            break;
    }
}