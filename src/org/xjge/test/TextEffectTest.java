package org.xjge.test;

import org.xjge.ui.Glyph;
import org.xjge.ui.TextEffect;

/**
 * Created: May 6, 2025
 * 
 * @author J Hoffman
 * @since  
 */
public class TextEffectTest extends TextEffect {

    @Override
    public void apply(Glyph glyph, int index) {
        glyph.positionOffsetY = switch(glyph.getCharacter()) {
            default -> 0;
            case 'p' -> 23;
        };
        
        switch(index) {
            case 4, 5, 6, 7, 8 -> {
                glyph.setOpacity(0.5f);
            }
        }
    }

    @Override
    public void reset() {
    }

}