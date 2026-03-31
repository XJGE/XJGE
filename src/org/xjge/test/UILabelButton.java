package org.xjge.test;

import org.xjge.graphics.ModelAnimator;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class UILabelButton {

    private final int width;
    
    private final ModelAnimator animator;
    private final String label;
    
    UILabelButton(ModelAnimator animator, String label, int width) {
        this.animator = animator;
        this.label    = label;
        this.width    = width;
    }
    
    void render() {
        
    }
    
}