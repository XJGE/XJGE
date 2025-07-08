package org.xjge.core;

import org.xjge.ui.TextEffect;
import org.xjge.ui.Glyph;
import org.xjge.graphics.Color;

/**
 * Created: May 6, 2025
 * 
 * @author J Hoffman
 * @since  4.0.0
 */
final class TextEffectTerminal extends TextEffect {

    private int start;
    
    @Override
    public void apply(Glyph glyph, int index) {
        if(glyph.character == ' ') start = index;
        
        switch(glyph.character) {
            default -> glyph.color.copy((start != 0 && index > start) ? Color.YELLOW : Color.CYAN);
            case '(', ')', ',', '<', '>' -> glyph.color.copy(Color.WHITE);
        };
    }

    @Override
    public void reset() {
        start = 0;
    }

}