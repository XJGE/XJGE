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
            thumb.position.x = (int) (track.position.x + time * track.width) - 5;
        }
    }
    
    void render() {
        track.render(Color.GRAY, 1f);
        thumb.render(thumbColor, 1f);
    }
    
    void relocate(Rectangle parent) {
        track.position.x = parent.position.x + 10;
        track.position.y = parent.position.y + parent.height - 160;
        thumb.position.y = track.position.y - 9;
    }
    
    void processMouseInput(Mouse mouse) {
        thumbColor.set(mouse.hovered(thumb) ? 1f : 0.75f);
        
        float mx = (float) mouse.getCursorPositionX();
        float my = (float) mouse.getCursorPositionY();
        
        boolean hovering = mx >= track.position.x &&
                           mx <= track.position.x + track.width &&
                           my >= track.position.y - 10 &&
                           my <= track.position.y + track.height + 10;
        
        if(mouse.clicked() && mouse.getButtonID() == GLFW_MOUSE_BUTTON_LEFT) {
            if(hovering || dragging) {
                dragging = true;
                
                float time = (mx - track.position.x) / track.width;
                time = Math.max(0f, Math.min(1f, time));
                
                animator.setNormalizedTime(time);
                thumb.position.x = (int) (track.position.x + time * track.width) - 5;
                
                animator.pause();
            }
        } else {
            dragging = false;
        }
    }
    
}