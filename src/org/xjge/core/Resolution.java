package org.xjge.core;

/**
 * Created: May 28, 2025
 * <br><br>
 * Determines the internal resolution (in pixels) that the applications 
 * framebuffer texture will use.
 * 
 * @author J Hoffman
 * @since  4.0.0
 */
final class Resolution {
    
    int width;
    int height;
    
    Resolution(int width, int height) {
        this.width  = width;
        this.height = height;
    }
    
}