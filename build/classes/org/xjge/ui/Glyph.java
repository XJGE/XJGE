package org.xjge.ui;

import org.joml.Vector2i;
import org.xjge.graphics.Color;

/**
 * Glyphs are used to represent the individual characters or letters that 
 * comprise a string. The data contained by these objects can be manipulated to 
 * create dynamic text effects.
 * 
 * @author J Hoffman
 * @since 2.0.0
 * 
 * @see TextEffect
 */
public final class Glyph {
    
    /**
     * The current visual opacity of the glyph, from 0 (transparent) to 1 (opaque).
     * Reverts to 1f if the glyph is unused.
     */
    public float opacity = 1f;
    
    /**
     * The character this glyph represents, defaulting to ASCII space (32).
     */
    public char character = 32;
    
    /**
     * The screen-space position of this glyph, set by the layout engine based on font metrics.
     */
    public final Vector2i position = new Vector2i();
    
    /**
     * The color the glyph will be rendered in. This is set to white by default
     * or anytime the object is recycled.
     */
    public final Color color = new Color(1f, 1f, 1f);
    
    /**
     * Default constructor defined here to keep it out of the public APIs reach.
     */
    Glyph() {}
    
}