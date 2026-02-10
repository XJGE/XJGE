package org.xjge.core;

import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Graphics;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_2;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_3;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_4;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;
import org.xjge.graphics.PostProcessShader;

/**
 * Created: May 11, 2021
 * <p>
 * Represents a rectangular section of the game window that serves as the players
 * viewpoint.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class Viewport {

    final int id;
    final int viewTexHandle;
    final int bloomTexHandle;
    int width;
    int height;
    
    private final int[] colorAttachments = new int[2];
    
    boolean active;
    
    private Graphics g = new Graphics();
    private Bloom bloom;
    
    Vector2i botLeft  = new Vector2i();
    Vector2i topRight = new Vector2i();
    Camera prevCamera = new Noclip(this);
    Camera currCamera = new Noclip(this);
    
    PostProcessShader postProcessShader;
    
    /**
     * Creates a new viewport object.
     * 
     * @param id the unique number used to identify the viewport in other parts 
     *           of the engine. Corresponds directly with 
     *           {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK} values.
     */
    Viewport(int id, Resolution resolution) {
        this.id = id;
        
        width  = resolution.width;
        height = resolution.height;
        bloom  = new Bloom(width, height);
        
        viewTexHandle  = glGenTextures();
        bloomTexHandle = bloom.textures[2];
        
        createTextureAttachment();
        
        active = (id == 0);
        
        switch(id) {
            case GLFW_JOYSTICK_1 -> {
                colorAttachments[0] = GL_COLOR_ATTACHMENT0;
                colorAttachments[1] = GL_COLOR_ATTACHMENT4;
            }
            case GLFW_JOYSTICK_2 -> {
                colorAttachments[0] = GL_COLOR_ATTACHMENT1;
                colorAttachments[1] = GL_COLOR_ATTACHMENT5;
            }
            case GLFW_JOYSTICK_3 -> {
                colorAttachments[0] = GL_COLOR_ATTACHMENT2;
                colorAttachments[1] = GL_COLOR_ATTACHMENT6;
            }
            case GLFW_JOYSTICK_4 -> {
                colorAttachments[0] = GL_COLOR_ATTACHMENT3;
                colorAttachments[1] = GL_COLOR_ATTACHMENT7;
            }
        }
    }
    
    /**
     * Creates a new OpenGL texture object to be attached to the engines 
     * framebuffer.
     */
    private void createTextureAttachment() {
        glBindTexture(GL_TEXTURE_2D, viewTexHandle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glBindTexture(GL_TEXTURE_2D, 0);
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(20);
            g.indices  = stack.mallocInt(6);
            
            //(vec3 position), (vec2 texCoords)
            g.vertices.put(0)    .put(height).put(0)    .put(1).put(1);
            g.vertices.put(width).put(height).put(0)    .put(0).put(1);
            g.vertices.put(width).put(0)     .put(0)    .put(0).put(0);
            g.vertices.put(0)    .put(0)     .put(0)    .put(1).put(0);
            
            g.indices.put(0).put(1).put(2);
            g.indices.put(3).put(2).put(0);
            
            g.vertices.flip();
            g.indices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (5 * Float.BYTES), 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, (5 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
    }
    
    private void attachColorTargets() {
         glBindFramebuffer(GL_FRAMEBUFFER, Window.getFBOHandle());
            glFramebufferTexture2D(GL_FRAMEBUFFER, colorAttachments[0], GL_TEXTURE_2D, viewTexHandle, 0);
            glFramebufferTexture2D(GL_FRAMEBUFFER, colorAttachments[1], GL_TEXTURE_2D, bloomTexHandle, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    
    /**
     * Renders a scene from the perspective of this viewport. Viewport 
     * rendering is done in three stages: 
     * <ol>
     * <li>The perspective of the camera object used by this viewport is 
     *     rendered.</li>
     * <li>The viewports UIManager components will be drawn in order of their 
     z-positions.</li> 
     * <li>The texture attachment associated with this viewport by the engines 
     *     framebuffer will be updated to reflect the changes made in the 
     *     previous two steps.</li>
     * </ol>
     * 
     * @param glPrograms an immutable collection containing the shader programs 
     *                   compiled during startup
     * @param stage the stage denoting the viewports current render pass
     * @param projMatrix the projection matrix managed internally by the engine
     */
    void render(Map<String, GLProgram> glPrograms, String stage, Matrix4f projMatrix) {
        switch(stage) {
            case "camera" -> {
                currCamera.render(glPrograms);
                ShaderViewport.getInstance().use();
                ShaderViewport.getInstance().setUniform("uProjection", currCamera.projMatrix);
            }
            
            case "texture" -> {
                if(postProcessShader != null) {
                    postProcessShader.render(viewTexHandle, bloomTexHandle, projMatrix, g);
                } else {
                    glActiveTexture(GL_TEXTURE0);
                    glBindTexture(GL_TEXTURE_2D, viewTexHandle);
                    glActiveTexture(GL_TEXTURE1);
                    glBindTexture(GL_TEXTURE_2D, bloom.textures[1]);
                    glBindVertexArray(g.vao);
                    
                    ShaderViewport.getInstance().use();
                    ShaderViewport.getInstance().setUniform("uTexture", 0);
                    ShaderViewport.getInstance().setUniform("uBloomTexture", 1);
                    ShaderViewport.getInstance().setUniform("uProjection", projMatrix);
                    
                    glDrawElements(GL_TRIANGLES, g.indices.capacity(), GL_UNSIGNED_INT, 0);
                    ErrorUtils.checkGLError();
                }
            }
            
            case "ui" -> {
                UIManager.updateProjectionMatrix(width, height, Short.MIN_VALUE, Short.MAX_VALUE);
                UIManager.renderWidgets(id, glPrograms);
                resetCamera(glPrograms); //TODO: is this even necessary now since the projection matrix is handled by UIManager?
            }
        }
    }
    
    /**
     * Convenience method used to revert the viewports camera projection matrix 
     * back to whatever projection type (orthogonal or perspective) it was 
     * using before.
     * 
     * @param glPrograms an immutable collection containing the shader programs 
     *                   compiled during startup
     */
    void resetCamera(Map<String, GLProgram> glPrograms) {
        glPrograms.values().forEach(glProgram -> {
            if(currCamera.isOrtho) currCamera.setOrtho(glProgram, width, height);
            else                   currCamera.setPerspective(glProgram, width, height);
        });
    }
    
    /**
     * Sets the resolution and position of the viewport inside the game window.
     * 
     * @param width the width of the viewport in pixels
     * @param height the height of the viewport in pixels
     * @param x1 the x-coordinate of the viewports bottom-left corner
     * @param y1 the y-coordinate of the viewports bottom-left corner
     * @param x2 the x-coordinate of the viewports top-right corner
     * @param y2 the y-coordinate of the viewports top-right corner
     */
    void setBounds(int width, int height, int x1, int y1, int x2, int y2) {
        this.width  = width;
        this.height = height;
        botLeft.set(x1, y1);
        topRight.set(x2, y2);
        
        createTextureAttachment();
        bloom.createTextureAttachments(width, height);
        attachColorTargets();
        
        UIManager.relocateWidgets(id, width, height);
    }
    
    /**
     * Binds one or both color buffer texture attachments associated with this
     * viewports framebuffer.
     * 
     * @param both if true, both color attachments will be drawn into
     */
    void bindDrawBuffers(boolean both) {
        if(both) glDrawBuffers(colorAttachments);
        else     glDrawBuffer(colorAttachments[0]);
    }
    
    /**
     * Calculates the output of a Gaussian style blur on the framebuffer texture 
     * generated by the bloom effect.
     * 
     * @param blurProgram the shader program used to apply a Gaussian blur to
     *                    the bloom framebuffer texture 
     */
    void applyBloom() {
        boolean firstPass  = true;
        boolean horizontal = true;
        int blurWeight = 10;

        for(int i = 0; i < blurWeight; i++) {
            int value     = (horizontal) ? 1 : 0;
            int invValue  = (horizontal) ? 0 : 1;
            int texHandle = bloomTexHandle;

            glBindFramebuffer(GL_FRAMEBUFFER, bloom.fbos[invValue]);
            bloom.applyBlur((firstPass) ? texHandle : bloom.textures[value], horizontal);

            horizontal = !horizontal;
            if(firstPass) firstPass = false;
        }
        
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    
}