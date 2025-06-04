package org.xjge.core;

import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWErrorCallback;
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
    
    static final int DEFAULT_WIDTH  = 1280;
    static final int DEFAULT_HEIGHT = 720;
    
    private static int fboHandle;
    
    private static long handle = NULL;
    
    private static final WindowData data = new WindowData();
    
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
        
        data.fullscreen = config.getFullscreen();
        data.width      = config.getWidth();
        data.height     = config.getHeight();
        data.title      = config.getTitle();
        data.monitor    = config.getMonitor();
        data.resolution = config.getResolution();
        
        handle = glfwCreateWindow(data.width, data.height, data.title, NULL, NULL);
        
        if(config.getIconFilename() == null) {
            setIcon("/org/xjge/assets/", "xjge_texture_missing.png");
        } else {
            setIcon(config.getIconFilename());
        }
        
        glfwMakeContextCurrent(handle);
        GL.createCapabilities();
        reconfigure();
        
        for(int i = 0; i < viewports.length; i++) viewports[i] = new Viewport(i, data.resolution);
        
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
        
        glfwSetErrorCallback((error, description) -> {
            Logger.logWarning("GLFW Error: (" + error + ") " + GLFWErrorCallback.getDescription(description), null);
        });
        
        glfwSetMonitorCallback((monitorHandle, event) -> {
            var monitors = Hardware2.findMonitors();
            
            if(monitors.isEmpty()) {
                //TODO: handle scenario where no monitor is available
            } else {
                Monitor eventMonitor = Hardware2.getMonitor(monitorHandle);
                
                if(event == GLFW_CONNECTED) {
                    Logger.logInfo("New monitor \"" + eventMonitor.name + "\" connected at index " + eventMonitor.index);
                } else if(event == GLFW_DISCONNECTED) {
                    if(data.monitor.handle == monitorHandle) {
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
        
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, data.resolution.width, data.resolution.height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboHandle);
        
        ErrorUtils.checkGLError();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    
    static void reconfigure() {
        if(data.fullscreen) {
            data.width  = data.monitor.getWidth();
            data.height = data.monitor.getHeight();
            
            glfwSetWindowMonitor(handle, data.monitor.handle, data.positionX, data.positionY, 
                                 data.monitor.getWidth(), data.monitor.getHeight(), data.monitor.getRefreshRate());
        } else {
            glfwSetWindowMonitor(handle, NULL, data.positionX, data.positionY, 
                                 data.width, data.height, data.monitor.getRefreshRate());
        }
        
        center(data.monitor);
        glfwSwapInterval(true ? 1 : 0);
    }
    
    public static void center(Monitor monitor) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer xPosBuf = stack.mallocInt(1);
            IntBuffer yPosBuf = stack.mallocInt(1);
            
            glfwGetMonitorPos(monitor.handle, xPosBuf, yPosBuf);
            
            data.positionX = Math.round((monitor.getWidth() - data.width) / 2) + xPosBuf.get();
            data.positionY = Math.round((monitor.getHeight() - data.height) / 2) + yPosBuf.get();
            
            glfwSetWindowPos(handle, data.positionX, data.positionY);
        }
    }
    
    /**
     * Closes the applications window and gracefully ceases execution.
     */
    public static void close() {
        glfwSetWindowShouldClose(handle, true);
    }
    
    public static void addObserver(PropertyChangeListener observer) {
        data.observable.addObserver(observer);
    }
    
    public static void removeObserver(PropertyChangeListener observer) {
        data.observable.removeObserver(observer);
    }
    
    public static void setFullscreen(boolean fullscreen) {
        data.fullscreen = fullscreen;
        reconfigure();
    }
    
    public static void setSize(int width, int height) {
        if(width <= 0 || height <= 0) {
            Logger.logInfo("Invalid window size used (" + width + ", " + height + 
                           ") values passed must be greater than zero");
            return;
        }
        
        data.width  = width;
        data.height = height;
        
        glfwSetWindowSize(handle, data.width, data.height);
    }
    
    public static void setPosition(int positionX, int positionY) {
        data.positionX = positionX;
        data.positionY = positionY;
        
        glfwSetWindowPos(handle, positionX, positionY);
    }
    
    public static void setResolution(int width, int height) {
        if(width <= 0 || height <= 0) {
            Logger.logInfo("Invalid window resolution used (" + width + ", " + 
                           height + ") values passed must be greater than zero");
            return;
        }
        
        data.width = width;
        data.width = height;
    }
    
    public static void setInputMode(int mode, int value) {
        glfwSetInputMode(handle, mode, value);
    }
    
    public static void setTitle(String title) {
        if(title == null) {
            Logger.logInfo("Window title cannot be null");
            return;
        }
        
        data.title = title;
        
        glfwSetWindowTitle(handle, title);
    }
    
    private static void setIcon(String filepath, String filename) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            InputStream file = Window.class.getResourceAsStream(filepath + filename);
            byte[] iconData  = file.readAllBytes();
            
            IntBuffer widthBuf   = stack.mallocInt(1);
            IntBuffer heightBuf  = stack.mallocInt(1);
            IntBuffer channelBuf = stack.mallocInt(1);
            
            ByteBuffer icon = stbi_load_from_memory(
                    stack.malloc(iconData.length).put(iconData).flip(),
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
    
    public static void setMonitor(Monitor monitor) {
        if(monitor == null) {
            Logger.logInfo("Window monitor cannot be null");
            return;
        }
        
        Window.monitor = monitor;
        reconfigure();
    }
    
    public static void setSplitScreenType(SplitScreenType splitType) {
        data.splitType = splitType;
        
        for(Viewport viewport : viewports) {
            switch(splitType) {
                case NONE -> {
                    viewport.active = (viewport.id == 0);
                    viewport.setBounds(data.resolution.width, data.resolution.height, 
                                       0, 0, 
                                       Window.getWidth(), Window.getHeight());
                }
                
                case HORIZONTAL -> {
                    viewport.active = (viewport.id == 0 || viewport.id == 1);
                    switch(viewport.id) {
                        case 0 -> viewport.setBounds(
                                    data.resolution.width, data.resolution.height / 2,
                                    0, Window.getHeight() / 2, 
                                    Window.getWidth(), Window.getHeight() / 2);
                            
                        case 1 -> viewport.setBounds(
                                    data.resolution.width, data.resolution.height / 2,
                                    0, 0, 
                                    Window.getWidth(), Window.getHeight() / 2);
                        
                        default -> viewport.setBounds(data.resolution.width, data.resolution.height, 0, 0, 0, 0);
                    }
                }
                
                case VERTICAL -> {
                    viewport.active = (viewport.id == 0 || viewport.id == 1);
                    switch(viewport.id) {
                        case 0 -> viewport.setBounds(
                                    data.resolution.width / 2, data.resolution.height,
                                    0, 0, 
                                    Window.getWidth() / 2, Window.getHeight());
                            
                        case 1 -> viewport.setBounds(
                                    data.resolution.width / 2, data.resolution.height,
                                    Window.getWidth() / 2, 0, 
                                    Window.getWidth() / 2, Window.getHeight());
                        
                        default -> viewport.setBounds(data.resolution.width, data.resolution.height, 0, 0, 0, 0);
                    }
                }
                
                case TRISECT -> {
                    viewport.active = (viewport.id != 3);
                    switch(viewport.id) {
                        case 0 -> viewport.setBounds(
                                    data.resolution.width / 2, data.resolution.height / 2,
                                    0, Window.getHeight() / 2, 
                                    Window.getWidth() / 2, Window.getHeight() / 2);
                            
                        case 1 -> viewport.setBounds(
                                    data.resolution.width / 2, data.resolution.height / 2,
                                    Window.getWidth() / 2, Window.getHeight() / 2, 
                                    Window.getWidth() / 2, Window.getHeight() / 2);
                            
                        case 2 -> viewport.setBounds(
                                    data.resolution.width / 2, data.resolution.height / 2,
                                    Window.getWidth() / 4, 0, 
                                    Window.getWidth() / 2, Window.getHeight() / 2);
                        
                        default -> viewport.setBounds(data.resolution.width, data.resolution.height, 0, 0, 0, 0);
                    }
                }
                
                case QUARTER -> {
                    viewport.active = true;
                    switch(viewport.id) {
                        case 0 -> viewport.setBounds(
                                    data.resolution.width / 2, data.resolution.height / 2,
                                    0, Window.getHeight() / 2, 
                                    Window.getWidth() / 2, Window.getHeight() / 2);
                            
                        case 1 -> viewport.setBounds(
                                    data.resolution.width / 2, data.resolution.height / 2,
                                    Window.getWidth() / 2, Window.getHeight() / 2, 
                                    Window.getWidth() / 2, Window.getHeight() / 2);
                            
                        case 2 -> viewport.setBounds(
                                    data.resolution.width / 2, data.resolution.height / 2,
                                    0, 0, 
                                    Window.getWidth() / 2, Window.getHeight() / 2);
                            
                        case 3 -> viewport.setBounds(
                                    data.resolution.width / 2, data.resolution.height / 2,
                                    Window.getWidth() / 2, 0, 
                                    Window.getWidth() / 2, Window.getHeight() / 2);
                    }
                }
            }
        }
    }
    
    public static boolean getFullscreen() {
        return data.fullscreen;
    }
    
    public static int getWidth() {
        return data.width;
    }
    
    public static int getHeight() {
        return data.height;
    }
    
    public static int getPositionX() {
        return data.positionX;
    }
    
    public static int getPositionY() {
        return data.positionY;
    }
    
    public static int getResolutionWidth() {
        return data.resolution.width;
    }
    
    public static int getResolutionHeight() {
        return data.resolution.height;
    }
    
    public static int getInputMode(int mode) {
        return glfwGetInputMode(handle, mode);
    }
    
    public static final String getTitle() {
        return data.title;
    }
    
    public static final Monitor getMonitor() {
        return data.monitor;
    }
    
    public static SplitScreenType getSplitScreenType() {
        return data.splitType;
    }
    
}