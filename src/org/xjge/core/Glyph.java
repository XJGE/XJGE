package org.xjge.core;

import org.joml.Vector2i;
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
    
    public int positionOffsetX;
    public int positionOffsetY;
    
    Color color = Color.WHITE;
    
    void reset() {
        character = 32;
        positionX = 0;
        positionY = 0;
        color     = Color.WHITE;
    }
    
    public char getCharacter() {
        return character;
    }
    
    public Color getColor() {
        return color;
    }
    
    public void setCharacter(char newCharacter) {
        character = (newCharacter < 32 || newCharacter > 127) ? '?' : newCharacter;
    }
    
    public void setColor(Color newColor) {
        color = (color == null) ? Color.WHITE : newColor;
    }
    
}