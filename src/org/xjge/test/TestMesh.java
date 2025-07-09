package org.xjge.test;

import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;
import org.xjge.core.Component;
import org.xjge.core.ErrorUtils;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Graphics;
import org.xjge.graphics.Texture;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class TestMesh extends Component {

    private final Vector3f position;
    private final Graphics g = new Graphics();
    
    TestMesh(int size, float x, float y, float z) {
        position = new Vector3f(x, y, z);
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(20);
            g.indices  = stack.mallocInt(6);
            
            //(vec3 position), (vec2 texCoords)
            g.vertices.put(-size / 2f).put( size / 2f).put(0f).put(0f).put(0f);
            g.vertices.put( size / 2f).put( size / 2f).put(0f).put(1f).put(0f);
            g.vertices.put( size / 2f).put(-size / 2f).put(0f).put(1f).put(1f);
            g.vertices.put(-size / 2f).put(-size / 2f).put(0f).put(0f).put(1f);
            
            g.indices.put(0).put(1).put(2);
            g.indices.put(2).put(3).put(0);
            
            g.vertices.flip();
            g.indices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (5 * Float.BYTES), 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, (5 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
    }
    
    void render(GLProgram shader) {
        g.modelMatrix.translation(position);
        
        //glEnable(GL_DEPTH_TEST);
        
        glActiveTexture(GL_TEXTURE0);
        Texture.placeholder.bind(GL_TEXTURE_2D); //TODO: Texture is all black?
        glBindVertexArray(g.vao);
        
        shader.use();
        shader.setUniform("uModel", false, g.modelMatrix);

        glDrawElements(GL_TRIANGLES, g.indices.capacity(), GL_UNSIGNED_INT, 0);
        
        //glDisable(GL_DEPTH_TEST);
        
        ErrorUtils.checkGLError();
    }
    
}