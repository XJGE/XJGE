#version 330 core

in vec2 ioTexCoords;
in vec4 ioColorRGBA;

uniform int uType;
uniform int uIsBitmapFont;
uniform sampler2D uTexture;

layout (location = 0) out vec4 ioFragColor;

void main() {
    switch(uType) {
        case 0: //Used for rendering fonts
            vec3 textColorOutput = (uIsBitmapFont == 1) 
                                 ? ioColorRGBA.xyz * texture(uTexture, ioTexCoords).xyz 
                                 : ioColorRGBA.xyz;
            ioFragColor = vec4(textColorOutput, texture(uTexture, ioTexCoords).a * ioColorRGBA.w);
            break;

        case 1: case 2: //Used for rendering rectangles and polygons
            ioFragColor = ioColorRGBA;
            break;

        case 3: //Used for rendering icons
            ioFragColor = texture(uTexture, ioTexCoords) * ioColorRGBA;
            break;
    }
}