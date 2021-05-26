package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import dev.theskidster.xjge2.ui.Background;
import dev.theskidster.xjge2.ui.Font;
import static dev.theskidster.xjge2.ui.Font.DEFAULT_SIZE;
import dev.theskidster.xjge2.ui.Glyph;
import dev.theskidster.xjge2.ui.Text;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: May 25, 2021
 */

class TerminalText extends Text {

    private final int PADDING = 4;
    
    TerminalText(Font font) {
        super(font);
    }
    
    void drawCommand(String text, Vector3f position) {
        boolean changed = textChanged(text) || positionChanged(position);
        
        if(changed) {
            glyphs.clear();
            
            int advance = 0;
            int start   = 0;
            
            for(int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if(c == ' ') start = i;
                
                Vector3f pos = new Vector3f(position.x + advance + font.getGlyphBearingLeft(c),
                                            position.y + font.getGlyphDescent(c), 
                                            position.z);
                
                Color col = (start != 0 && i > start) ? Color.YELLOW : Color.CYAN;
                
                switch(c) {
                    case '(', ')', ',', '<', '>' -> col = Color.WHITE;
                }
                
                glyphs.put(i, new Glyph(c, pos, col));
                advance += font.getGlyphAdvance(c);
            }
        }
        
        font.draw(glyphs, changed);
        
        updateChangeValues(text, position, Color.WHITE);
    }
    
    void drawOutput(TerminalOutput[] o1, TerminalOutput o2, int index, boolean executed, Background background) {
        if(executed) {
            glyphs.clear();
            
            int advance = 0;
            int descent = 0;
            int yOffset = (Font.DEFAULT_SIZE + PADDING) * Text.numCharOccurences(o2.text, '\n', 0);
            
            if(index != 0) {
                String composite = "";
                for(int i = 0; i < index; i++) composite += o1[i].text;
                yOffset += (Font.DEFAULT_SIZE + PADDING) * Text.numCharOccurences(composite, '\n', 0);
            }
            
            for(int i = 0; i < o2.text.length(); i++) {
                char c = o2.text.charAt(i);
                
                if(c != '\n') {
                    Vector3f pos = new Vector3f(advance + font.getGlyphBearingLeft(c), 
                                               (yOffset + descent + font.getGlyphDescent(c) + DEFAULT_SIZE / 4), 
                                                -2);
                    
                    glyphs.put(i, new Glyph(c, pos, o2.color));
                    advance += font.getGlyphAdvance(c);
                } else {
                    advance  = 0;
                    descent -= Font.DEFAULT_SIZE + PADDING;
                }
            }
            
            background.drawRectangle(0, yOffset + descent + DEFAULT_SIZE + PADDING, 
                                     XJGE.getResolutionX(), Math.abs(descent), 
                                     Color.BLACK, 0.4f);
        }
        
        font.draw(glyphs, executed);
    } 

}