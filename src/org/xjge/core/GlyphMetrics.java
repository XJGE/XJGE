package org.xjge.core;

/**
 * Created: Apr 28, 2025
 * 
 * @author J Hoffman
 * @since  4.0.0
 */
record GlyphMetrics (
    float texCoordX,
    float texCoordY,
    int advance,
    int descent,
    int leftBearing
) {}