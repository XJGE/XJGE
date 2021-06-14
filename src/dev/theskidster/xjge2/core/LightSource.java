package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Atlas;
import dev.theskidster.xjge2.graphics.Graphics;
import dev.theskidster.xjge2.graphics.Light;
import dev.theskidster.xjge2.graphics.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Jun 13, 2021
 */

public final class LightSource {
    
    private final boolean isWorldLight;
    
    private final Light light;
    private final Graphics g;
    private final Texture iconTexture;
    private final Atlas atlas;
    private final Vector2f texCoords;
    
    LightSource(boolean isWorldLight, Light light, Texture iconTexture) {
        this.isWorldLight = isWorldLight;
        this.light        = light;
        this.iconTexture  = iconTexture;
        
        g         = new Graphics();
        atlas     = new Atlas(iconTexture, 64, 64);
        texCoords = new Vector2f();
        
        setTexCoords();
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(20);
            g.indices  = stack.mallocInt(6);
            
            //(vec3 position), (vec2 texCoords)
            g.vertices.put(-0.5f) .put(0.5f).put(0) .put(0)                  .put(0);
            g.vertices .put(0.5f) .put(0.5f).put(0) .put(atlas.subImageWidth).put(0);
            g.vertices .put(0.5f).put(-0.5f).put(0) .put(atlas.subImageWidth).put(atlas.subImageHeight);
            g.vertices.put(-0.5f).put(-0.5f).put(0) .put(0)                  .put(atlas.subImageHeight);
            
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
    
    LightSource(boolean isWorldLight, Light light, LightSource source) {
        this.isWorldLight = isWorldLight;
        this.light        = light;
        
        g           = source.g;
        iconTexture = source.iconTexture;
        atlas       = source.atlas;
        texCoords   = source.texCoords;
        
        setTexCoords();
    }
    
    private void setTexCoords() {
        if(isWorldLight) texCoords.set(atlas.subImageWidth, atlas.subImageHeight);
        else             texCoords.set(atlas.subImageWidth * 2, atlas.subImageHeight);
    }
    
    void update() {
        g.modelMatrix.translation(light.position);
    }
    
    void render(Vector3f camPos, Vector3f camDir, Vector3f camUp) {
        g.modelMatrix.billboardSpherical(light.position, camPos, camUp);
        g.modelMatrix.scale(camPos.distance(light.position) / 10);

        XJGE.getDefaultGLProgram().use();
        
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBindTexture(GL_TEXTURE_2D, iconTexture.handle);
        glBindVertexArray(g.vao);
        
        XJGE.getDefaultGLProgram().setUniform("uType", 6);
        XJGE.getDefaultGLProgram().setUniform("uModel", false, g.modelMatrix);
        XJGE.getDefaultGLProgram().setUniform("uColor", light.ambientColor.asVec3());
        XJGE.getDefaultGLProgram().setUniform("uTexCoords", texCoords);
        
        glDrawElements(GL_TRIANGLES, g.indices.capacity(), GL_UNSIGNED_INT, 0);
        glDisable(GL_BLEND);
        ErrorUtils.checkGLError();
    }
    
    public boolean getEnabled() {
        return light.enabled;
    }
    
    public float getBrightness() {
        return light.brightness;
    }
    
    public float getContrast() {
        return light.contrast;
    }
    
    public Vector3f getPosition() {
        return light.position;
    }
    
    public Vector3f getAmbientColor() {
        return light.ambientColor.asVec3();
    }
    
    public Vector3f getDiffuseColor() {
        return light.diffuseColor.asVec3();
    }
    
}