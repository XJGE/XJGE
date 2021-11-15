package org.xjge.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import org.xjge.core.Camera;
import org.xjge.core.Scene;

/**
 * Sep 7, 2021
 */

/**
 * 
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public abstract class ShadowMap {

    protected final int width;
    protected final int height;
    protected final int fbo;
    protected final int texHandle;
    
    protected final float nearPlane;
    protected final float farPlane;
    
    protected final Vector3f lightPosition  = new Vector3f();
    protected final Vector3f lightDirection = new Vector3f();
    
    protected final Matrix4f lightView  = new Matrix4f();
    protected final Matrix4f lightProj  = new Matrix4f();
    protected final Matrix4f lightSpace = new Matrix4f();
    
    public ShadowMap(int width, int height, float nearPlane, float farPlane) {
        this.width     = width;
        this.height    = height;
        this.nearPlane = nearPlane;
        this.farPlane  = farPlane;
        
        fbo       = glGenFramebuffers();
        texHandle = glGenTextures();
    }
    
    public abstract void createMap(Camera camera, GLProgram glProgram, Scene scene);
    
}