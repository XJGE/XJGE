package org.xjge.core;

import org.xjge.graphics.Color;
import static org.xjge.ui.Font.placeholder;
import org.xjge.ui.Glyph;
import org.xjge.ui.Rectangle;
import org.xjge.ui.TextEffect;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
class DGSystemInfo extends DebugGroup {

    private int longestString;
    
    private final TextEffect highlight = new Highlight();
    
    private class Highlight extends TextEffect {

        private int colonIndex;
        
        @Override
        public void apply(Glyph glyph, int index) {
            if(glyph.getCharacter() == ':') colonIndex = index;
            glyph.setColor((index <= colonIndex) ? Color.WHITE : Color.YELLOW);
            
            System.out.println(glyph.getCharacter() + " " + index); //Why does this appear to be called twice?
        }

        @Override
        public void reset() {}
        
    }
    
    DGSystemInfo(String title) {
        super(title);
        
        contentAreaWidth = 400;
        contentAreaHeight = 154;
    }

    @Override
    void render(StringBuilder output, Rectangle contentArea) {
        output.setLength(0);
        output.append("OS NAME: ").append(System.getProperty("os.name")).append("\n");
        //output.append("JAVA VER: ").append(System.getProperty("java.version"));
        
        placeholder.drawString(output, contentArea.positionX, contentArea.positionY, highlight);
    }

}