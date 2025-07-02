package org.xjge.core;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import org.xjge.graphics.Color;
import org.xjge.graphics.Texture;
import static org.xjge.ui.Font.placeholder;
import org.xjge.ui.Rectangle;

/**
 * Created: Jun 10, 2021
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class DebugInfo2 {
    
    boolean show;
    
    private int contentOffset;
    private int contentAreaHeight;
    private int cursorPositionX;
    private int cursorPositionY;
    
    private final Rectangle titleBar    = new Rectangle(5, 0, 0, 28);
    private final Rectangle contentArea = new Rectangle();
    private final StringBuilder output  = new StringBuilder();
    
    private final DebugGroup[] groups;
    
    DebugInfo2(Texture engineIcons) {
        groups = new DebugGroup[] {
            new DGSystemInfo("System Info"),
            new DGPerformance("Performance"),
            //new DebugGroup("Settings"),
            //new DGHardware("Hardware")
            //new DebugGroup("Noclip")
        };
        
        groups[0].expanded = true;
    }
    
    void render() {        
        contentOffset = 0;
        
        for(int i = 0; i < groups.length; i++) {
            contentAreaHeight  = (groups[i].expanded) ? groups[i].contentAreaHeight : 0;
            titleBar.width     = (groups[i].expanded) ? groups[i].contentAreaWidth : 286;
            titleBar.positionY = Window.getHeight() - ((33 * (i + 1)) + contentOffset);
            
            contentOffset += contentAreaHeight;
            
            titleBar.render(1f, Color.BLACK);
            placeholder.drawString(groups[i].title, titleBar.positionX + 5, titleBar.positionY + 7, Color.SILVER, 1f);
            
            contentArea.width     = titleBar.width;
            contentArea.height    = contentAreaHeight;
            contentArea.positionX = titleBar.positionX;
            contentArea.positionY = titleBar.positionY - contentAreaHeight;
            contentArea.render(0.5f, Color.BLACK);
            
            if(groups[i].expanded) groups[i].render(output, contentArea);
            
            groups[i].button.width     = !groups[i].expanded ? 98 : 126;
            groups[i].button.positionX = titleBar.positionX + titleBar.width - groups[i].button.width;
            groups[i].button.positionY = titleBar.positionY;
            groups[i].button.render(1f, Color.BLACK);
            
            groups[i].buttonTextColor = (groups[i].button.contains(cursorPositionX, cursorPositionY))
                                      ? Color.WHITE
                                      : Color.CYAN;
            
            placeholder.drawString(!groups[i].expanded ? "[expand]" : "[collapse]", 
                                   groups[i].button.positionX, 
                                   groups[i].button.positionY + 8, 
                                   groups[i].buttonTextColor, 1f);
        }
    }
    
    void processMouseInput(Mouse mouse) {
        cursorPositionX = (int) mouse.cursorPositionX;
        cursorPositionY = (int) mouse.cursorPositionY;
        
        for(DebugGroup group : groups) {
            if(mouse.clickedOnce(group.button, GLFW_MOUSE_BUTTON_LEFT)) group.expanded = !group.expanded;
        }
    }
    
}