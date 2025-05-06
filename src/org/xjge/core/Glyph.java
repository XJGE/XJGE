package org.xjge.core;

import org.xjge.graphics.Color;

/**
 * Created: May 13, 2021
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Glyph {
    
    char character = 32; //ASCII space
    
    int positionX;
    int positionY;
    
    Color color = Color.WHITE;
    
    void reset() {
        character = 32;
        positionX = 0;
        positionY = 0;
        color     = Color.WHITE;
    }
    
    public void overrideCharacter() {
        
    }
    
    public void overridePositionX() {
        
    }
    
    public void overridePositionY() {
        
    }
    
    public void overrideColor() {
        
    }
    
}