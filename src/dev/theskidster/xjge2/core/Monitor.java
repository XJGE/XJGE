package dev.theskidster.xjge2.core;

import java.util.Map.Entry;
import java.util.TreeMap;
import static org.lwjgl.glfw.GLFW.glfwGetMonitorName;
import org.lwjgl.glfw.GLFWVidMode;

/**
 * @author J Hoffman
 * Created: Apr 29, 2021
 */

public final class Monitor {

    public final long handle;
    
    public final String name;
    private String aspect;
    
    Entry<Integer, GLFWVidMode> videoMode;
    final TreeMap<Integer, GLFWVidMode> videoModes = new TreeMap<>();
    
    Monitor(long handle) {
        this.handle = handle;
        name        = glfwGetMonitorName(handle);
        
        
    }
    
}