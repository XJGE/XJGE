#version 330 core

in vec2 ioTexCoords;

uniform sampler2D uTexture;

void main() {
    
	vec3 test = any(greaterThan(texture(uTexture, ioTexCoords).rgb, vec3(100))) ? vec3(1) : vec3(0);
	
	vec3 outputColor = vec3(0.5f); //= any(greaterThan(texture(uTexture, ioTexCoords).rgb, vec3(100))) ? vec3(1) : vec3(0);
    
    gl_FragColor = vec4(outputColor, 1);
}