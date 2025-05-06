package org.xjge.test;

import org.xjge.core.Glyph;
import org.xjge.core.TextEffect;
import org.xjge.graphics.Color;

/**
 * Created: May 6, 2025
 * 
 * @author J Hoffman
 * @since  
 */
public class TextEffectTest extends TextEffect {

    @Override
    public void apply(Glyph glyph, int index) {
        Color c = switch(glyph.getCharacter()) {
            default -> Color.WHITE;
            case 'p' -> Color.RED;
        };
        
        glyph.setColor(c);
    }

    @Override
    public void reset() {
    }

}