package org.xjge.test;

import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;
import org.xjge.core.Component;
import org.xjge.core.ErrorUtils;
import org.xjge.core.Puppet;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Graphics;
import org.xjge.graphics.Texture;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class ComponentUnit extends Component {

    int team;
    
    private final Vector3f position;
    private final Graphics graphics = new Graphics();
    //private final Puppet puppet;
    
    private static final Texture texture;
    
    static {
        texture = new Texture("image_units.png");
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glBindTexture(GL_TEXTURE_2D, 0);
    }
    
    ComponentUnit(Vector3f position, String name, int team, int deviceID) {
        this.position = position;
        
        //texture offset because I'm too lazy to use sprites
        float t1 = (team == 0) ? 0 : 0.5f;
        float t2 = (team == 0) ? 0.5f : 1f;
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            graphics.vertices = stack.mallocFloat(20);
            graphics.indices  = stack.mallocInt(6);
            
            //(vec3 position), (vec2 texCoords)
            graphics.vertices.put(-0.5f).put(0).put( 0.5f).put(t1).put(1f);
            graphics.vertices.put( 0.5f).put(0).put( 0.5f).put(t2).put(1f);
            graphics.vertices.put( 0.5f).put(0).put(-0.5f).put(t2).put(0f);
            graphics.vertices.put(-0.5f).put(0).put(-0.5f).put(t1).put(0f);
            
            graphics.indices.put(0).put(1).put(2);
            graphics.indices.put(2).put(3).put(0);
            
            graphics.vertices.flip();
            graphics.indices.flip();
        }
        
        graphics.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (5 * Float.BYTES), 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, (5 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
    }
    
    void render(GLProgram shader) {
        graphics.modelMatrix.translation(position);
        
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, 0);
        
        glActiveTexture(GL_TEXTURE0);
        texture.bind(GL_TEXTURE_2D);
        
        glBindVertexArray(graphics.vao);
        
        shader.use();
        shader.setUniform("uModel", false, graphics.modelMatrix);

        glDrawElements(GL_TRIANGLES, graphics.indices.capacity(), GL_UNSIGNED_INT, 0);
        
        glDisable(GL_ALPHA_TEST);
        
        ErrorUtils.checkGLError();
    }
    
}