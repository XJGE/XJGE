package org.xjge.ui;

import org.xjge.core.XJGE;
import org.xjge.graphics.Color;

/**
 * Created: May 13, 2021
 * <br><br>
 * Represents a 
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Glyph {
    
    float opacity = 1f;
    
    char character = 32; //ASCII space
    
    int positionX;
    int positionY;
    
    public int positionOffsetX;
    public int positionOffsetY;
    
    Color color = Color.WHITE;
    
    void reset() {
        character = 32;
        positionX = 0;
        positionY = 0;
    }
    
    public float getOpacity() {
        return opacity;
    }
    
    public char getCharacter() {
        return character;
    }
    
    public Color getColor() {
        return color;
    }
    
    public void setOpacity(float newOpacity) {
        opacity = XJGE.clampValue(0f, 1f, newOpacity);
    }
    
    public void setCharacter(char newCharacter) {
        character = (newCharacter < 32 || newCharacter > 127) ? '?' : newCharacter;
    }
    
    public void setColor(Color newColor) {
        color = (color == null) ? Color.WHITE : newColor;
    }
    
}