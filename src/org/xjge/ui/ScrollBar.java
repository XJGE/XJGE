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
    private final Rectangle thumb  = new Rectangle(20, 0, 0, 0);
    private final Rectangle trough = new Rectangle(20, 0, 0, 0, Color.BLACK, 0.5f);
    
    public ScrollBar(Rectangle contentArea, boolean startAtTop) {
        this.contentArea = contentArea;
        this.startAtTop  = startAtTop;
        relocate();
    }
    
    private void scroll(double change) {
        change = -change;
        
        boolean minLimitReached = thumb.position.y + change < trough.position.y;
        boolean maxLimitReached = (thumb.position.y + thumb.height) + change > trough.position.y + trough.height;
        
        if(minLimitReached) {
            thumb.position.y = trough.position.y;
        } else if(maxLimitReached) {
            thumb.position.y = trough.position.y + trough.height - thumb.height;
        } else {
            thumb.position.y += change;
        }
    }
    
    public void render() {
        //TODO: in theory this can drift with large enough content, see the NNCEditors implementation for normalization fix
        float scaleFactor = ((currentContentLength / thumb.height) / contentScale);
        contentOffset = (startAtTop) ? ((thumb.position.y + thumb.height) - (trough.position.y + trough.height)) * scaleFactor
                                     : (thumb.position.y - trough.position.y) * scaleFactor;
        
        if(!hidden) {
            trough.render();
            thumb.render();
        }
    }
    
    public void relocate() {
        trough.position.x = (contentArea.position.x + contentArea.width) - trough.width;
        trough.position.y = contentArea.position.y;
        trough.height     = contentArea.height;
        thumb.position.x  = trough.position.x;
    }
    
    public void processMouseInput(Mouse mouse) {
        if(!hidden) {
            thumb.color.set((mouse.hovered(thumb)) ? Color.SILVER : Color.GRAY);
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
                thumb.position.y = (trough.position.y + trough.height) - thumb.height;
            }
            
            scroll(previousThumbHeight - thumb.height);
        }
    }
    
    public int getContentOffset() {
        return (int) contentOffset;
    }
    
}