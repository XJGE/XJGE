package org.xjge.core;

import static org.lwjgl.glfw.GLFW.glfwInit;

/**
 * Created: May 28, 2025
 * <br><br>
 * Determines the initial configuration of the applications window.
 * 
 * @author J Hoffman
 * @since  4.0.0
 */
public final class WindowConfig {
    
    final boolean resizable;
    final boolean fullscreen;
    
    final int width;
    final int height;
    
    final String title;
    final Monitor monitor;
    final Resolution resolution;
    
    static {
        String javaVersion = System.getProperty("java.version");
        
        if(javaVersion.compareTo("22.0.2") < 0) {
            Logger.logError("Unsupported Java version used (found: " + javaVersion + " required: 22.0.2)", null);
        }
        
        if(!glfwInit()) Logger.logError("Failed to initialize GLFW", null);
    }
    
    /**
     * 
     * @param resizable
     * @param fullscreen
     * @param width
     * @param height
     * @param title
     * @param monitor
     * @param resolution 
     */
    public WindowConfig(boolean resizable, boolean fullscreen, int width, int height, String title, Monitor monitor, Resolution resolution) {
        this.resizable  = resizable;
        this.fullscreen = fullscreen;
        this.width      = (width <= 0) ? 640 : width;
        this.height     = (height <= 0) ? 480 : height;
        this.title      = (title == null) ? "XJGE v" + XJGE.VERSION : title;
        this.monitor    = monitor;
        this.resolution = (resolution == null) ? new Resolution(this.width, this.height) : resolution;
    }
    
}