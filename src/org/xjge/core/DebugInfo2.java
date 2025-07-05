package org.xjge.core;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import org.xjge.graphics.Texture;

/**
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class DebugInfo2 {
    
    boolean show;
    
    private int contentOffset;
    private int cursorPositionX;
    private int cursorPositionY;
    
    private final DebugGroup[] groups;
    
    DebugInfo2(Texture engineIcons) {
        groups = new DebugGroup[] {
            new DGSystemInfo("System Info"),
            new DGPerformance("Performance"),
            new DGHardware("Hardware")
            //new DebugGroup("Noclip")
        };
        
        groups[0].expanded = true;
        groups[1].expanded = true;
    }
    
    void update(double deltaMetric, int fps, int entityCount) {
        contentOffset = 0;
        
        for(int i = 0; i < groups.length; i++) {
            contentOffset += groups[i].update(deltaMetric, i, contentOffset, fps, entityCount);
        }
    }
    
    void render() {
        for(DebugGroup group : groups) group.render(cursorPositionX, cursorPositionY);
    }
    
    void processMouseInput(Mouse mouse) {
        cursorPositionX = (int) mouse.cursorPositionX;
        cursorPositionY = (int) mouse.cursorPositionY;
        
        for(DebugGroup group : groups) {
            if(mouse.clickedOnce(group.button, GLFW_MOUSE_BUTTON_LEFT)) group.toggleExpanded();
        }
    }
    
}