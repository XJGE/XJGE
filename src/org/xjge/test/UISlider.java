package org.xjge.test;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import org.xjge.core.Mouse;
import org.xjge.graphics.Color;
import org.xjge.graphics.ModelAnimator;
import org.xjge.ui.Rectangle;

/**
 * 
 * @author J Hoffman
 * @since 
 */
final class UISlider {
    
    private boolean dragging = false;

    private double previousSpeed;
    
    public Rectangle track = new Rectangle(0, 0, 300, 6);
    public Rectangle thumb = new Rectangle(0, 0, 10, 24);
    
    private final ModelAnimator animator;
    
    private final Color thumbColor = new Color(0.75f);
    
    UISlider(ModelAnimator animator) {
        this.animator = animator;
    }
    
    void update() {
        if(!dragging) {
            double time = animator.getNormalizedTime();
            thumb.positionX = (int) (track.positionX + time * track.width) - 5;
        }
    }
    
    void render() {
        track.render(1f, Color.GRAY);
        thumb.render(1f, thumbColor);
    }
    
    void relocate(Rectangle parent) {
        track.positionX = parent.positionX + 10;
        track.positionY = parent.positionY + parent.height - 120;
        thumb.positionY = track.positionY - 9;
    }
    
    void processMouseInput(Mouse mouse) {
        thumbColor.set(mouse.hovered(thumb) ? 1f : 0.75f);
        
        float mx = (float) mouse.getCursorPositionX();
        float my = (float) mouse.getCursorPositionY();
        
        boolean hovering = mx >= track.positionX &&
                           mx <= track.positionX + track.width &&
                           my >= track.positionY - 10 &&
                           my <= track.positionY + track.height + 10;
        
        if(mouse.clicked() && mouse.getButtonID() == GLFW_MOUSE_BUTTON_LEFT) {
            if(hovering || dragging) {
                dragging = true;
                
                float time = (mx - track.positionX) / track.width;
                time = Math.max(0f, Math.min(1f, time));
                
                animator.setNormalizedTime(time);
                thumb.positionX = (int) (track.positionX + time * track.width) - 5;
                
                animator.pause();
            }
        } else {
            dragging = false;
        }
    }
    
}