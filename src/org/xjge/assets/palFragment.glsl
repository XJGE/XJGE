#version 330 core

in vec2 ioTexCoords;

uniform sampler2D uTexture;
uniform vec3[6] uPalette;

out vec4 ioFragColor;

void main() {
    int palIndex = 0;

    switch(int(255 * texture(uTexture, ioTexCoords).r)) {        
        case 51:
            palIndex = 1;
            break;
        
        case 102:
            palIndex = 2;
            break;

        case 153:
            palIndex = 3;
            break;

        case 204:
            palIndex = 4;
            break;
        
        case 255:
            palIndex = 5;
            break;
    }
    
    ioFragColor = vec4(uPalette[palIndex], texture(uTexture, ioTexCoords).a);
}