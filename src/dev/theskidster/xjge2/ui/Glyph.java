package dev.theskidster.xjge2.ui;

import dev.theskidster.xjge2.graphics.Color;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: May 13, 2021
 */

public final class Glyph {

    //TODO: remove public modifiers once moved
    
    public char c;
    
    public Vector3f position;
    public Color color;
    
    public Glyph(char c, Vector3f position, Color color) {
        this.c        = c;
        this.position = position;
        this.color    = color;
    }
    
    public boolean equals(Glyph other) {
        return (this.c == other.c) &&
                (this.position.equals(other.position)) &&
                (this.color.equals(other.color));
    }
    
}