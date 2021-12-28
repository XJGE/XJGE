package org.xjge.scenes;

import java.util.Map;
import org.joml.Matrix3f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_GREATER;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glAlphaFunc;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
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
import org.xjge.graphics.Texture;

/**
 * Dec 27, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class TestEntity extends Entity {

    private boolean isBG;
    
    private Texture texture;
    private Graphics g;
    
    private Vector2f texCoords = new Vector2f();
    private Matrix3f normal    = new Matrix3f();
    
    private Color offWhite = Color.create(230, 230, 230);
    
    TestEntity(float x, float y, float z, float size, boolean isBG) {
        super(new Vector3f(x, y, z));
        this.isBG = isBG;
        
        texture = new Texture("img_null.png");
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glBindTexture(GL_TEXTURE_2D, 0);
        
        g = new Graphics();
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.callocFloat(20);
            g.indices  = stack.callocInt(6);
            
            float halfSize = size / 2;
            
            if(!isBG) {
                //(vec3 position), (vec2 texCoords)
                g.vertices.put(-halfSize).put(-halfSize).put(0) .put(0).put(1);
                g.vertices.put(-halfSize) .put(halfSize).put(0) .put(0).put(0);
                g.vertices .put(halfSize) .put(halfSize).put(0) .put(1).put(0);
                g.vertices .put(halfSize).put(-halfSize).put(0) .put(1).put(1);
            } else {
                //(vec3 position), (vec2 texCoords)
                g.vertices.put(-halfSize).put(-halfSize).put(0) .put(0).put(0);
                g.vertices.put(-halfSize) .put(halfSize).put(0) .put(0).put(0);
                g.vertices .put(halfSize) .put(halfSize).put(0) .put(0).put(0);
                g.vertices .put(halfSize).put(-halfSize).put(0) .put(0).put(0);
            }
            
            g.indices.put(0).put(1).put(2);
            g.indices.put(2).put(3).put(0);
            
            g.vertices.flip();
            g.indices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (5 * Float.BYTES), 0);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, (5 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(2);
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        g.modelMatrix.translation(position);
    }

    @Override
    public void render(GLProgram glProgram, Camera camera, Light[] lights, int depthTexHandle) {
        glProgram.use();
        
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, 0);
        
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.handle);
        
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, depthTexHandle);
        
        glBindVertexArray(g.vao);
        
        glProgram.setUniform("uType", 9);
        glProgram.setUniform("uColor", (isBG) ? offWhite.asVec3() : Color.WHITE.asVec3());
        glProgram.setUniform("uNormal", true, normal);
        glProgram.setUniform("uTexCoords", texCoords);
        glProgram.setUniform("uModel", false, g.modelMatrix);
        glProgram.setUniform("uOpacity", 1f);
        glProgram.setUniform("uTexture", 0);
        glProgram.setUniform("uShadowMap", 1);
        
        glDrawElements(GL_TRIANGLES, g.indices.capacity(), GL_UNSIGNED_INT, 0);
        glDisable(GL_ALPHA_TEST);
        glDisable(GL_BLEND);
        glDisable(GL_DEPTH_TEST);
        
        ErrorUtils.checkGLError();
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, Camera camera, Light[] lights, int depthTexHandle) {
    }

    @Override
    public void renderShadow(GLProgram depthProgram) {
        glEnable(GL_DEPTH_TEST);
        glBindVertexArray(g.vao);
        glBindTexture(GL_TEXTURE_2D, texture.handle);
        
        depthProgram.setUniform("uModel", false, g.modelMatrix);
        
        glDrawElements(GL_TRIANGLES, g.indices.capacity(), GL_UNSIGNED_INT, 0);
        glDisable(GL_DEPTH_TEST);
        
        ErrorUtils.checkGLError();
    }

    @Override
    protected void destroy() {
    }

}
