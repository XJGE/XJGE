#version 330 core

//Non-instanced attributes
layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec2 aTexCoords;
layout (location = 5) in vec3 aNormal;

//Instanced attributes
layout (location = 2) in vec3  aPosOffset;
layout (location = 3) in vec3  aColOffset;
layout (location = 4) in float aHovOffset;

uniform int  uType;
uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProjection;
uniform mat3 uNormal;

out vec3  ioColor;
out vec2  ioTexCoords;
out vec3  ioNormal;
out vec3  ioFragPos;
out float ioHovered;

void main() {
    switch(uType) {
        case 0: //Used for floor tiles.
            ioColor     = aColOffset;
            ioTexCoords = aTexCoords;
            gl_Position = uProjection * uView * uModel * vec4(aPosition + aPosOffset, 1);
            break;
        
        case 1: //Used for the build tool indicator and solid entity collision cylinders.
            gl_Position = uProjection * uView * uModel * vec4(aPosition, 1);
            break;
        
        case 2: //Used for arena blocks.
            ioHovered   = aHovOffset;
            ioColor     = aColOffset;
            ioTexCoords = aTexCoords;
            gl_Position = uProjection * uView * uModel * vec4(aPosition + aPosOffset, 1);
            break;
        
        case 3: //Used for faces.
            ioColor     = aColOffset;
            gl_Position = uProjection * uView * uModel * vec4(aPosition + aPosOffset, 1);
            break;
        
        case 4: //Used for block renderer.
            ioColor     = aColOffset;
            ioTexCoords = aTexCoords;
            ioNormal    = uNormal * aNormal;
            ioFragPos   = vec3(uModel * vec4(aPosition, 1));
            gl_Position = uProjection * uView * uModel * vec4(aPosition + aPosOffset, 1);
            break;
        
        case 5: //Used for foliage
            ioTexCoords = aTexCoords;
            gl_Position = uProjection * uView * uModel * vec4(aPosition, 1);
            break;
    }
}