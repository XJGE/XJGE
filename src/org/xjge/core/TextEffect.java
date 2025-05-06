package org.xjge.core;

/**
 * Created: May 6, 2025
 * 
 * @author J Hoffman
 * @since  4.0.0
 */
public abstract class TextEffect {

    public abstract void apply(Glyph glyph, int index);
    
    public abstract void reset();
    
}