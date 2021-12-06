package org.xjge.core;

import java.util.LinkedHashMap;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import org.xjge.graphics.GLProgram;

/**
 * Nov 9, 2021
 */

/**
 * @author J Hoffman
 * @since  2.0.0
 */
public final class ShadowMap {

    private int textureWidth  = 4096;
    private int textureHeight = 4096;
    private int fbo;
    public final int depthTexHandle;
    
    int PCFValue = 1;
    
    private final float NEAR_PLANE = 1f;
    private final float FAR_PLANE  = 100f;
    
    final Vector3f lightDir   = new Vector3f();
    final Matrix4f lightView  = new Matrix4f();
    final Matrix4f lightProj  = new Matrix4f();
    final Matrix4f lightSpace = new Matrix4f();
    
    ShadowMap() {
        fbo = glGenFramebuffers();
        
        depthTexHandle = glGenTextures();
        
        glBindTexture(GL_TEXTURE_2D, depthTexHandle);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, textureWidth, textureHeight, 0, GL_DEPTH_COMPONENT, GL_FLOAT, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR); //TODO: add option to change filter type?
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        
        //TODO: show bounds function?
        //float[] borderColor = new float[] {1, 1, 1, 1};
        //glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor);
        
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexHandle, 0);
            glDrawBuffer(GL_NONE);
            glReadBuffer(GL_NONE);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    
    void generate(Vector3f camUp, GLProgram depthProgram, Vector3f worldLightPos, LinkedHashMap<String, Entity> entities) {
        lightProj.setOrtho(-100f, 100f, -100f, 100f, NEAR_PLANE, FAR_PLANE);
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
    
}
