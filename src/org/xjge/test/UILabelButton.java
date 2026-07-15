package org.xjge.test;

import org.joml.Vector2i;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import org.xjge.core.Mouse;
import org.xjge.graphics.Color;
import org.xjge.graphics.ModelAnimator;
import org.xjge.ui.Font;
import org.xjge.ui.Rectangle;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class UILabelButton {
    
    private final ModelAnimator animator;
    private final Rectangle bounds;
    private final String type;
    private String label;
    
    private final Vector2i textPos = new Vector2i();
    
    UILabelButton(ModelAnimator animator, String label, int width) {
        this.animator = animator;
        this.label    = label;
        
        type   = label;
        bounds = new Rectangle(width, 30, 0, 0, new Color(0.5f), 1f);
    }
    
    void render() {
        switch(type) {
            case "Pause" -> {
                label = animator.isPlaying() ? "Pause" : "Play";
            }
        }
        
        bounds.render();
        
        textPos.x = (bounds.position.x + (bounds.width / 2)) - (Font.FALLBACK.lengthInPixels(label) / 2);
        Font.FALLBACK.drawString(label, textPos.x, textPos.y, Color.WHITE, 1f);
    }
    
    void relocate(Rectangle parent, int offsetX, int offsetY) {
        bounds.position.x = parent.position.x + offsetX;
        bounds.position.y = parent.position.y + offsetY;
        textPos.y         = bounds.position.y + 8;
    }
    
    void processMouseInput(Mouse mouse, UISpinbox speedControl) {
        bounds.color.set(mouse.hovered(bounds) ? 0.75f : 0.5f);
        
        if(mouse.clickedOnce(bounds, GLFW_MOUSE_BUTTON_LEFT)) {
            switch(type) {
                case "Pause" -> {
                    if(animator.isPlaying()) animator.pause();
                    else animator.play();
                }
                case "Reset Speed" -> {
                    animator.setSpeed(1.0);
                }
            }
        }
    }
    
}