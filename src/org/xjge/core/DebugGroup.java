package org.xjge.core;

import org.xjge.graphics.Color;
import org.xjge.ui.Rectangle;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
abstract class DebugGroup {
    
    private boolean expanded;
    
    final String title;
    final Rectangle button = new Rectangle(0, 0, 0, 28);
    protected final Rectangle titleBar    = new Rectangle(5, 0, 0, 28);
    protected final Rectangle contentArea = new Rectangle();
    protected final StringBuilder output  = new StringBuilder();
    
    Color buttonTextColor = Color.CYAN;
    
    DebugGroup(String title) {
        this.title = title;
    }
    
    abstract void update();
    
    abstract void render();

    void toggleExpanded() {
        expanded = !expanded;
    }
    
    int getContentOffset(int index) {
        
    }
    
}