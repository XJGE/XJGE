package org.xjge.test;

import org.xjge.graphics.Color;
import org.xjge.ui.Glyph;
import org.xjge.ui.TextEffect;

/**
 * Created: May 6, 2025
 * 
 * @author J Hoffman
 * @since  
 */
public class TextEffectTest extends TextEffect {

    boolean ascending;
    
    private final Color color = new Color(0, 0, 0);
    
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
        
        if(index >= 16 && index <= 18) {
            if(index == 16) {
                if(ascending) {
                    color.setRed(color.getRed() + 0.01f);
                    if(color.getRed() >= 1) {
                        color.setRed(1f);
                        ascending = false;
                    }
                } else {
                    color.setRed(color.getRed() - 0.01f);
                    if(color.getRed() <= 0) {
                        color.setRed(0f);
                        ascending = true;
                    }
                }
            }
            
            glyph.setColor(color);
        }
    }

    @Override
    public void reset() {
    }

}