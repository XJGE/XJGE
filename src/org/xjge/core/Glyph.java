package org.xjge.core;

import org.xjge.graphics.Color;
import org.joml.Vector2i;

/**
 * Created: May 13, 2021
 * <p>
 * Represents a single character/letter in a language. These objects are coupled 
 * with additional state that allows them to exhibit limited uniqueness during 
 * instanced rendering.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class Glyph {
    
    boolean inUse;
    
    char character;
    
    Vector2i position;
    Color color;
    
    /**
     * Creates a new glyph object that exhibits the initial state provided.
     * 
     * @param character the character/letter this glyph will represent
     * @param position the position of this glyph on the screen
     * @param color the color this glyph will be rendered in
     */
    Glyph(char character, Vector2i position, Color color) {
        this.character = character;
        this.position  = position;
        this.color     = color;
    }
    
}