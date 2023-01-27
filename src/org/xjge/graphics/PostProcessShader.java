package org.xjge.graphics;

import org.joml.Matrix4f;

//Created: May 16, 2022

/**
 * These objects are used to apply effects to the framebuffer texture of a 
 * viewport. They achieve this by supplementing the existing functionality of 
 * the default shader program by providing entry points before and after the 
 * texture render call of the viewport.
 * <p>
 * NOTE: When defining shaders for post processing effects take care to ensure
 * that the vertex layout is correct. (specifically this consists of a vec3 and 
 * vec2 at positions 0 and 2 for the framebuffer textures position and texture 
 * coordinates respectively). Additional data that may be used for uniform 
 * variables is provided as parameters in the render functions of this class.
 * 
 * @author J Hoffman
 * @since  2.1.9
 */
public abstract class PostProcessShader {

    public final GLProgram glProgram;
    
    /**
     * Creates an object that can be used to manipulate the output of a 
     * viewports framebuffer texture both before and after its render cycle.
     * 
     * @param glProgram the shader program that will be used to render the 
     *                  framebuffer texture
     */
    public PostProcessShader(GLProgram glProgram) {
        this.glProgram = glProgram;
    }
    
    /**
     * Acts like any other render method does within the engine with the 
     * exception that it runs <i>before</i> a viewports framebuffer texture is 
     * processed. 
     * 
     * @param viewTexHandle  the handle of the framebuffer texture containing
     *                       color information
     * @param bloomTexHandle the handle of another framebuffer texture generated
     *                       by the engine when bloom effects are enabled
     * @param projMatrix     A temporary projection matrix used by the engine. 
     *                       You'll want to pass this to your custom post-process 
     *                       shader during the vertex stage.
     * @param g              the graphics object used by the viewport
     */
    public abstract void renderBefore(int viewTexHandle, int bloomTexHandle, Matrix4f projMatrix, Graphics g);
    
    /**
     * Acts like any other render method does within the engine with the 
     * exception that it runs <i>after</i> a viewports framebuffer texture is 
     * processed.
     * 
     * @param viewTexHandle  the handle of the framebuffer texture containing
     *                       color information
     * @param bloomTexHandle the handle of another framebuffer texture generated
     *                       by the engine when bloom effects are enabled
     * @param projMatrix     A temporary projection matrix used by the engine. 
     *                       You'll want to pass this to your custom post-process 
     *                       shader during the vertex stage.
     * @param g              the graphics object used by the viewport
     */
    public abstract void renderAfter(int viewTexHandle, int bloomTexHandle, Matrix4f projMatrix, Graphics g);
    
}