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

    private double previousSpeed;
    
    private final ModelAnimator animator;
    private final Rectangle bounds;
    private final String type;
    private String label;
    
    private final Vector2i textPos  = new Vector2i();
    private final Color boundsColor = new Color(0.5f);
    
    UILabelButton(ModelAnimator animator, String label, int width) {
        this.animator = animator;
        this.label    = label;
        
        type   = label;
        bounds = new Rectangle(0, 0, width, 30);
    }
    
    void render() {
        switch(type) {
            case "Pause" -> {
                label = animator.getSpeed() == 0 ? "Play" : "Pause";
            }
        }
        
        bounds.render(1f, boundsColor);
        
        textPos.x = (bounds.positionX + (bounds.width / 2)) - (Font.FALLBACK.lengthInPixels(label) / 2);
        Font.FALLBACK.drawString(label, textPos.x, textPos.y, Color.WHITE, 1f);
    }
    
    void relocate(Rectangle parent, int offsetX, int offsetY) {
        bounds.positionX = parent.positionX + offsetX;
        bounds.positionY = parent.positionY + offsetY;
        textPos.y        = bounds.positionY + 8;
    }
    
    void processMouseInput(Mouse mouse, UISpinbox speedControl) {
        boundsColor.set(mouse.hovered(bounds) ? 0.75f : 0.5f);
        
        if(mouse.clickedOnce(bounds, GLFW_MOUSE_BUTTON_LEFT)) {
            switch(type) {
                case "Pause" -> {
                    if(animator.getSpeed() == 0) {
                        animator.setSpeed(previousSpeed);
                    } else {
                        previousSpeed = animator.getSpeed();
                        animator.setSpeed(0);
                    }
                }
                case "Reset Speed" -> {
                    animator.setSpeed(1.0);
                }
            }
        }
    }
    
}