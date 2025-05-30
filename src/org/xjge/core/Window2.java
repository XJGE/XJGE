package org.xjge.core;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.STBI_rgb_alpha;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created: Apr 29, 2021
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Window2 {
    
    private static boolean fullscreen;
    
    private static int width;
    private static int height;
    private static int positionX;
    private static int positionY;
    private static int fboHandle;
    
    private static long handle = NULL;
    
    private static String title;
    private static Monitor monitor;
    private static Resolution resolution;
    
    private static final Viewport[] viewports = new Viewport[4];
    
    private Window2() {}
    
    public static void show(WindowConfig config) {
        if(handle != NULL) {
            Logger.logInfo("The application window is already visible");
            return;
        }
        
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_FOCUS_ON_SHOW, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, config.getResizable() ? GLFW_TRUE : GLFW_FALSE);
        
        /**
         * TODO:
         * - Make call to XJGE.init() before this is used
         * - Add XJGE.init() call to any methods in the hardware class that use GLFW
         * - Search for other cases where implicit initialization might be used
         * - XJGE.init() should be public and static, encourage the API to call it before Window.show()
         */
        fullscreen = config.getFullscreen();
        width      = config.getWidth();
        height     = config.getHeight();
        title      = config.getTitle();
        monitor    = config.getMonitor();
        resolution = config.getResolution();
        
        handle = glfwCreateWindow(width, height, title, NULL, NULL);
        
        if(config.getIconFilename() == null) {
            setIcon("/org/xjge/assets/", "xjge_texture_missing.png");
        } else {
            setIcon(config.getIconFilename());
        }
        
        glfwMakeContextCurrent(handle);
        GL.createCapabilities();
        reconfigure();
        
        for(int i = 0; i < viewports.length; i++) viewports[i] = new Viewport(i, resolution);
        
        fboHandle = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboHandle);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, viewports[0].viewTexHandle, 0);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, viewports[1].viewTexHandle, 0);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT2, GL_TEXTURE_2D, viewports[2].viewTexHandle, 0);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT3, GL_TEXTURE_2D, viewports[3].viewTexHandle, 0);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT4, GL_TEXTURE_2D, viewports[0].bloomTexHandle, 0);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT5, GL_TEXTURE_2D, viewports[1].bloomTexHandle, 0);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT6, GL_TEXTURE_2D, viewports[2].bloomTexHandle, 0);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT7, GL_TEXTURE_2D, viewports[3].bloomTexHandle, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        
        createRenderbuffer();
        ErrorUtils.checkFBStatus(GL_FRAMEBUFFER);
        
        glfwShowWindow(handle);
        
        //TODO: register GLFW callbacks
        
        while(!glfwWindowShouldClose(handle)) {
            //TODO: reimplement game loop
            glfwPollEvents(); //Temp, this will be inside XJGE.loop()
        }
        
        GL.destroy();
        glfwTerminate();
        
        /**
         * Hardware.findMonitors(); //returns list of monitor objects
         * Hardware.findSpeakers(); //returns list of speaker objects
         * Hardware.findGamepads(); //returns list of gamepad objects
         * 
         * XJGE.init("/assets/", "scenes.", debugEnabled);
         * XJGE.setScene(Scene);
         * XJGE.addEvent(Event);
         * 
         * Window.show(WindowConfig);
         * 
         * Window.setSize(0, 0);
         * Window.setPosition(0, 0);
         * Window.setTitle("");
         * Window.setFullscreen(false);
         * Window.setScreenSplitType(ScreenSplitType);
         * Window.setResolution(640, 480);
         * Window.setResizable(false);
         * Window.setMontior(Monitor);
         * Window.setInputMode(CURSOR_NORMAL);
         * Window.center();
         * 
         * Window.getWidth();
         * Window.getHeight();
         * Window.getPositionX();
         * Window.getPositionY();
         * Window.getTitle();
         * Window.getFullscreen();
         * Window.getScreenSplitType();
         * Window.getResolutionWidth();
         * Window.getResolutionHeight();
         * Window.getResizeable();
         * Window.getMonitor();
         * Window.getInputMode();
         * 
         * XJGE methods like setAssetsFilepath(), setScenesFilepath(), and 
         * setDebugModeEnabled() can no longer be called following Window.show()
         * 
         * Game -> XJGE (maintains loop, scene, and config settings)
         * XJGE -> Window (maintains window, viewports, and glfw callbacks)
         */
    }
    
    /**
     * Generates a new renderbuffer object and attaches it to the engines 
     * framebuffer. 
     */
    private static void createRenderbuffer() {
        glBindFramebuffer(GL_FRAMEBUFFER, fboHandle);
        int rboHandle = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rboHandle);
        
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, resolution.width, resolution.height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboHandle);
        
        ErrorUtils.checkGLError();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    
    static void reconfigure() {
        if(fullscreen) {
            width  = monitor.getWidth();
            height = monitor.getHeight();
            
            glfwSetWindowMonitor(handle, monitor.handle, positionX, positionY, 
                                 monitor.getWidth(), monitor.getHeight(), monitor.getRefreshRate());
        } else {
            //width  = (int) Math.round(monitor.getWidth() * 0.6f);
            //height = (int) Math.round(monitor.getHeight() * 0.6f);
            
            glfwSetWindowMonitor(handle, NULL, positionX, positionY, width, height, monitor.getRefreshRate());
        }
        
        center(monitor);
        glfwSwapInterval(true ? 1 : 0);
    }
    
    public static void center(Monitor monitor) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer xPosBuf = stack.mallocInt(1);
            IntBuffer yPosBuf = stack.mallocInt(1);
            
            glfwGetMonitorPos(monitor.handle, xPosBuf, yPosBuf);
            
            positionX = Math.round((monitor.getWidth() - width) / 2) + xPosBuf.get();
            positionY = Math.round((monitor.getHeight() - height) / 2) + yPosBuf.get();
            
            glfwSetWindowPos(handle, positionX, positionY);
        }
    }
    
    private static void setIcon(String filepath, String filename) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            InputStream file = Window.class.getResourceAsStream(filepath + filename);
            byte[] data      = file.readAllBytes();
            
            IntBuffer widthBuf   = stack.mallocInt(1);
            IntBuffer heightBuf  = stack.mallocInt(1);
            IntBuffer channelBuf = stack.mallocInt(1);
            
            ByteBuffer icon = stbi_load_from_memory(
                    stack.malloc(data.length).put(data).flip(),
                    widthBuf,
                    heightBuf,
                    channelBuf,
                    STBI_rgb_alpha);
            
            glfwSetWindowIcon(handle, GLFWImage.malloc(1, stack)
                    .width(widthBuf.get())
                    .height(heightBuf.get())
                    .pixels(icon));
            
            stbi_image_free(icon);
            
        } catch(Exception exception) {
            Logger.logWarning("Failed to change the window icon using \"" + filename +
                              "\" check the filename, path, or extension", exception);
        }
    }
    
    public static void setIcon(String filename) {
        setIcon(XJGE.getAssetsFilepath(), filename);
    }
    
    public static void setTitle(String title) {
        Window2.title = title;
        glfwSetWindowTitle(handle, title);
    }
    
}