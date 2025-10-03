package org.xjge.test;

import java.util.Map;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import org.lwjgl.system.MemoryStack;
import org.xjge.core.Component;
import org.xjge.core.ErrorUtils;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Graphics;
import org.xjge.graphics.Texture;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class ComponentMudBall extends Component {
    
    private final Graphics graphics = new Graphics();
    
    private static final Texture texture;
    
    static {
        texture = new Texture("image_mudball.png");
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glBindTexture(GL_TEXTURE_2D, 0);
    }
    
    ComponentMudBall() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            graphics.vertices = stack.mallocFloat(32);
            graphics.indices  = stack.mallocInt(6);
            
            //(vec3 position), (vec2 texcoords)
            graphics.vertices.put(-0.5f).put( 0.5f).put(0) .put(0).put(1);
            graphics.vertices.put( 0.5f).put( 0.5f).put(0) .put(1).put(1);
            graphics.vertices.put( 0.5f).put(-0.5f).put(0) .put(1).put(0);
            graphics.vertices.put(-0.5f).put(-0.5f).put(0) .put(0).put(0);
            
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
    
    void render(Map<String, GLProgram> glPrograms, Vector3f position, Vector3f cameraPos) {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, 0);
        
        glActiveTexture(GL_TEXTURE0);
        texture.bind(GL_TEXTURE_2D);
        
        glBindVertexArray(graphics.vao);
        
        GLProgram glProgram = glPrograms.get("mudball");
        glProgram.use();
        
        graphics.modelMatrix.translation(position);
        graphics.modelMatrix.billboardSpherical(position, cameraPos);
        graphics.modelMatrix.scale(0.75f);
        
        glProgram.setUniform("uTexture", 0);
        glProgram.setUniform("uColor", Color.BROWN.asVector3f());
        glProgram.setUniform("uModel", false, graphics.modelMatrix);
        
        glDrawElements(GL_TRIANGLES, graphics.indices.capacity(), GL_UNSIGNED_INT, 0);
        
        glDisable(GL_ALPHA_TEST);
        
        ErrorUtils.checkGLError();
    }
    
    void destroy() {
        graphics.freeBuffers();
    }
    
}