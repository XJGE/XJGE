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
    
    private final Rectangle titleBar    = new Rectangle(5, 0, 0, 28);
    private final Rectangle contentArea = new Rectangle();
    private final StringBuilder output  = new StringBuilder();
    
    private final DebugGroup[] groups;
    
    DebugInfo2(Texture engineIcons) {
        
        
        groups = new DebugGroup[] {
            new DGSystemInfo("System Info"),
            new DGPerformance("Performance")
            //new DebugGroup("Settings"),
            //new DebugGroup("Hardware"),
            //new DebugGroup("Noclip")
        };
        
        groups[0].expanded = true;
    }
    
    void render() {
        int contentOffset = 0;
        
        for(int i = 0; i < groups.length; i++) {
            if(groups[i].expanded) {
                groups[i].contentAreaHeight = switch(i) {
                    default -> 154;
                };
            } else {
                groups[i].contentAreaHeight = 0;
            }
            
            titleBar.positionY = Window.getHeight() - ((33 * (i + 1)) + contentOffset);
            
            if(groups[i].expanded) {
                titleBar.width = switch(i) {
                    default -> 400;
                };
            } else {
                titleBar.width = 286;
            }
            
            contentOffset += groups[i].contentAreaHeight;
            
            titleBar.render(1f, Color.BLACK);
            placeholder.drawString(groups[i].title, titleBar.positionX + 5, titleBar.positionY + 7, Color.SILVER, 1f);
            
            contentArea.width     = titleBar.width;
            contentArea.height    = groups[i].contentAreaHeight;
            contentArea.positionX = titleBar.positionX;
            contentArea.positionY = titleBar.positionY - groups[i].contentAreaHeight;
            contentArea.render(0.5f, Color.BLACK);
            
            groups[i].render();
            
            groups[i].button.width     = !groups[i].expanded ? 98 : 126;
            groups[i].button.positionX = titleBar.positionX + titleBar.width - groups[i].button.width;
            groups[i].button.positionY = titleBar.positionY;
            groups[i].button.render(1f, Color.BLACK);
            
            placeholder.drawString(!groups[i].expanded ? "[expand]" : "[collapse]", 
                                   groups[i].button.positionX, 
                                   groups[i].button.positionY + 8, 
                                   groups[i].buttonTextColor, 1f);
        }
    }
    
    void processMouseInput(Mouse mouse) {
        for(DebugGroup group : groups) {
            group.buttonTextColor = (mouse.hovered(group.button)) ? Color.WHITE : Color.CYAN;
            
            if(mouse.clickedOnce(group.button, GLFW_MOUSE_BUTTON_LEFT)) {
                group.expanded = !group.expanded;
            }
        }
    }
    
}