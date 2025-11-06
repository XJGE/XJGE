package org.xjge.core;

/**
 * Created: May 28, 2025
 * <br><br>
 * Stores the resolution of the applications window.
 * <br><br>
 * More specifically, this object is initialized once by the engine and is then 
 * used to store the width and height (in pixels) of the framebuffer texture.
 * 
 * @author J Hoffman
 * @since  4.0.0
 */
final class Resolution {
    
    int width;
    int height;
    
    /**
     * Creates a new object that's used to store the resolution of the 
     * applications window. The object initialized through this constructor is 
     * only done once however the values of the fields it contains are subject to
     * change with regular frequency depending on the needs of the implementation.
     * 
     * @param width the horizontal resolution of the application window (in pixels)
     * @param height the vertical resolution of the application window (in pixels)
     */
    Resolution(int width, int height) {
        this.width  = width;
        this.height = height;
    }
    
}