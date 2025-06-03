package org.xjge.core;

/**
 * Created: May 28, 2025
 * <br><br>
 * Determines the initial configuration of the applications window.
 * 
 * @author J Hoffman
 * @since  4.0.0
 */
public final class WindowConfig {
    
    private boolean fullscreen;
    private boolean resizable;
    
    private int width  = Window2.DEFAULT_WIDTH;
    private int height = Window2.DEFAULT_HEIGHT;
    
    private String iconFilename;
    private String title          = "XJGE v" + XJGE.VERSION;
    private Monitor monitor       = Hardware2.getPrimaryMonitor();
    private Resolution resolution = new Resolution(Window2.DEFAULT_WIDTH, Window2.DEFAULT_HEIGHT);
    
    boolean getFullscreen() {
        return fullscreen;
    }
    
    boolean getResizable() {
        return resizable;
    }
    
    int getWidth() {
        return width;
    }
    
    int getHeight() {
        return height;
    }
    
    final String getIconFilename() {
        return iconFilename;
    }
    
    final String getTitle() {
        return title;
    }
    
    final Monitor getMonitor() {
        return monitor;
    }
    
    final Resolution getResolution() {
        return resolution;
    }
    
    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }
    
    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }
    
    public void setSize(int width, int height) {
        this.width  = (width <= 0) ? 1280 : width;
        this.height = (height <= 0) ? 720 : height;
    }
    
    public void setIcon(String filename) {
        if(filename == null) return;
        this.iconFilename = filename;
    }
    
    public void setTitle(String title) {
        if(title == null) return;
        this.title = title;
    }
    
    public void setMonitor(Monitor monitor) {
        if(monitor == null) return;
        this.monitor = monitor;
    }
    
    public void setResolution(int width, int height) {
        if(width <= 0 || height <= 0) return;
        this.resolution = new Resolution(width, height);
    }
    
}