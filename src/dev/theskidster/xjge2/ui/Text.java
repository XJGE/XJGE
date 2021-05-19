package dev.theskidster.xjge2.ui;

import dev.theskidster.xjge2.graphics.Color;
import dev.theskidster.xjge2.shaderutils.GLProgram;
import java.util.HashMap;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: May 18, 2021
 */

public class Text {

    private String prevText        = "";
    private final Vector3f prevPos = new Vector3f();
    private Color prevCol          = Color.WHITE;
    private Font font;
    
    private final HashMap<Integer, Glyph> glyphs = new HashMap<>();
    
    public Text(Font font) {
        this.font = font;
    }
    
    public void draw(String text, Vector3f position, Color color) {
        boolean changed = !prevText.equals(text) || !prevPos.equals(position.x, position.y, position.z) || !prevCol.equals(color);
        
        if(changed) {
            glyphs.clear();
            float advance = 0;
            
            for(int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                Vector3f pos = new Vector3f(position.x + advance + font.getGlyphBearingLeft(c), 
                                            position.y + font.getGlyphDescent(c),
                                            position.z);

                glyphs.put(i, new Glyph(c, pos, color));
                advance += font.getGlyphAdvance(c);
            }
        }
        
        font.draw(glyphs, changed);
        
        prevText = text;
        prevPos.set(position);
        prevCol = color;
    }
    
}