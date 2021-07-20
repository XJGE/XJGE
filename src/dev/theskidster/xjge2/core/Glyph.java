package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import org.joml.Vector2i;

//Created: May 13, 2021

/**
 * Represents a single character/letter in a language. These objects are 
 * coupled with additional state that allows them to exhibit limited uniqueness 
 * during instanced rendering.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class Glyph {
    
    char c;
    
    Vector2i position;
    Color color;
    
    /**
     * Creates a new glyph object that exhibits the initial state provided.
     * 
     * @param c        the character/letter this glyph will represent
     * @param position the position of this glyph on the screen
     * @param color    the color this glyph will be rendered in
     */
    Glyph(char c, Vector2i position, Color color) {
        this.c        = c;
        this.position = position;
        this.color    = color;
    }
    
}