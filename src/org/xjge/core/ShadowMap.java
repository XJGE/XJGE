package org.xjge.core;

import java.util.LinkedHashMap;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import org.xjge.graphics.GLProgram;

//Created: Nov 9, 2021

/**
 * @author J Hoffman
 * @since  2.0.0
 */
public final class ShadowMap {

    private int textureWidth;
    private int textureHeight;
    private int fbo;
    final int depthTexHandle;
    public int PCFValue;
    
    public float minBias;
    public float maxBias;
    public float frustumSize;
    public float range;
    
    private boolean useLinearFilter;
    
    private final Vector3f lightDir  = new Vector3f();
    private final Matrix4f lightView = new Matrix4f();
    private final Matrix4f lightProj = new Matrix4f();
    
    final Matrix4f lightSpace = new Matrix4f();
    
    public ShadowMap(float minBias, float maxBias, float frustumSize, float range, boolean useLinearFilter,
                     int textureWidth, int textureHeight, boolean showBounds, int PCFValue) {
        fbo            = glGenFramebuffers();
        depthTexHandle = glGenTextures();
        
        this.minBias         = minBias;
        this.maxBias         = maxBias;
        this.frustumSize     = frustumSize;
        this.range           = range;
        this.PCFValue        = PCFValue;
        
        updateTexture(useLinearFilter, textureWidth, textureHeight);
        setShowBounds(showBounds);
        
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexHandle, 0);
            glDrawBuffer(GL_NONE);
            glReadBuffer(GL_NONE);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    
    public ShadowMap() {
        this(0.00003f, 0.0009f, 100f, 100f, true, 1920, 1920, false, 1);
    }
    
    void generate(Vector3f camUp, GLProgram depthProgram, Vector3f worldLightPos, LinkedHashMap<String, Entity> entities) {
        lightProj.setOrtho(-frustumSize, frustumSize, -frustumSize, frustumSize, 1f, range);
        lightView.setLookAt(worldLightPos, lightDir, camUp);
        lightProj.mul(lightView, lightSpace);
        
        depthProgram.use();
        depthProgram.setUniform("uLightSpace", false, lightSpace);
        
        glViewport(0, 0, textureWidth, textureHeight);
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
            glClear(GL_DEPTH_BUFFER_BIT);
            glBindTexture(GL_TEXTURE_2D, depthTexHandle);
            entities.values().forEach(entity -> entity.renderShadow(depthProgram));
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    
    private void updateTexture(boolean useLinearFilter, int textureWidth, int textureHeight) {
        this.useLinearFilter = useLinearFilter;
        this.textureWidth    = textureWidth;
        this.textureHeight   = textureHeight;
        
        glBindTexture(GL_TEXTURE_2D, depthTexHandle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, (useLinearFilter) ? GL_LINEAR : GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, (useLinearFilter) ? GL_LINEAR : GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, textureWidth, textureHeight, 0, GL_DEPTH_COMPONENT, GL_FLOAT, NULL);
        glBindTexture(GL_TEXTURE_2D, 0);
    }
    
    public void setTextureSize(int width, int height) {
        updateTexture(useLinearFilter, width, height);
    }
    
    public void setTextureFilter(boolean useLinearFilter) {
        updateTexture(useLinearFilter, textureWidth, textureHeight);
    }
    
    public void setShowBounds(boolean value) {
        float[] borderColor = (value) 
                            ? new float[] {0, 0, 0, 0} 
                            : new float[] {1, 1, 1, 1};
        
        glBindTexture(GL_TEXTURE_2D, depthTexHandle);
            glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor);
        glBindTexture(GL_TEXTURE_2D, 0);
    }
    
}
