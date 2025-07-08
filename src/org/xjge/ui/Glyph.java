package org.xjge.ui;

import org.xjge.core.XJGE;
import org.xjge.graphics.Color;

/**
 * Glyphs are used to represent the individual characters or letters that 
 * comprise a string. The data contained by these objects can be manipulated to 
 * create dynamic text effects.
 * 
 * @author J Hoffman
 * @since 2.0.0
 * 
 * @see TextEffect
 */
public final class Glyph {
    
    float opacity = 1f;
    
    char character = 32; //ASCII space
    
    int positionX;
    int positionY;
    
    public int positionOffsetX;
    public int positionOffsetY;
    
    Color color = Color.WHITE;
    
    /**
     * Default constructor defined here to keep it out of the public APIs reach.
     */
    Glyph() {}
    
    /**
     * Reverts the position and character fields to their initial values.
     */
    void reset() {
        opacity   = 1f;
        character = 32;
        positionX = 0;
        positionY = 0;
        color     = Color.WHITE;
    }
    
    /**
     * Obtains the current opacity (or transparency) value of this glyph.
     * 
     * @return a number (between 0 and 1) indicating how transparent the glyph is
     */
    public float getOpacity() {
        return opacity;
    }
    
    /**
     * Obtains the current character (or letter) that the glyph is representing.
     * 
     * @return a char denoting which ASCII character the glyph is representing
     */
    public char getCharacter() {
        return character;
    }
    
    /**
     * Obtains the current color of the glyph.
     * 
     * @return an object representing a 3-component RGB color
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * Changes the opacity (or transparency) value of this glyph. If a value of 
     * less than 1 is used, the glyph will exhibit blending effects with 
     * whatever is positioned behind it.
     * 
     * @param opacity the value (between 0 and 1) indicating the glyphs opacity
     */
    public void setOpacity(float opacity) {
        this.opacity = XJGE.clampValue(0f, 1f, opacity);
    }
    
    /**
     * Changes the current character (or letter) this glyph will represent.
     * 
     * @param character the new character this glyph will display
     */
    public void setCharacter(char character) {
        this.character = (character < 32 || character > 127) ? '?' : character;
    }
    
    /**
     * Changes the color of this glyph.
     * 
     * @param newColor the color to render the glyph in
     */
    public void setColor(Color newColor) {
        color = (newColor == null) ? Color.WHITE : newColor;
    }
    
}