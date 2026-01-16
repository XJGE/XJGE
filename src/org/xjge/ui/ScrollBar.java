package org.xjge.ui;

import org.xjge.core.Mouse;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import org.xjge.graphics.Color;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public final class ScrollBar {

    private boolean thumbClicked;
    private boolean hidden;
    private final boolean startAtTop;
    
    private float contentScale;
    private float contentOffset;
    private float currentContentLength;
    private float previousContentLength;
    
    private double previousCursorPositionY;
    
    private final Rectangle contentArea;
    private final Rectangle thumb  = new Rectangle(0, 0, 20, 0);
    private final Rectangle trough = new Rectangle(0, 0, 20, 0);
    
    private Color thumbColor = Color.GRAY;
    
    public ScrollBar(Rectangle contentArea, boolean startAtTop) {
        this.contentArea = contentArea;
        this.startAtTop  = startAtTop;
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
    
    public void render() {
        float scaleFactor = ((currentContentLength / thumb.height) / contentScale);
        contentOffset = (startAtTop) ? ((thumb.positionY + thumb.height) - (trough.positionY + trough.height)) * scaleFactor
                                     : (thumb.positionY - trough.positionY) * scaleFactor;
        
        if(!hidden) {
            trough.render(0.5f, Color.BLACK);
            thumb.render(1f, thumbColor);
        }
    }
    
    public void relocate() {
        trough.positionX = (contentArea.positionX + contentArea.width) - trough.width;
        trough.positionY = contentArea.positionY;
        trough.height    = contentArea.height;
        thumb.positionX  = trough.positionX;
    }
    
    public void processMouseInput(Mouse mouse) {
        if(!hidden) {
            thumbColor = (mouse.hovered(thumb)) ? Color.SILVER : Color.GRAY;
            if(!thumbClicked) thumbClicked = mouse.clicked(thumb, GLFW_MOUSE_BUTTON_LEFT);
            double change = 0;
            
            if(mouse.hovered(contentArea) && mouse.getScrollSpeedY() != 0) {
                change = -mouse.getScrollSpeedY() * 10.0;
                scroll(change);
            } else if(thumbClicked) {
                change = previousCursorPositionY - mouse.getCursorPositionY();
                scroll(change);
            }
            
            previousCursorPositionY = (float) mouse.getCursorPositionY();
        }
        
        if(!mouse.clicked()) thumbClicked = false;
    }
    
    public void setContentAreaLength(float length) {
        float previousThumbHeight = thumb.height;
        
        previousContentLength = currentContentLength;
        currentContentLength  = length;
        
        contentScale = currentContentLength / contentArea.height;
        hidden       = contentScale <= 1;
        thumb.height = (int) (trough.height / contentScale);
        
        if(previousContentLength != currentContentLength) {
            if(startAtTop) {
                thumb.positionY = (trough.positionY + trough.height) - thumb.height;
            }
            
            scroll(previousThumbHeight - thumb.height);
        }
    }
    
    public int getContentOffset() {
        return (int) contentOffset;
    }
    
}