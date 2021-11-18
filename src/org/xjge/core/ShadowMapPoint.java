package org.xjge.core;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import org.xjge.graphics.GLProgram;

/**
 * Nov 17, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class ShadowMapPoint {

    private final int SHADOW_WIDTH  = 1024;
    private final int SHADOW_HEIGHT = 1024;
    private final int fbo;
    
    public final int depthTexHandle;
    
    private float aspect;
    private float NEAR_PLANE = 1f;
    private float FAR_PLANE  = 25f;
    
    private Vector3f[] tempVec = new Vector3f[12];
    
    private final Matrix4f[] shadowTransforms = new Matrix4f[6];
    
    private final Matrix4f[] lightView = new Matrix4f[6];
    private final Matrix4f lightProj   = new Matrix4f();
    final Matrix4f lightSpace           = new Matrix4f();
    
    ShadowMapPoint() {
        depthTexHandle = glGenTextures();
        
        glBindTexture(GL_TEXTURE_CUBE_MAP, depthTexHandle);
        for(int f = 0; f < 6; f++) {
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + f, 0, GL_DEPTH_COMPONENT, 
                         SHADOW_WIDTH, SHADOW_HEIGHT, 0, GL_DEPTH_COMPONENT, GL_FLOAT, NULL);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
            
            lightView[f] = new Matrix4f();
            tempVec[f]   = new Vector3f();
        }
        
        fbo = glGenFramebuffers();
        
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
            glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, depthTexHandle, 0);
            glDrawBuffer(GL_NONE);
            glReadBuffer(GL_NONE);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        
        aspect = (float) (SHADOW_WIDTH / SHADOW_HEIGHT);
    }
    
    void createMap(GLProgram depthProgram, Scene scene, LightSource light) {
        lightProj.setPerspective((float) Math.toRadians(90f), aspect, NEAR_PLANE, FAR_PLANE);
        lightProj.mul(lightView[0].lookAt(light.getPosition(), light.getPosition().add( 1, 0, 0, tempVec[0]),  tempVec[1].set(0, -1, 0)), shadowTransforms[0]);
        lightProj.mul(lightView[1].lookAt(light.getPosition(), light.getPosition().add(-1, 0, 0, tempVec[2]),  tempVec[3].set(0, -1, 0)), shadowTransforms[1]);
        lightProj.mul(lightView[2].lookAt(light.getPosition(), light.getPosition().add(0,  1, 0, tempVec[4]),  tempVec[5].set(0, 0,  1)), shadowTransforms[2]);
        lightProj.mul(lightView[3].lookAt(light.getPosition(), light.getPosition().add(0, -1, 0, tempVec[6]),  tempVec[7].set(0, 0, -1)), shadowTransforms[3]);
        lightProj.mul(lightView[4].lookAt(light.getPosition(), light.getPosition().add(0, 0,  1, tempVec[8]),  tempVec[9].set(0, -1, 0)), shadowTransforms[4]);
        lightProj.mul(lightView[5].lookAt(light.getPosition(), light.getPosition().add(0, 0, -1, tempVec[10]), tempVec[11].set(0, -1, 0)), shadowTransforms[5]);
        
        depthProgram.use();
        depthProgram.setUniform("uLightSpace", false, lightSpace);
        
        glViewport(0, 0, SHADOW_WIDTH, SHADOW_HEIGHT);
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
            glClear(GL_DEPTH_BUFFER_BIT);
            glBindTexture(GL_TEXTURE_CUBE_MAP, depthTexHandle);
            scene.renderShadows(depthProgram);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    
}