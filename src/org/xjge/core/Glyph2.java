package org.xjge.core;

/**
 * Created: May 13, 2021
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
record Glyph2(
    
    float s0,
    float s1,
    float t0,
    float t1,
    
    int advance,
    int width,
    int height,
    int bearingX,
    int bearingY
    
) {}