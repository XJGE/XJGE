package org.xjge.core;

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
    
    private final Texture engineIcons;
    private final Rectangle titleBar    = new Rectangle(5, 0, 0, 28);
    private final Rectangle contentArea = new Rectangle();
    private final StringBuilder output  = new StringBuilder();
    
    private final Group[] groups;
    
    private class Group {
        
        boolean expanded;
        
        int contentAreaHeight;
        
        final String title;
        final Rectangle expandButton = new Rectangle(0, 0, 0, 28);
        
        Group(String title) { this.title = title; }
        
    }
    
    DebugInfo2(Texture engineIcons) {
        this.engineIcons = engineIcons;
        
        groups = new Group[] {
            new Group("System Info"),
            new Group("Performance"),
            new Group("Settings"),
            new Group("Hardware"),
            new Group("Noclip")
        };
        
        groups[0].expanded = true;
    }
    
    void render() {
        int contentOffset = 0;
        
        for(int i = 0; i < groups.length; i++) {
            if(groups[i].expanded) {
                groups[i].contentAreaHeight = switch(i) {
                    default -> 50;
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
            
            groups[i].expandButton.width     = !groups[i].expanded ? 98 : 126;
            groups[i].expandButton.positionX = titleBar.positionX + titleBar.width - groups[i].expandButton.width;
            groups[i].expandButton.positionY = titleBar.positionY;
            groups[i].expandButton.render(1f, Color.BLACK);
            
            placeholder.drawString(!groups[i].expanded ? "[expand]" : "[collapse]", 
                                   groups[i].expandButton.positionX, 
                                   groups[i].expandButton.positionY + 8, 
                                   Color.CYAN, 1f);
        }
    }
    
    void processMouseInput(Mouse mouse) {
        
    }
    
}