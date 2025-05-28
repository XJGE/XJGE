package org.xjge.core;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
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
    private static int resolutionWidth;
    private static int resolutionHeight;
    
    private static long handle = NULL;
    
    private static String title;
    
    private static final Viewport[] viewports = new Viewport[4];
    
    private Window2() {}
    
    public static void show(WindowConfig config) {
        if(handle != NULL) {
            Logger.logInfo("The application window is already visible.");
            return;
        }
        
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_FOCUS_ON_SHOW, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, config.resizable() ? GLFW_TRUE : GLFW_FALSE);
        
        width  = config.width();
        height = config.height();
        title  = config.title();
        handle = glfwCreateWindow(width, height, title, NULL, NULL);
        
        setIcon("/org/xjge/assets/", "xjge_texture_missing.png");
        
        glfwMakeContextCurrent(handle);
        GL.createCapabilities();
        reconfigure(config.monitor());
        
        //Create framebuffers with resolution, etc.
        
        glfwShowWindow(handle);
        
        //TODO: register GLFW callbacks
        
        while(!glfwWindowShouldClose(handle)) {
            //TODO: reimplement game loop
        }
        
        /**
         * Maybe add these?
         * XJGE.setUserSavedControls(viewportID, Collection);
         * XJGE.setMusicMasterVolume(1.0f);
         * XJGE.setSoundMasterVolume(1.0f);
         * XJGE.setVsyncEnabled(true);
         * XJGE.setGamma() //future feature?
         * XJGE.findMonitors(); //returns list of monitor objects
         * XJGE.findSpeakers(); //returns list of speaker objects
         * XJGE.findGamepads(); //returns list of gamepads
         * 
         * Definitely add these:
         * XJGE.setAssetsFilepath("");
         * XJGE.setScenesFilepath("");
         * XJGE.setDebugModeEnabled(true);
         * XJGE.setScene(Scene);
         * XJGE.addEvent(Event);
         * XJGE.restrict4K(true);
         * XJGE.retainFullscreen(false);
         * 
         * Window.show();
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
         * XJGE -> Window (maintains window, viewport, and glfw callbacks)
         */
        
        /**
         * XJGE.loadConfig(); //returns collection of settings
         * 
         * assetsFilepath
         * scenesFilepath
         * debugEnabled
         * restrict4K
         * retainFullscreen
         */
    }
    
    static void reconfigure(Monitor monitor) {
        if(fullscreen) {
            width  = monitor.getWidth();
            height = monitor.getHeight();
            
            glfwSetWindowMonitor(handle, monitor.handle, positionX, positionY, 
                                 monitor.getWidth(), monitor.getHeight(), monitor.getRefreshRate());
        } else {
            width  = (int) Math.round(monitor.getWidth() * 0.6f);
            height = (int) Math.round(monitor.getHeight() * 0.6f);
            
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