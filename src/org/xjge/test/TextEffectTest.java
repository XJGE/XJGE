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
    
    float redValue;
    
    @Override
    public void apply(int index, Glyph glyph) {
        glyph.position.y += switch(glyph.character) {
            default -> 0;
            case 'p' -> 23;
        };
        
        switch(index) {
            case 4, 5, 6, 7, 8 -> {
                glyph.opacity = 0.5f;
            }
        }
        
        if(index == 0) {
            if(ascending) {
                redValue += 0.01f;
                
                if(redValue >= 1) {
                    redValue = 1f;
                    ascending = false;
                }
            } else {
                redValue -= 0.01f;
                
                if(redValue <= 0) {
                    redValue = 0f;
                    ascending = true;
                }
            }
        }
        
        if(index >= 16 && index <= 18) {
            glyph.color.set(redValue, 0f, 0f);
        }
    }

    @Override
    public void reset() {
    }

}