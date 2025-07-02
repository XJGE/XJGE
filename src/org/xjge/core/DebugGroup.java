package org.xjge.core;

import org.xjge.graphics.Color;
import org.xjge.ui.Rectangle;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
abstract class DebugGroup {
    
    boolean expanded;
    
    int contentAreaWidth;
    int contentAreaHeight;
    
    final String title;
    final Rectangle button = new Rectangle(0, 0, 0, 28);
    
    Color buttonTextColor = Color.CYAN;
    
    DebugGroup(String title) {
        this.title = title;
    }
    
    abstract void render(StringBuilder output, Rectangle contentArea);

}