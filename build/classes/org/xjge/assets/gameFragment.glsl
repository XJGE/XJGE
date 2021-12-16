#version 330 core

in vec3  ioColor;
in vec2  ioTexCoords;
in float ioHovered;
in vec3 ioNormal;
in vec3 ioFragPos;

uniform int  uType;
uniform vec3 uColor;
uniform sampler2D uTexture;
uniform vec3 uAmbientColor;
uniform vec3 uDiffuseColor;
uniform vec3 uLightPosition;
uniform float uBrightness;
uniform float uContrast;

out vec4 ioResult;

void main() {
    switch(uType) {
        case 0: //Used for floor tiles.
            //if(ioColor.r == 0) discard;
        
            ioResult = texture(uTexture, ioTexCoords) * vec4(ioColor, 1);
            break;
        
        case 1: //Used for the build tool indicator and solid entity collision cylinders.
            ioResult = vec4(uColor, 0.4f);
            break;
        
        case 2: //Used for arena blocks.
            float hoverColor = (ioHovered > -1) ? 1.35f : 1;
            vec4 brickColor  = texture(uTexture, ioTexCoords);
            
            if(brickColor.r < 0.8f && brickColor.g < 0.8f && brickColor.b < 0.8f) {
                ioResult = (brickColor * vec4(ioColor, 1)) * hoverColor;
            } else {
                ioResult = (brickColor / 1.15f) * hoverColor;
            }            
            break;
        
        case 3: //Used for faces.
            if(ioColor.r == 0) discard;
            ioResult = vec4(ioColor, 0.35f);
            break;
        
        case 4: //Used for block renderer.
            vec3 lightDir = normalize(uLightPosition - ioFragPos);
            float diff    = max(dot(normalize(ioNormal), lightDir), -uContrast);
            vec3 diffuse  = diff * uAmbientColor;

            ioResult = texture(uTexture, ioTexCoords) * vec4((((uAmbientColor + diffuse) * uDiffuseColor) * ioColor) * uBrightness, 1);
            break;
        
        case 5: //Used for foliage
            if(texture(uTexture, ioTexCoords).a <= 0.5f) discard;
            ioResult = texture(uTexture, ioTexCoords);
            break;
    }
}