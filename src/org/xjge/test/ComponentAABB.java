package org.xjge.test;

import java.nio.FloatBuffer;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30C.*;
import org.lwjgl.system.MemoryStack;
import org.xjge.core.Component;
import org.xjge.core.ErrorUtils;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Graphics;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class ComponentAABB extends Component {

    float width;
    float height;
    float depth;
    
    private final Graphics graphics = new Graphics();
    
    ComponentAABB(float width, float height, float depth) {
        this.width  = width;
        this.height = height;
        this.depth  = depth;
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            int[] indexData = {
                //Front
                4,5,
                5,6,
                6,7,
                7,4,
                
                //Back
                0,1,
                1,2,
                2,3,
                3,0,
                
                //Center
                0,4,
                3,7,
                1,5,
                2,6
            };
            
            graphics.vertices = stack.mallocFloat(24);
            graphics.indices  = stack.mallocInt(indexData.length);
            
            graphics.indices.put(indexData).flip();
        }
        
        graphics.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        glEnableVertexAttribArray(0);
    }
    
    void update() {
        if(owner.hasComponent(ComponentPosition.class)) {
            Vector3f position = owner.getComponent(ComponentPosition.class).position;
            graphics.modelMatrix.translation(position);
        }
    }
    
    void render(GLProgram shader) {
        shader.use();
        
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glLineWidth(4);
        
        glBindVertexArray(graphics.vao);
        
        shader.setUniform("uModel", false, graphics.modelMatrix);
        shader.setUniform("uColor", Color.CYAN.asVector3f());
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertices = stack.mallocFloat(24);
            
            float halfWidth = width / 2;
            float halfDepth = depth / 2;
            
            vertices.put(-halfWidth).put(0)      .put(halfDepth);
            vertices .put(halfWidth).put(0)      .put(halfDepth);
            vertices .put(halfWidth).put(height) .put(halfDepth);
            vertices.put(-halfWidth).put(height) .put(halfDepth);
            vertices.put(-halfWidth).put(0)     .put(-halfDepth);
            vertices .put(halfWidth).put(0)     .put(-halfDepth);
            vertices .put(halfWidth).put(height).put(-halfDepth);
            vertices.put(-halfWidth).put(height).put(-halfDepth);
            
            vertices.flip();
            
            glBindBuffer(GL_ARRAY_BUFFER, graphics.vbo);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }
        
        glDrawElements(GL_LINES, graphics.indices.capacity(), GL_UNSIGNED_INT, 0);
        glLineWidth(1);
        glDisable(GL_BLEND);
        
        ErrorUtils.checkGLError();
    }
    
}