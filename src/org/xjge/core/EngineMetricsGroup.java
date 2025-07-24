package org.xjge.core;

import java.util.ArrayList;
import java.util.List;
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
abstract class EngineMetricsGroup {
    
    boolean expanded;
    
    private int longestStringLength;
    private int contentAreaWidth;
    private int contentAreaHeight;
    
    Color buttonTextColor = Color.CYAN;
    
    final String title;
    
    final Rectangle button      = new Rectangle(0, 0, 0, 28);
    final Rectangle titleBar    = new Rectangle(5, 0, 0, 28);
    final Rectangle contentArea = new Rectangle();
    final TextEffect highlight  = new HighlightInfo();
    
    final List<StringBuilder> output = new ArrayList<>();
    
    private final class HighlightInfo extends TextEffect {

        private boolean colonFound;
        
        @Override
        public void apply(int index, Glyph glyph) {
            if(index == 0) colonFound = false;
            glyph.color.copy((colonFound) ? Color.YELLOW : Color.WHITE);
            if(glyph.character == ':') colonFound = true;
        }

        @Override
        public void reset() {}
        
    }
    
    EngineMetricsGroup(String title) {
        this.title = title;
    }
    
    protected abstract void update(double deltaMetric, int fps, int entityCount, Noclip noclip);
    
    protected abstract void render();
    
    int update(double deltaMetric, int index, int contentOffset, int fps, int entityCount, Noclip noclip) {
        longestStringLength  = 0;
        
        for(CharSequence line : output) {
            if(line.length() > longestStringLength) {
                longestStringLength = line.length();
                contentAreaWidth    = placeholder.lengthInPixels(line) + 20;
            }
        }
        
        if(expanded && contentAreaWidth < 328) contentAreaWidth = 328;
        contentAreaHeight = (output.size() * placeholder.size) + 10;
        
        contentArea.width  = (expanded) ? contentAreaWidth  : 300;
        contentArea.height = (expanded) ? contentAreaHeight : 0;
        
        titleBar.width     = (expanded) ? contentAreaWidth : 300;
        titleBar.positionY = Window.getHeight() - ((33 * (index + 1)) + contentOffset);
        
        contentArea.positionX = titleBar.positionX;
        contentArea.positionY = titleBar.positionY - contentAreaHeight;

        button.width     = !expanded ? 98 : 126;
        button.positionX = titleBar.positionX + titleBar.width - button.width;
        button.positionY = titleBar.positionY;
        
        update(deltaMetric, fps, entityCount, noclip);
        
        return contentArea.height;
    }
    
    void render(int cursorPositionX, int cursorPositionY) {
        titleBar.render(1f, Color.BLACK);
        placeholder.drawString(title, titleBar.positionX + 5, titleBar.positionY + 7, Color.SILVER, 1f);
        contentArea.render(0.5f, Color.BLACK);
        
        buttonTextColor = (button.contains(cursorPositionX, cursorPositionY))
                        ? Color.WHITE
                        : Color.CYAN;
        
        placeholder.drawString(!expanded ? "[expand]" : "[collapse]", 
                                button.positionX, 
                                button.positionY + 8, 
                                buttonTextColor, 1f);
        
        if(expanded) {
            for(int i = 0; i < output.size(); i++) {
                placeholder.drawString(output.get(i), 
                                       contentArea.positionX + 10, 
                                       titleBar.positionY - (placeholder.size * (i + 1)), 
                                       highlight);
            }
            render();
        }
    }
    
    void toggleExpanded() {
        expanded = !expanded;
    }
    
}