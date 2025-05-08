package org.xjge.core;

import org.xjge.graphics.Graphics;
import org.xjge.graphics.GLProgram;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
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
import org.xjge.ui.UI;

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
    Camera prevCamera = new Noclip();
    Camera currCamera = new Noclip();
    Mouse mouse       = new Mouse();
    
    PostProcessShader postProcessShader;
    
    LinkedHashMap<String, Widget> ui = new LinkedHashMap<>();
    
    /**
     * Creates a new viewport object.
     * 
     * @param id the unique number used to identify the viewport in other parts 
     *           of the engine. Corresponds directly with 
     *           {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK} values.
     */
    Viewport(int id) {
        this.id = id;
        
        width  = XJGE.getResolutionX();
        height = XJGE.getResolutionY();
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
        glVertexAttribPointer(2, 2, GL_FLOAT, false, (5 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(2);
    }
    
    /**
     * Renders a scene from the perspective of this viewport. Viewport 
     * rendering is done in three stages: 
     * <ol>
     * <li>The perspective of the camera object used by this viewport is 
     *     rendered.</li>
     * <li>The viewports UI components will be drawn in order of their 
     *     z-positions.</li> 
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
                glPrograms.values().forEach(glProgram -> {
                    if(glProgram.containsUniform("uCamPos")) {
                        glProgram.use();
                        glProgram.setUniform("uCamPos", currCamera.position);
                    }
                });
            }
            
            case "ui" -> {
                currCamera.setOrtho(glPrograms.get("default"), width, height); //TODO: remove once ui has been moved to independent shader
                UI.getInstance().setProjectionMatrix(width, height, Short.MIN_VALUE, Short.MAX_VALUE, projMatrix);
                ui.values().forEach(widget -> widget.render(glPrograms));
                resetCamera(glPrograms);
            }
            
            case "texture" -> {
                if(postProcessShader != null) {
                    postProcessShader.render(viewTexHandle, bloomTexHandle, projMatrix, g);
                } else {
                    glActiveTexture(GL_TEXTURE0);
                    glBindTexture(GL_TEXTURE_2D, viewTexHandle);
                    glActiveTexture(GL_TEXTURE3);
                    glBindTexture(GL_TEXTURE_2D, bloom.textures[1]);
                    glBindVertexArray(g.vao);
                    
                    glPrograms.get("default").use();
                    
                    glPrograms.get("default").setUniform("uType", 0);
                    glPrograms.get("default").setUniform("uTexture", 0);
                    glPrograms.get("default").setUniform("uBloomTexture", 3);
                    glPrograms.get("default").setUniform("uProjection", false, projMatrix);
                    
                    glDrawElements(GL_TRIANGLES, g.indices.capacity(), GL_UNSIGNED_INT, 0);
                    ErrorUtils.checkGLError();
                }
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
        XJGE.glPrograms.values().forEach(glProgram -> {
            if(currCamera.isOrtho) currCamera.setOrtho(glProgram, width, height);
            else                   currCamera.setPerspective(glProgram, width, height);
        });
    }
    
    /**
     * Processes input from the keyboard captured by the game window.
     * 
     * @param key the value supplied by GLFW of a single key on the keyboard
     * @param action an action supplied by GLFW that describes the nature of 
     *               the key press
     * @param mods a value supplied by GLFW denoting whether any mod keys where 
     *             held (such as shift or control)
     */
    void processKeyInput(int key, int action, int mods) {        
        ui.values().forEach(widget -> {
            if(!widget.remove) widget.processKeyInput(key, action, mods);
        });
    }
    
    /**
     * Processes input from the mouse captured by the game window.
     */
    void processMouseInput() {
        ui.values().forEach(widget -> {
            if(!widget.remove) {
                widget.processMouseInput(mouse.cursorPosX, mouse.cursorPosY, 
                                         mouse.button, mouse.action, mouse.mods, 
                                         mouse.scrollX, mouse.scrollY);
            }
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
        
        ui.values().forEach(widget -> {
            widget.relocate(XJGE.getScreenSplit(), width, height);
        });
    }
    
    /**
     * Adds a new {@link Widget} to this viewport. Widgets will be rendered in 
     * the order of their z-positions with lower numbers denoting a higher 
     * priority. For example, a component with a z-position of 0 will be 
     * rendered in front of a component with a z-position of 1.
     * 
     * @param name the name that will be used to identify/remove the widget
     * @param widget the widget to add
     */
    void addUIWidget(boolean debugEnabled, String name, Widget widget) {
        if(debugEnabled) {
            Logger.logInfo("Added widget \"" + name + "\" to viewport " + id);
        }
        
        ui.put(name, widget);
        
        List<Map.Entry<String, Widget>> widgetList = new LinkedList<>(ui.entrySet());
        
        Collections.sort(widgetList, (var o1, var o2) -> {
            return (o2.getValue()).compareTo(o1.getValue());
        });
        
        var temp = new LinkedHashMap<String, Widget>();
        widgetList.forEach(comp2 -> temp.put(comp2.getKey(), comp2.getValue()));
        
        ui.clear();
        ui.putAll(temp);
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
    void applyBloom(GLProgram blurProgram) {
        boolean firstPass  = true;
        boolean horizontal = true;
        int blurWeight = 10;

        for(int i = 0; i < blurWeight; i++) {
            int value     = (horizontal) ? 1 : 0;
            int invValue  = (horizontal) ? 0 : 1;
            int texHandle = bloomTexHandle;

            glBindFramebuffer(GL_FRAMEBUFFER, bloom.fbos[invValue]);
            bloom.applyBlur(blurProgram, (firstPass) ? texHandle : bloom.textures[value], horizontal);

            horizontal = !horizontal;
            if(firstPass) firstPass = false;
        }
        
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    
}