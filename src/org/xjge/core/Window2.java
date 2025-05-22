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
    
    private static final long handle;
    
    private static String title;
    
    static {
        handle = glfwCreateWindow(640, 480, "XJGE v" + XJGE.VERSION, NULL, NULL);
    }
    
    static void show() {
        
    }
    
}