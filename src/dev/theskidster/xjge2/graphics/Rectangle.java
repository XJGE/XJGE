package dev.theskidster.xjge2.graphics;

import org.joml.Vector2i;

/**
 * @author J Hoffman
 * Created: May 25, 2021
 */

/**
 * Data structure used by {@link RectangleBatch} objects to draw single color rectangles.
 */
public class Rectangle {

    public int xPos;
    public int yPos;
    public int width;
    public int height;
    
    /**
     * 
     */
    public Rectangle() {}
    
    /**
     * Creates a new rectangle object.
     * 
     * @param xPos
     * @param yPos
     * @param width
     * @param height 
     */
    public Rectangle(int xPos, int yPos, int width, int height) {
        this.xPos   = xPos;
        this.yPos   = yPos;
        this.width  = width;
        this.height = height;
    }
    
    public boolean contains(Vector2i point) {
        return (point.x > xPos && point.x < xPos + width) && 
               (point.y > yPos && point.y < yPos + height);
    }
    
}