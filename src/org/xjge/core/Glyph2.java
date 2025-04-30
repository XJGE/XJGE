package org.xjge.core;

import org.joml.Vector2i;
import org.xjge.graphics.Color;

/**
 * Created: May 13, 2021
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class Glyph2 {

    boolean inUse;
    
    char character;
    
    int index = -1;
    int positionX;
    int positionY;
    
    Color color = Color.WHITE;
    
    void reset() {
        inUse     = false;
        character = 0;
        index     = -1;
        positionX = 0;
        positionY = 0;
        color     = Color.WHITE;
    }
    
}