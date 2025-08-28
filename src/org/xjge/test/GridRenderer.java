package org.xjge.test;

import java.nio.FloatBuffer;
import java.util.Map;
import org.joml.Vector3f;
import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL33.*;
import org.lwjgl.system.MemoryStack;
import org.xjge.core.ErrorUtils;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Graphics;
import org.xjge.graphics.Texture;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class GridRenderer {

    private final int vboPositionOffset = glGenBuffers();
    private final int vboColorOffset    = glGenBuffers();
    
    private final Graphics graphics = new Graphics();
    private final Texture texture;
    
    GridRenderer() {
        texture = new Texture("image_gridspace.png");
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glBindTexture(GL_TEXTURE_2D, 0);
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            float[] vertexData = {
                //Front face
                -0.5f,-0.5f, 0.5f, 1,1,1, 0,0,
                 0.5f,-0.5f, 0.5f, 1,1,1, 1,0,
                 0.5f, 0.5f, 0.5f, 1,1,1, 1,1,
                -0.5f, 0.5f, 0.5f, 1,1,1, 0,1,

                //Back face
                -0.5f,-0.5f,-0.5f, 1,1,1, 1,0,
                 0.5f,-0.5f,-0.5f, 1,1,1, 0,0,
                 0.5f, 0.5f,-0.5f, 1,1,1, 0,1,
                -0.5f, 0.5f,-0.5f, 1,1,1, 1,1,

                //Left face
                -0.5f,-0.5f,-0.5f, 1,1,1, 0,0,
                -0.5f,-0.5f, 0.5f, 1,1,1, 1,0,
                -0.5f, 0.5f, 0.5f, 1,1,1, 1,1,
                -0.5f, 0.5f,-0.5f, 1,1,1, 0,1,

                //Right face
                 0.5f,-0.5f,-0.5f, 1,1,1, 1,0,
                 0.5f,-0.5f, 0.5f, 1,1,1, 0,0,
                 0.5f, 0.5f, 0.5f, 1,1,1, 0,1,
                 0.5f, 0.5f,-0.5f, 1,1,1, 1,1,

                //Top face
                -0.5f, 0.5f,-0.5f, 1,1,1, 0,0,
                -0.5f, 0.5f, 0.5f, 1,1,1, 0,1,
                 0.5f, 0.5f, 0.5f, 1,1,1, 1,1,
                 0.5f, 0.5f,-0.5f, 1,1,1, 1,0,

                //Bottom face
                -0.5f,-0.5f,-0.5f, 1,1,1, 0,1,
                -0.5f,-0.5f, 0.5f, 1,1,1, 0,0,
                 0.5f,-0.5f, 0.5f, 1,1,1, 1,0,
                 0.5f,-0.5f,-0.5f, 1,1,1, 1,1,
            };

            //Indices (6 faces × 2 triangles each)
            int[] indexData = {
                0,1,2,    2,3,0,    //Front
                4,5,6,    6,7,4,    //Back
                8,9,10,   10,11,8,  //Left
                12,13,14, 14,15,12, //Right
                16,17,18, 18,19,16, //Top
                20,21,22, 22,23,20  //Bottom
            };
            
            graphics.vertices = stack.mallocFloat(vertexData.length);
            graphics.indices  = stack.mallocInt(indexData.length);
            
            graphics.vertices.put(vertexData).flip();
            graphics.indices.put(indexData).flip();
        }
        
        graphics.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (8 * Float.BYTES), 0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, (8 * Float.BYTES), (3 * Float.BYTES));
        glVertexAttribPointer(2, 2, GL_FLOAT, false, (8 * Float.BYTES), (6 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
    }
    
    void draw(GLProgram glProgram, Map<Vector3f, GridSpace> spaces) {
        glEnable(GL_DEPTH_TEST);
        glBindVertexArray(graphics.vao);
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer positions = stack.mallocFloat(spaces.size() * 3);
            FloatBuffer colors    = stack.mallocFloat(spaces.size() * 3);
            
            spaces.values().forEach(space -> {
                positions.put(space.xLocation)
                         .put(space.yLocation - 0.5f)
                         .put(space.zLocation);
                
                switch(space.type) {
                    default -> {
                        switch(space.status) {
                            default       -> colors.put(0.8f).put(0.8f).put(0.8f);
                            case SELECTED -> colors.put(0.8f).put(1f).put(0.8f);
                            case INVALID  -> colors.put(1f).put(0.8f).put(0.8f);
                        }
                    }
                    case 1 -> colors.put(0.4f).put(0.4f).put(0.4f);
                }
            });
            
            positions.flip();
            colors.flip();
            
            glBindBuffer(GL_ARRAY_BUFFER, vboPositionOffset);
            glBufferData(GL_ARRAY_BUFFER, positions, GL_STATIC_DRAW);
            
            glVertexAttribPointer(3, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
            glEnableVertexAttribArray(3);
            glVertexAttribDivisor(3, 1);
            
            glBindBuffer(GL_ARRAY_BUFFER, vboColorOffset);
            glBufferData(GL_ARRAY_BUFFER, colors, GL_STATIC_DRAW);
            
            glVertexAttribPointer(4, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
            glEnableVertexAttribArray(4);
            glVertexAttribDivisor(4, 1);
        }
        
        glActiveTexture(GL_TEXTURE0);
        texture.bind(GL_TEXTURE_2D);
        
        glProgram.use();
        
        glProgram.setUniform("uTexture", 0);
        glProgram.setUniform("uModel", false, graphics.modelMatrix);
        
        glDrawElementsInstanced(GL_TRIANGLES, 36, GL_UNSIGNED_INT, 0, spaces.size());
        glDisable(GL_DEPTH_TEST);
        
        ErrorUtils.checkGLError();
    }
    
}