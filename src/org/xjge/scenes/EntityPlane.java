package org.xjge.scenes;

import java.util.Map;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;
import org.xjge.core.Camera;
import org.xjge.core.Entity;
import org.xjge.core.ErrorUtils;
import org.xjge.core.LightSource;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Graphics;

/**
 * Nov 9, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class EntityPlane extends Entity {

    private final Graphics g      = new Graphics();
    private final Matrix3f normal = new Matrix3f();
    
    private final Color color;
    
    private float angle;
    
    EntityPlane(float x, float y, float z, int width, int depth, Color color) {
        super(new Vector3f(x, y, z));
        
        this.color = color;
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(24);
            g.indices  = stack.mallocInt(6);
            
            //(vec3 position), (vec3 normal)
            g.vertices.put(-width).put(0).put(-depth)     .put(0).put(1).put(0);
            g.vertices.put(-width).put(0) .put(depth)     .put(0).put(1).put(0);
            g.vertices .put(width).put(0) .put(depth)     .put(0).put(1).put(0);
            g.vertices. put(width).put(0).put(-depth)     .put(0).put(1).put(0);
            
            g.indices.put(0).put(1).put(2);
            g.indices.put(2).put(3).put(0);
            
            g.vertices.flip();
            g.indices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (6 * Float.BYTES), 0);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, (6 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(3);
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        g.modelMatrix.translation(position);
        
        if(color == Color.RED) {
            g.modelMatrix.rotationY((float) Math.toRadians(angle += 0.1f));
        }
    }

    @Override
    public void render(GLProgram glProgram, Camera camera, LightSource[] lights, int numLights) {
        glEnable(GL_DEPTH_TEST);
        glBindVertexArray(g.vao);
        
        glProgram.setUniform("uType", 9);
        glProgram.setUniform("uColor", color.asVec3());
        glProgram.setUniform("uNormal", true, normal);
        glProgram.setUniform("uModel", false, g.modelMatrix);
        
        glDrawElements(GL_TRIANGLES, g.indices.capacity(), GL_UNSIGNED_INT, 0);
        glDisable(GL_DEPTH_TEST);
        
        ErrorUtils.checkGLError();
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, Camera camera, LightSource[] lights, int numLights) {
    }

    @Override
    public void renderShadow(GLProgram depthProgram) {
        //TODO: this could be automated- but would require a Graphics object
        
        glEnable(GL_DEPTH_TEST);
        glBindVertexArray(g.vao);
        
        depthProgram.setUniform("uModel", false, g.modelMatrix);
        
        glDrawElements(GL_TRIANGLES, g.indices.capacity(), GL_UNSIGNED_INT, 0);
        glDisable(GL_DEPTH_TEST);
        
        ErrorUtils.checkGLError();
    }

    @Override
    protected void destroy() {
    }

}
