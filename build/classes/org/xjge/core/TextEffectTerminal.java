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
        if(glyph.getCharacter() == ' ') start = index;
                
        Color color = switch(glyph.getCharacter()) {
            default -> (start != 0 && index > start) ? Color.YELLOW : Color.CYAN;
            case '(', ')', ',', '<', '>' -> Color.WHITE;
        };
        
        glyph.setColor(color);
    }

    @Override
    public void reset() {
        start = 0;
    }

}