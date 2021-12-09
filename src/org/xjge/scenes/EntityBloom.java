package org.xjge.scenes;

import java.util.Map;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import org.lwjgl.system.MemoryStack;
import org.xjge.core.Camera;
import org.xjge.core.Entity;
import org.xjge.core.ErrorUtils;
import org.xjge.core.Light;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Graphics;

/**
 * Dec 8, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class EntityBloom extends Entity {

    private Graphics g;
    
    public EntityBloom(float x, float y, float z, Color color, float size) {
        super(new Vector3f(x, y, z));
        
        g = new Graphics();
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(24);
            
            //(vec3 position), (vec3 color)
            g.vertices.put(-(size / 2)).put(-(size / 2)).put(0)   .put(color.r + 1).put(color.g + 1).put(color.b + 1);
            g.vertices.put(0)          .put(size / 2)   .put(0)   .put(color.r + 1).put(color.g + 1).put(color.b + 1);
            g.vertices.put(size / 2)   .put(-(size / 2)).put(0)   .put(color.r + 1).put(color.g + 1).put(color.b + 1);
            
            g.vertices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (6 * Float.BYTES), 0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, (6 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        g.modelMatrix.translation(position);
    }

    @Override
    public void render(GLProgram glProgram, Camera camera, Light[] lights, int shadowMapTexHandle) {
        glEnable(GL_DEPTH_TEST);
        glBindVertexArray(g.vao);
        
        glProgram.setUniform("uType", 10);
        glProgram.setUniform("uOpacity", 1f);
        glProgram.setUniform("uModel", false, g.modelMatrix);
        
        glDrawArrays(GL_TRIANGLES, 0, 3);
        glDisable(GL_DEPTH_TEST);
        
        ErrorUtils.checkGLError();
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, Camera camera, Light[] lights, int shadowMapTexHandle) {
    }

    @Override
    public void renderShadow(GLProgram depthProgram) {
    }

    @Override
    protected void destroy() {
    }

    
    
}