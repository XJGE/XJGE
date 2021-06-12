package dev.theskidster.xjge2.ui;

import org.joml.Vector2f;

/**
 * @author J Hoffman
 * Created: May 25, 2021
 */

public class Rectangle {

    public float xPos;
    public float yPos;
    public float width;
    public float height;
    
    public Rectangle() {}
    
    public Rectangle(float xPos, float yPos, float width, float height) {
        this.xPos   = xPos;
        this.yPos   = yPos;
        this.width  = width;
        this.height = height;
    }
    
    public boolean contains(Vector2f point) {
        return (point.x > xPos && point.x < xPos + width) && 
               (point.y > yPos && point.y < yPos + height);
    }
    
}