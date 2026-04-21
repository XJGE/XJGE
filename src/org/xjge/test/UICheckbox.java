package org.xjge.test;

import org.joml.Vector2i;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import org.xjge.core.Mouse;
import org.xjge.graphics.Color;
import org.xjge.graphics.ModelAnimator;
import org.xjge.graphics.Texture;
import org.xjge.ui.Font;
import org.xjge.ui.Icon;
import org.xjge.ui.Rectangle;

/**
 * 
 * @author J Hoffman
 * @since 
 */
final class UICheckbox {

    private final Vector2i textPos = new Vector2i();
    private final Rectangle hitbox = new Rectangle(0, 0, 22, 22);
    private final Icon icon;
    private final ModelAnimator animator;
    
    UICheckbox(ModelAnimator animator, Texture iconsTexture) {
        this.animator = animator;
        icon = new Icon(iconsTexture, 24, 24, false);
    }
    
    void render() {
        //TODO: gray out if no anim is selected or playing
        Font.FALLBACK.drawString("Loop", textPos.x, textPos.y, Color.WHITE, 1f);
        icon.setSubImage(animator.isLooping() ? 2 : 3, 0);
        icon.render();
    }
    
    void relocate(Rectangle parent) {
        textPos.x = parent.positionX + 10;
        textPos.y = parent.positionY - 35;
        hitbox.positionX = parent.positionX + 65;
        hitbox.positionY = parent.positionY - 40;
        icon.position.set(hitbox.positionX, hitbox.positionY);
    }
    
    void onClick(Mouse mouse) {
        if(mouse.clickedOnce(hitbox, GLFW_MOUSE_BUTTON_LEFT)) {
            animator.setLooping(!animator.isLooping());
        }
    }
    
}