package org.xjge.scenes;

import java.util.Map;
import org.joml.Matrix3f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;
import org.xjge.core.Camera;
import org.xjge.core.Entity;
import org.xjge.core.ErrorUtils;
import org.xjge.core.Game;
import org.xjge.core.LightSource;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Graphics;
import org.xjge.graphics.Texture;

/**
 * Nov 12, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class EntitySprite extends Entity {

    private Vector2f texCoords = new Vector2f();
    private Graphics g      = new Graphics();
    private Texture texture;
    private Matrix3f normal = new Matrix3f();
    
    EntitySprite(float x, float y, float z, float width, float depth) {
        super(new Vector3f(x, y, z));
        
        texture = new Texture("img_windows.png");
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(32);
            g.indices  = stack.mallocInt(6);
            
            //(vec3 position), (vec2 texCoords), (vec3 normal)
            g.vertices.put(-width).put(0).put(-depth)     .put(0).put(0)    .put(0).put(1).put(0);
            g.vertices.put(-width).put(0) .put(depth)     .put(1).put(0)    .put(0).put(1).put(0);
            g.vertices .put(width).put(0) .put(depth)     .put(1).put(1)    .put(0).put(1).put(0);
            g.vertices. put(width).put(0).put(-depth)     .put(0).put(1)    .put(0).put(1).put(0);
            
            g.indices.put(0).put(1).put(2);
            g.indices.put(2).put(3).put(0);
            
            g.vertices.flip();
            g.indices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (8 * Float.BYTES), 0);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, (8 * Float.BYTES), (3 * Float.BYTES));
        glVertexAttribPointer(3, 3, GL_FLOAT, false, (8 * Float.BYTES), (5 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        g.modelMatrix.translation(position);
        g.modelMatrix.rotateX((float) Math.toRadians(45f));
    }

    @Override
    public void render(GLProgram glProgram, Camera camera, LightSource[] lights, int numLights) {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.handle);
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, Game.shadowMap.depthTexHandle);
        glBindVertexArray(g.vao);
        
        glProgram.setUniform("uType", 7);
        glProgram.setUniform("uColor", Color.WHITE.asVec3());
        //glProgram.setUniform("uNormal", true, normal);
        glProgram.setUniform("uTexCoords", texCoords);
        glProgram.setUniform("uModel", false, g.modelMatrix);
        glProgram.setUniform("uTexture", 0);
        glProgram.setUniform("uShadowMap", 1);
        
        glDrawElements(GL_TRIANGLES, g.indices.capacity(), GL_UNSIGNED_INT, 0);
        glDisable(GL_BLEND);
        glDisable(GL_DEPTH_TEST);
        
        ErrorUtils.checkGLError();
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, Camera camera, LightSource[] lights, int numLights) {
    }

    @Override
    public void renderShadow(GLProgram depthProgram) {
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