package dev.theskidster.xjge2.core;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author J Hoffman
 * Created: Apr 29, 2021
 */

final class Window {
    
    public final long handle = glfwCreateWindow(640, 480, "XJGE 2 (v" + XJGE.VERSION + ")", NULL, NULL);
    
    static void show() {
        
    }
    
}