package org.xjge.graphics;

import org.joml.Matrix4f;


//Created: May 16, 2022

/**
 * These objects are used to apply post-processing effects to the framebuffer 
 * texture of a viewport. They achieve this by changing which shader program the 
 * framebuffer object will use during its rendering cycle.
 * <p>
 * NOTE: When defining a shaders for post processing effects take care to make 
 * sure the vertex layout is organized correctly. More specifically the vertex 
 * layout for a framebuffer texture includes a vec3 at location 0 (for its 
 * position) and a vec2 at location 2 (for its texture coordinates). It should
 * look a little something like this;
 * <blockquote><pre>
 * layout (location = 0) in vec3  aPosition;
 * layout (location = 2) in vec2  aTexCoords;
 * ...
 * </pre></blockquote>
 * <p>
 * Additionally, because framebuffers write to texture objects any shader program that 
 * wishes to apply post-processing effects should contain (at minimum) the 
 * following code;
 * 
 * <blockquote><pre>
 * //Vertex shader
 * ...
 * ioTexCoords = aTexCoords;
 * gl_Position = uProjection * vec4(aPosition, 1);
 * ...
 * 
 * //Fragment shader
 * ...
 * vec2 vRes = textureSize(uTexture, 0);
 *
 * vec3 sceneColor = texture(uTexture, vec2(
 *     sharpen(ioTexCoords.x * vRes.x) / vRes.x,
 *     sharpen(ioTexCoords.y * vRes.y) / vRes.y
 * )).rgb;
 * 
 * ioFragColor = vec4(sceneColor, 1);
 * ...
 * 
 * //The sharpen method helps retain a pixelated look at odd resolutions:
 * ...
 * float sharpen(float pixArray) {
 *     float normal  = (fract(pixArray) - 0.5) * 2.0;
 *     float normal2 = normal * normal;
 * 
 *     return floor(pixArray) + normal * pow(normal2, 2.0) / 2.0 + 0.5;
 * }
 * ...
 * </pre></blockquote>
 * 
 * @author J Hoffman
 * @since  2.1.9
 */
public abstract class PostProcessShader {

    public final GLProgram glProgram;
    
    /**
     * Creates a new object that can be used to reroute the render cycle of a 
     * viewport object and apply post-processing effects to its framebuffer 
     * texture.
     * 
     * @param glProgram the shader program that will be used to render the 
     *                  framebuffer texture
     */
    public PostProcessShader(GLProgram glProgram) {
        this.glProgram = glProgram;
    }
    
    /**
     * Acts like any other render method does within the engine with the 
     * exception that it runs 
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
    public abstract void render(int viewTexHandle, int bloomTexHandle, Matrix4f projMatrix, Graphics g);
    
}