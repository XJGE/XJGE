package org.xjge.core;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created: Apr 29, 2021
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Window2 {
    
    private static boolean fullscreen;
    
    private static int positionX;
    private static int positionY;
    private static int width;
    private static int height;
    
    private static long handle = NULL;
    
    private static String title;
    
    private static final Viewport[] viewports = new Viewport[4];
    
    private Window2() {}
    
    private static void init() {
        if(handle != NULL) return;
        
        if(!glfwInit()) Logger.logError("Failed to initialize GLFW", null);
        
        handle = glfwCreateWindow(640, 480, "XJGE v" + XJGE.VERSION, NULL, NULL);
    }
    
    public static void show() {
        init();
        
        /**
         * XJGE.setAssetsFilepath("");
         * XJGE.setScenesFilepath("");
         * XJGE.setDebugModeEnabled(true);
         * 
         * Window.setResolution(640, 480);
         * Window.restrict4K(true);
         * Window.retainFullscreen(false);
         * Window.resizeable(false);
         * 
         * Window.show();
         * 
         * XJGE methods like setAssetsFilepath(), setScenesFilepath(), and 
         * setDebugModeEnabled() can no longer be called following Window.show()
         * 
         * Game -> XJGE (maintains loop, scene, and config settings)
         * XJGE -> Window (maintains window, viewport, and glfw callbacks)
         */
    }
    
}