package org.xjge.core;

/**
 * Created: May 13, 2021
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
record Glyph2(
    
    float x0,
    float x1,
    float y0,
    float y1,
    
    int advance,
    int width,
    int height,
    int bearingX,
    int bearingY
    
) {}