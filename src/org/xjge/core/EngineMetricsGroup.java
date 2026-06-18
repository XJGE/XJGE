package org.xjge.core;

import java.util.ArrayList;
import java.util.List;
import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glScissor;
import org.xjge.graphics.Color;
import org.xjge.ui.Font;
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
    private final boolean contentHeightLocked;
    
    private int longestStringLength;
    private int contentAreaWidth;
    private int contentAreaHeight;
    protected int contentOffset;
    
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
        contentHeightLocked = false;
    }
    
    EngineMetricsGroup(String title, int contentAreaHeight) {
        this.title = title;
        this.contentAreaHeight = contentAreaHeight;
        contentHeightLocked = true;
    }
    
    protected abstract void update(double deltaMetric, int fps, int entityCount, Noclip noclip);
    
    protected abstract void render();
    
    protected abstract void processMouseInput(Mouse mouse);
    
    int update(double deltaMetric, int index, int contentOffset, int fps, int entityCount, Noclip noclip) {
        longestStringLength  = 0;
        
        for(CharSequence line : output) {
            if(line.length() > longestStringLength) {
                longestStringLength = line.length();
                contentAreaWidth    = Font.FALLBACK.lengthInPixels(line) + (contentHeightLocked ? 48 : 20);
            }
        }
        
        if(expanded && contentAreaWidth < 328) contentAreaWidth = 328;
        if(!contentHeightLocked) contentAreaHeight = (output.size() * Font.FALLBACK.getSize()) + 10;
        
        contentArea.width  = (expanded) ? contentAreaWidth  : 300;
        contentArea.height = (expanded) ? contentAreaHeight : 0;
        
        titleBar.width     = (expanded) ? contentAreaWidth : 300;
        titleBar.position.y = Window.getHeight() - ((33 * (index + 1)) + contentOffset);
        
        contentArea.position.x = titleBar.position.x;
        contentArea.position.y = titleBar.position.y - contentAreaHeight;

        button.width     = !expanded ? 98 : 126;
        button.position.x = titleBar.position.x + titleBar.width - button.width;
        button.position.y = titleBar.position.y;
        
        update(deltaMetric, fps, entityCount, noclip);
        
        return contentArea.height;
    }
    
    void render(int cursorPositionX, int cursorPositionY) {
        titleBar.render(Color.BLACK, 1f);
        Font.FALLBACK.drawString(title, titleBar.position.x + 5, titleBar.position.y + 7, Color.SILVER, 1f);
        contentArea.render(Color.BLACK, 0.5f);
        
        buttonTextColor = (button.contains(cursorPositionX, cursorPositionY))
                        ? Color.WHITE
                        : Color.CYAN;
        
        Font.FALLBACK.drawString(!expanded ? "[expand]" : "[collapse]", 
                                button.position.x, 
                                button.position.y + 8, 
                                buttonTextColor, 1f);
        
        if(expanded) {
            glEnable(GL_SCISSOR_TEST);
            glScissor(contentArea.position.x, contentArea.position.y, contentArea.width, contentArea.height);
            
            for(int i = 0; i < output.size(); i++) {
                Font.FALLBACK.drawString(output.get(i), 
                                         contentArea.position.x + 10, 
                                         (titleBar.position.y - (Font.FALLBACK.getSize() * (i + 1))) - contentOffset, 
                                         highlight);
            }
            
            render();
            glDisable(GL_SCISSOR_TEST);
        }
    }
    
    void toggleExpanded() {
        expanded = !expanded;
    }
    
}