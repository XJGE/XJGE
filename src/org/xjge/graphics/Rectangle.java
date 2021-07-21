package org.xjge.graphics;

import org.joml.Vector2i;

//Created: May 25, 2021

/**
 * Data structure used by {@link RectangleBatch} objects to draw single color 
 * rectangles.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public class Rectangle {

    public int xPos;
    public int yPos;
    public int width;
    public int height;
    
    /**
     * Creates a new rectangle object.
     */
    public Rectangle() {}
    
    /**
     * Creates a new rectangle object using the data specified.
     * 
     * @param xPos   the x-coordinate of the rectangles position
     * @param yPos   the y-coordinate of the rectangles position
     * @param width  the width of the rectangle
     * @param height the height of the rectangle
     */
    public Rectangle(int xPos, int yPos, int width, int height) {
        this.xPos   = xPos;
        this.yPos   = yPos;
        this.width  = width;
        this.height = height;
    }
    
    /**
     * Determines if the specified point has intersected this rectangle.
     * 
     * @param point the point to check
     * 
     * @return true if an intersection was detected
     */
    public boolean contains(Vector2i point) {
        return (point.x > xPos && point.x < xPos + width) && 
               (point.y > yPos && point.y < yPos + height);
    }
    
}