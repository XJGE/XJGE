#version 330 core

//Non-instanced attributes
layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec3 aColor;
layout (location = 2) in vec2 aTexCoords;

//Instanced attributes
layout (location = 4) in vec3 aPosOffset;
layout (location = 5) in vec2 aTexOffset;
layout (location = 6) in vec3 aColOffset;

uniform int uType;
uniform vec2 uTexCoords;
uniform vec3 uColor;
uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProjection;

out vec3 ioColor;
out vec2 ioTexCoords;

void main() {
    switch(uType) {
        case 0: //Used for viewport framebuffer texture attachments.
            ioTexCoords = aTexCoords;
            gl_Position = uProjection * vec4(aPosition, 1);
            break;

        case 1: //Used for rendering text.
            ioTexCoords = aTexCoords + aTexOffset;
            ioColor     = aColOffset;
            gl_Position = uProjection * vec4(aPosition + aPosOffset, 1);
            break;

        case 2: //TEMP: for drawing the test entity.
            ioColor = aColor;
            gl_Position = uProjection * uView * uModel * vec4(aPosition, 1);
            break;

        case 3: //Used for rendering background rectangles.
            ioColor = aColor;
            gl_Position = uProjection * vec4(aPosition, 1);
            break;

        case 4: //Used for rendering icons.
            ioTexCoords = aTexCoords + uTexCoords;
            gl_Position = uProjection * uModel * vec4(aPosition, 1);
            break;

        case 5: //Used for light source icons.
            ioTexCoords = aTexCoords + uTexCoords;
            ioColor     = uColor;
            gl_Position = uProjection * uView * uModel * vec4(aPosition, 1);
            break;

        case 6: //Used for animated 2D sprites.
            ioTexCoords = aTexCoords + uTexCoords;
            gl_Position = uProjection * uView * uModel * vec4(aPosition, 1);
            break;
    }
}