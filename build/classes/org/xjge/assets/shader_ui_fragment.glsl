#version 330 core

in vec2 ioTexCoords;
in vec4 ioColor;

uniform int uType;
uniform int uIsBitmapFont;
uniform sampler2D uTexture;

layout (location = 0) out vec4 ioFragColor;

void main() {
    switch(uType) {
        case 0: //Used for rendering fonts
            vec3 textColorOutput = (uIsBitmapFont == 1) 
                                 ? ioColor.xyz * texture(uTexture, ioTexCoords).xyz 
                                 : ioColor.xyz;
            ioFragColor = vec4(textColorOutput, texture(uTexture, ioTexCoords).a * ioColor.w);
            break;

        case 1: //Used for rendering rectangles
            ioFragColor = vec4(ioColor);
            break;
    }
}