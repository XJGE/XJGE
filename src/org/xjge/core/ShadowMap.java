package org.xjge.core;

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
 * @since  
 */
public class ShadowMap {

    private final int SHADOW_WIDTH  = 2560;
    private final int SHADOW_HEIGHT = 2560;
    private final int fbo;
    final int depthTexHandle;
    
    private final float NEAR_PLANE = 1f;
    private final float FAR_PLANE  = 100f;
    
    private final Vector3f lightDir = new Vector3f();
    
    private final Matrix4f lightView = new Matrix4f();
    private final Matrix4f lightProj = new Matrix4f();
    final Matrix4f lightSpace        = new Matrix4f();
    
    ShadowMap() {
        fbo = glGenFramebuffers();
        
        depthTexHandle = glGenTextures();
        
        glBindTexture(GL_TEXTURE_2D, depthTexHandle);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, SHADOW_WIDTH, SHADOW_HEIGHT, 0, GL_DEPTH_COMPONENT, GL_FLOAT, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        float[] borderColor = new float[] {1, 1, 1, 1};
        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor);
        
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexHandle, 0);
            glDrawBuffer(GL_NONE);
            glReadBuffer(GL_NONE);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    
    void createMap(Vector3f camUp, GLProgram depthProgram, Scene scene) {
        lightProj.setOrtho(-100f, 100f, -100f, 100f, NEAR_PLANE, FAR_PLANE);
        lightView.setLookAt(scene.getLightSources()[0].getPosition(), lightDir, camUp);
        lightProj.mul(lightView, lightSpace);
        
        depthProgram.use();
        depthProgram.setUniform("uLightSpace", false, lightSpace);
        
        glViewport(0, 0, SHADOW_WIDTH, SHADOW_HEIGHT);
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
            glClear(GL_DEPTH_BUFFER_BIT);
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, depthTexHandle);
            scene.renderShadows(depthProgram);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    
}
