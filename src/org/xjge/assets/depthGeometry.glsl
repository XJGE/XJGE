#version 330 core

in vec2 ioTexCoords[];

layout (triangles) in;
layout (triangle_strip, max_vertices = 18) out;

uniform mat4 uShadowMatrices[6];

out vec2 ioTexCoords2;
out vec4 ioFragPos;

void main() {
    ioTexCoords2 = ioTexCoords[0];
    
    for(int f = 0; f < 6; f++) {
        gl_Layer = f;
        
        for(int v = 0; v < 3; v++) {
            ioFragPos = gl_in[v].gl_Position;
            gl_Position = uShadowMatrices[f] * ioFragPos;
            EmitVertex();
        }
        
        EndPrimitive();
    }
}