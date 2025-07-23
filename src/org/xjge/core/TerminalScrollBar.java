package org.xjge.core;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import org.xjge.graphics.Color;
import org.xjge.ui.Rectangle;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
final class TerminalScrollBar {

    private boolean thumbClicked;
    private boolean hidden;
    
    private float contentScale;
    private float contentOffset;
    private float currentContentLength;
    private float previousContentLength;
    
    private double previousCursorPositionY;
    
    private final Rectangle contentArea;
    private final Rectangle thumb  = new Rectangle(0, 0, 20, 0);
    private final Rectangle trough = new Rectangle(0, 0, 20, 0);
    
    private Color thumbColor = Color.GRAY;
    
    TerminalScrollBar(Rectangle contentArea) {
        this.contentArea = contentArea;
        relocate();
    }
    
    private void scroll(double change) {
        change = -change;
        
        boolean minLimitReached = thumb.positionY + change < trough.positionY;
        boolean maxLimitReached = (thumb.positionY + thumb.height) + change > trough.positionY + trough.height;
        
        if(minLimitReached) {
            thumb.positionY = trough.positionY;
        } else if(maxLimitReached) {
            thumb.positionY = trough.positionY + trough.height - thumb.height;
        } else {
            thumb.positionY += change;
        }
    }
    
    void render() {
        float scaleFactor = ((currentContentLength / thumb.height) / contentScale);
        contentOffset = (thumb.positionY - trough.positionY) * scaleFactor;

        if(!hidden) {
            trough.render(0.5f, Color.BLACK);
            thumb.render(1f, thumbColor);
        }
    }
    
    void relocate() {
        trough.positionX = contentArea.width - trough.width;
        trough.positionY = contentArea.positionY;
        trough.height    = contentArea.height;
        thumb.positionX  = trough.positionX;
    }
    
    void processMouseInput(Mouse mouse) {
        if(!hidden) {
            thumbColor = (mouse.hovered(thumb)) ? Color.SILVER : Color.GRAY;
            if(!thumbClicked) thumbClicked = mouse.clicked(thumb, GLFW_MOUSE_BUTTON_LEFT);
            double change = 0;
            
            if(mouse.hovered(contentArea) && mouse.getScrollSpeedY() != 0) {
                change = -mouse.getScrollSpeedY() * 10.0;
                scroll(change);
            } else if(thumbClicked) {
                change = previousCursorPositionY - mouse.cursorPositionY;
                scroll(change);
            }
            
            previousCursorPositionY = (float) mouse.cursorPositionY;
        }
        
        if(!mouse.clicked()) thumbClicked = false;
    }
    
    void setContentAreaLength(float length) {
        float previousThumbHeight = thumb.height;
        
        previousContentLength = currentContentLength;
        currentContentLength  = length;
        
        contentScale = currentContentLength / contentArea.height;
        hidden       = contentScale <= 1;
        thumb.height = (int) (trough.height / contentScale);
        
        if(previousContentLength != currentContentLength) scroll(previousThumbHeight - thumb.height);
    }
    
    int getContentOffset() {
        return (int) contentOffset;
    }
    
}