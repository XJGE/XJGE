package dev.theskidster.xjge2.core;

import java.util.Map.Entry;
import java.util.TreeMap;
import org.lwjgl.glfw.GLFWVidMode;

/**
 * @author J Hoffman
 * Created: Apr 29, 2021
 */

public final class Monitor {

    final long handle;
    
    private String name;
    private String aspect;
    
    Entry<Integer, GLFWVidMode> videoMode;
    final TreeMap<Integer, GLFWVidMode> videoModes = new TreeMap<>();
    
    Monitor(long handle) {
        this.handle = handle;
    }
    
}