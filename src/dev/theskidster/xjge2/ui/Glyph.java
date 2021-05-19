package dev.theskidster.xjge2.ui;

import dev.theskidster.xjge2.graphics.Color;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: May 13, 2021
 */

public final class Glyph {

    char c;
    
    Vector3f position;
    Color color;
    
    public Glyph(char c, Vector3f position, Color color) {
        this.c        = c;
        this.position = position;
        this.color    = color;
    }
    
}