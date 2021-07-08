package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import org.joml.Vector2i;

/**
 * @author J Hoffman
 * Created: May 13, 2021
 */

/**
 * Data structure which represents a single symbol (or character) used to comprise the letters of a language.
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