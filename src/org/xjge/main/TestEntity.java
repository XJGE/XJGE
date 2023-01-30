package org.xjge.main;

//Created: Jan 29, 2023

import java.util.Map;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;
import org.xjge.core.Camera;
import org.xjge.core.Entity;
import org.xjge.core.ErrorUtils;
import org.xjge.core.Light;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Graphics;
import org.xjge.graphics.Texture;


/**
 * @author J Hoffman
 * @since  
 */
class TestEntity extends Entity {

    private final Texture texture;
    private final Graphics g;
    
    private final Color[] palette = new Color[6];
    
    TestEntity(float x, float y, float z, float size) {
        super(new Vector3f(x, y, z));
        
        palette[0] = Color.BLACK;
        palette[1] = Color.create(64, 4, 38);
        palette[2] = Color.create(0, 61, 166);
        palette[3] = Color.create(244, 36, 36);
        palette[4] = Color.create(255, 160, 128);
        palette[5] = Color.WHITE;
        
        texture = new Texture("img_mario.png");
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glBindTexture(GL_TEXTURE_2D, 0);
        
        g = new Graphics();
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(20);
            g.indices  = stack.mallocInt(6);
            
            //(vec3 position), (vec2 tex coords)
            g.vertices.put(-size / 2).put( size / 2).put(0) .put(0).put(0);
            g.vertices.put( size / 2).put( size / 2).put(0) .put(1).put(0);
            g.vertices.put( size / 2).put(-size / 2).put(0) .put(1).put(1);
            g.vertices.put(-size / 2).put(-size / 2).put(0) .put(0).put(1);
            
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
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        g.modelMatrix.translation(position);
    }

    @Override
    public void render(GLProgram glProgram, Camera camera, Light[] lights, int depthTexHandle) {
        glProgram.use();
        
        glBindVertexArray(g.vao);
        
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.handle);
        
        glProgram.setUniform("uModel", false, g.modelMatrix);
        glProgram.setUniform("uTexture", 0);
        
        for(int i = 0; i < palette.length; i++) {
            glProgram.setUniform("uPalette[" + i + "]", palette[i].asVec3());
        }
        
        glDrawElements(GL_TRIANGLES, g.indices.limit(), GL_UNSIGNED_INT, 0);
        
        glDisable(GL_BLEND);
        
        ErrorUtils.checkGLError();
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, Camera camera, Light[] lights, int depthTexHandle) {
    }

    @Override
    public void renderShadow(GLProgram depthProgram) {
    }

    @Override
    protected void destroy() {
    }

}
