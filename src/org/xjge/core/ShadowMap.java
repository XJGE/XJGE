package org.xjge.core;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import org.xjge.graphics.GLProgram;

//Created: Nov 9, 2021

/**
 * Shadow maps generate a 2D texture that is then projected over the scene to 
 * create the appearance of dynamic shadows.
 * <p>
 * More specifically, the shadow map projects a frustum from the position of 
 * the global light source towards the scenes origin point (0, 0, 0) and renders 
 * the scene an extra time. The texture created by this process is then 
 * formatted as a depth attachment within a framebuffer object.
 * <p>
 * NOTE: Entities that wish to produce shadows can do so by supplying their 
 * model matrix (and texture if necessary) to the depth shader program made 
 * accessible through their 
 * {@linkplain Entity#renderShadow(org.xjge.graphics.GLProgram) renderShadow()} 
 * methods.
 * 
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
    
    /**
     * Creates a new shadow map object using the settings specified. The 
     * settings listed here can be used to combat issues such as peter-panning 
     * and shadow acne.
     * 
     * @param minBias         a small value indicating the minimum amount of 
     *                        shadow bias to offset the depth of all surfaces by
     * @param maxBias         a small value indicating the maximum amount of 
     *                        shadow bias to offset the depth of all surfaces by
     * @param frustumSize     the size of the area the shadow map will attempt 
     *                        to cover. Values may not correspond perfectly to 
     *                        world space.
     * @param range           the maximum distance or depth of the shadow maps 
     *                        influence
     * @param useLinearFilter if true, the texture will exhibit linear 
     *                        filtering otherwise nearest neighbor filtering 
     *                        will be used
     * @param textureWidth    the width of the shadow map texture (in pixels)
     * @param textureHeight   the height of the shadow map texture (in pixels)
     * @param showBounds      if true, the edges of the shadow texture will be 
     *                        darkened. This is useful in instances where the 
     *                        effected area of the shadow map needs to be 
     *                        exposed for debugging purposes
     * @param PCFValue        indicates how much to blur the edges of shadows. 
     *                        Passing a value of zero will produce hard edges 
     *                        whereas higher numbers will apply the filter more.
     */
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
    
    /**
     * Creates a new shadow map object using the default settings provided by 
     * the engine.
     */
    public ShadowMap() {
        this(0.00003f, 0.0009f, 100f, 100f, true, 1920, 1920, false, 1);
    }
    
    /**
     * Generates a 2D texture containing every shadow produced by each entity
     * in the current scene. This method is called automatically by the engine.
     * 
     * @param camUp         the direction considered upwards relative to the 
     *                      viewports camera
     * @param depthProgram  the shader program provided by the engine that will 
     *                      be used to generate the shadow map texture
     * @param worldLightPos the current position of the global light source
     */
    void generate(Vector3f camUp, GLProgram depthProgram, Scene scene) {
        lightProj.setOrtho(-frustumSize, frustumSize, -frustumSize, frustumSize, 1f, range);
        lightView.setLookAt(scene.lights[0].position, lightDir, camUp);
        lightProj.mul(lightView, lightSpace);
        
        depthProgram.use();
        depthProgram.setUniform("uLightSpace", false, lightSpace);
        
        glViewport(0, 0, textureWidth, textureHeight);
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
            glClear(GL_DEPTH_BUFFER_BIT);
            glBindTexture(GL_TEXTURE_2D, depthTexHandle);
            scene.renderShadows(depthProgram);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    
    /**
     * Updates the parameters of the shadow maps texture.
     * 
     * @param useLinearFilter if true, the texture will exhibit linear 
     *                        filtering otherwise nearest neighbor filtering 
     *                        will be used
     * @param textureWidth    the width of the shadow map texture (in pixels)
     * @param textureHeight   the height of the shadow map texture (in pixels)
     */
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
    
    /**
     * Sets the dimensions of the texture the shadow map will generate.
     * 
     * @param width  the width of the shadow map texture (in pixels)
     * @param height the height of the shadow map texture (in pixels)
     */
    public void setTextureSize(int width, int height) {
        updateTexture(useLinearFilter, width, height);
    }
    
    /**
     * Sets the type of filtering (linear or nearest neighbor) the shadow maps
     * texture will use.
     * 
     * @param useLinearFilter if true, the texture will exhibit linear 
     *                        filtering otherwise nearest neighbor filtering 
     *                        will be used
     */
    public void setTextureFilter(boolean useLinearFilter) {
        updateTexture(useLinearFilter, textureWidth, textureHeight);
    }
    
    /**
     * Sets whether the areas beyond the shadow maps frustum are darkened or 
     * not. It's often useful to expose the edges of the shadow maps influence 
     * for debugging purposes.
     * 
     * @param value if true, the edges beyond the shadow maps influence will be 
     *              darkened
     */
    public void setShowBounds(boolean value) {
        float[] borderColor = (value) 
                            ? new float[] {0, 0, 0, 0} 
                            : new float[] {1, 1, 1, 1};
        
        glBindTexture(GL_TEXTURE_2D, depthTexHandle);
            glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor);
        glBindTexture(GL_TEXTURE_2D, 0);
    }
    
}
