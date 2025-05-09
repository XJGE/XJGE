package org.xjge.core;

/**
 * Created: May 9, 2025
 * 
 * @author J Hoffman
 * @since  
 */
public class Viewport2 {

    public final int id;
    
    private int width;
    private int height;
    
    Viewport2(int id) {
        this.id = id;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
}