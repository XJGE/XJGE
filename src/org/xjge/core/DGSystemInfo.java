package org.xjge.core;

import org.xjge.graphics.Color;
import static org.xjge.ui.Font.placeholder;
import org.xjge.ui.Glyph;
import org.xjge.ui.TextEffect;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
class DGSystemInfo extends DebugGroup {

    private int previousLength;
    private int longestString;
    
    private final TextEffect highlight = new Highlight();
    
    private class Highlight extends TextEffect {

        private boolean colonFound;
        
        @Override
        public void apply(Glyph glyph, int index) {
            if(glyph.getCharacter() == '\n' || index == 0) colonFound = false;
            glyph.setColor((colonFound) ? Color.YELLOW : Color.WHITE);
            if(glyph.getCharacter() == ':') colonFound = true;
        }

        @Override
        public void reset() {}
        
    }
    
    DGSystemInfo(String title) {
        super(title);
    }
    
    @Override
    int update(int index, int contentOffset) {
        return 0;
    }

    @Override
    void render() {
        /*
        output.setLength(0);
        previousLength = 0;
        output.append("OS NAME: ").append(System.getProperty("os.name")).append("\n");
        updateLength(output);
        output.append("JAVA VER: ").append(System.getProperty("java.version")).append("\n");
        updateLength(output);
        output.append("XJGE VER: ").append(XJGE.VERSION).append("\n");
        updateLength(output);
        output.append("GLFW VER: ").append(glfwGetVersionString()).append("\n");
        updateLength(output);
        output.append("OPENAL VER: ").append(alGetString(AL_VERSION)).append("\n");
        updateLength(output);
        output.append("OPENGL VER: ").append(glGetString(GL_VERSION));
        updateLength(output);
        
        placeholder.drawString(output, 
                               contentArea.positionX + 10, 
                               contentArea.positionY + contentArea.height - placeholder.size, 
                               highlight);
        */
    }
    
    private void updateLength(StringBuilder output) {
        if(output.length() - previousLength > longestString) {
            longestString = output.length() - previousLength;
            //contentAreaWidth = placeholder.lengthInPixels(output.subSequence(previousLength, output.length())) + 10;
        }
        
        previousLength = output.length();
    }

}