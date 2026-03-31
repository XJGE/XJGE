package org.xjge.test;

import org.xjge.graphics.Color;
import org.xjge.graphics.ModelAnimator;
import org.xjge.ui.Rectangle;

/**
 * 
 * @author J Hoffman
 * @since 
 */
final class UISlider {

    public Rectangle track = new Rectangle(0, 0, 300, 6);
    public Rectangle thumb = new Rectangle();
    
    private ModelAnimator animator;
    
    UISlider(ModelAnimator animator) {
        this.animator = animator;
    }
    
    void update() {
        
    }
    
    void render() {
        track.render(1f, Color.GRAY);
    }
    
    void relocate(Rectangle parent) {
        track.positionX = parent.positionX + 10;
        track.positionY = parent.positionY + parent.height - 120;
    }
    
}