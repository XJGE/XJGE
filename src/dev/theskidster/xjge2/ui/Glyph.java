package dev.theskidster.xjge2.ui;

import dev.theskidster.xjge2.graphics.Color;
import org.joml.Vector3i;

/**
 * @author J Hoffman
 * Created: May 13, 2021
 */

final class Glyph {

    char c;
    
    Vector3i position;
    Color color;
    
    Glyph(char c, Vector3i position, Color color) {
        this.c        = c;
        this.position = position;
        this.color    = color;
    }
    
}