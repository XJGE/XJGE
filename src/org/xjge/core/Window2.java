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
import static org.xjge.core.SplitScreenType.*;

/**
 * Created: Apr 29, 2021
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Window2 {
    
    private static boolean fullscreen;
    
    static final int DEFAULT_WIDTH  = 1280;
    static final int DEFAULT_HEIGHT = 720;
    
    private static int width;
    private static int height;
    private static int positionX;
    private static int positionY;
    private static int fboHandle;
    
    private static long handle = NULL;
    
    private static String title;
    private static Monitor monitor;
    private static Resolution resolution;
    
    private static SplitScreenType splitType = SplitScreenType.NONE;
    
    private static final Viewport[] viewports = new Viewport[4];
    
    private Window2() {}
    
    public static void show(WindowConfig config) {
        if(handle != NULL) {
            Logger.logInfo("The applications window is already visible");
            return;
        }
        
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_FOCUS_ON_SHOW, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, config.getResizable() ? GLFW_TRUE : GLFW_FALSE);
        
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
        
        glfwSetMonitorCallback((monitorHandle, event) -> {
            var monitors = Hardware2.findMonitors();
            
            if(monitors.isEmpty()) {
                //TODO: handle scenario where no monitor is available
            } else {
                Monitor eventMonitor = Hardware2.getMonitor(monitorHandle);
                
                if(event == GLFW_CONNECTED) {
                    Logger.logInfo("New monitor \"" + eventMonitor.name + "\" connected at index " + eventMonitor.index);
                } else if(event == GLFW_DISCONNECTED) {
                    if(monitor.handle == monitorHandle) {
                        Logger.logWarning("The current monitor used by the applications window has been disconnected. " + 
                                          "(name: \" " + eventMonitor.name + "\" index: " + eventMonitor.index + ") " + 
                                          "Attempting to move the window to the next available monitor...", 
                                          null);
                    } else {
                        Logger.logInfo("The monitor \"" + eventMonitor.name + "\" at index " + 
                                       eventMonitor.index + " has been disconnected");
                    }
                }
                
                setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
                setFullscreen(false);
                setMonitor(monitors.get(0));
            }
        });
        
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
         * Window.reconfigure();
         * Window.center();
         * 
         * Window.setFullscreen(false);
         * Window.setSize(0, 0);
         * Window.setPosition(0, 0);
         * Window.setResolution(640, 480);
         * Window.setTitle("");
         * Window.setIcon("");
         * Window.setScreenSplitType(ScreenSplitType);
         * Window.setMontior(Monitor);
         * Window.setInputMode(CURSOR_NORMAL);
         * 
         * Window.getFullscreen();
         * Window.getWidth();
         * Window.getHeight();
         * Window.getPositionX();
         * Window.getPositionY();'
         * Window.getResolutionWidth();
         * Window.getResolutionHeight();
         * Window.getTitle();
         * Window.getScreenSplitType();
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
    
    /**
     * Closes the applications window and gracefully ceases execution.
     */
    public static void close() {
        glfwSetWindowShouldClose(handle, true);
    }
    
    public static void setFullscreen(boolean fullscreen) {
        Window2.fullscreen = fullscreen;
        reconfigure();
    }
    
    public static void setSize(int width, int height) {
        if(width <= 0 || height <= 0) {
            Logger.logInfo("Invalid window size used (" + width + ", " + height + 
                           ") values passed must be greater than zero");
            return;
        }
        
        Window2.width  = width;
        Window2.height = height;
        
        glfwSetWindowSize(handle, Window2.width, Window2.height);
    }
    
    public static void setPosition(int positionX, int positionY) {
        Window2.positionX = positionX;
        Window2.positionY = positionY;
        
        glfwSetWindowPos(handle, positionX, positionY);
    }
    
    public static void setResolution(int width, int height) {
        if(width <= 0 || height <= 0) {
            Logger.logInfo("Invalid window resolution used (" + width + ", " + 
                           height + ") values passed must be greater than zero");
            return;
        }
        
        resolution.width = width;
        resolution.width = height;
    }
    
    public static void setTitle(String title) {
        if(title == null) {
            Logger.logInfo("Window title cannot be null");
            return;
        }
        
        Window2.title = title;
        
        glfwSetWindowTitle(handle, title);
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
    
    public static void setSplitScreenType(SplitScreenType splitType) {
        Window2.splitType = splitType;
        
        for(Viewport viewport : viewports) {
            switch(splitType) {
                case NONE -> {
                    viewport.active = (viewport.id == 0);
                    viewport.setBounds(resolution.width, resolution.height, 
                                       0, 0, 
                                       Window.getWidth(), Window.getHeight());
                }
                
                case HORIZONTAL -> {
                    viewport.active = (viewport.id == 0 || viewport.id == 1);
                    switch(viewport.id) {
                        case 0 -> viewport.setBounds(
                                    resolution.width, resolution.height / 2,
                                    0, Window.getHeight() / 2, 
                                    Window.getWidth(), Window.getHeight() / 2);
                            
                        case 1 -> viewport.setBounds(
                                    resolution.width, resolution.height / 2,
                                    0, 0, 
                                    Window.getWidth(), Window.getHeight() / 2);
                        
                        default -> viewport.setBounds(resolution.width, resolution.height, 0, 0, 0, 0);
                    }
                }
                
                case VERTICAL -> {
                    viewport.active = (viewport.id == 0 || viewport.id == 1);
                    switch(viewport.id) {
                        case 0 -> viewport.setBounds(
                                    resolution.width / 2, resolution.height,
                                    0, 0, 
                                    Window.getWidth() / 2, Window.getHeight());
                            
                        case 1 -> viewport.setBounds(
                                    resolution.width / 2, resolution.height,
                                    Window.getWidth() / 2, 0, 
                                    Window.getWidth() / 2, Window.getHeight());
                        
                        default -> viewport.setBounds(resolution.width, resolution.height, 0, 0, 0, 0);
                    }
                }
                
                case TRISECT -> {
                    viewport.active = (viewport.id != 3);
                    switch(viewport.id) {
                        case 0 -> viewport.setBounds(
                                    resolution.width / 2, resolution.height / 2,
                                    0, Window.getHeight() / 2, 
                                    Window.getWidth() / 2, Window.getHeight() / 2);
                            
                        case 1 -> viewport.setBounds(
                                    resolution.width / 2, resolution.height / 2,
                                    Window.getWidth() / 2, Window.getHeight() / 2, 
                                    Window.getWidth() / 2, Window.getHeight() / 2);
                            
                        case 2 -> viewport.setBounds(
                                    resolution.width / 2, resolution.height / 2,
                                    Window.getWidth() / 4, 0, 
                                    Window.getWidth() / 2, Window.getHeight() / 2);
                        
                        default -> viewport.setBounds(resolution.width, resolution.height, 0, 0, 0, 0);
                    }
                }
                
                case QUARTER -> {
                    viewport.active = true;
                    switch(viewport.id) {
                        case 0 -> viewport.setBounds(
                                    resolution.width / 2, resolution.height / 2,
                                    0, Window.getHeight() / 2, 
                                    Window.getWidth() / 2, Window.getHeight() / 2);
                            
                        case 1 -> viewport.setBounds(
                                    resolution.width / 2, resolution.height / 2,
                                    Window.getWidth() / 2, Window.getHeight() / 2, 
                                    Window.getWidth() / 2, Window.getHeight() / 2);
                            
                        case 2 -> viewport.setBounds(
                                    resolution.width / 2, resolution.height / 2,
                                    0, 0, 
                                    Window.getWidth() / 2, Window.getHeight() / 2);
                            
                        case 3 -> viewport.setBounds(
                                    resolution.width / 2, resolution.height / 2,
                                    Window.getWidth() / 2, 0, 
                                    Window.getWidth() / 2, Window.getHeight() / 2);
                    }
                }
            }
        }
    }
    
    public static void setMonitor(Monitor monitor) {
        Window.monitor = monitor;
        reconfigure();
    }
    
    public static void setInputMode(int mode, int value) {
        //TODO: add validation?
        glfwSetInputMode(handle, mode, value);
    }
    
}