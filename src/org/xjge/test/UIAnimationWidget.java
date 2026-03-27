package org.xjge.test;

import java.awt.Checkbox;
import java.util.Map;
import org.xjge.core.Mouse;
import org.xjge.core.SplitScreenType;
import org.xjge.core.Window;
import org.xjge.graphics.Color;
import org.xjge.graphics.ModelAnimator;
import org.xjge.graphics.Shader;
import org.xjge.graphics.Texture;
import org.xjge.ui.Rectangle;
import org.xjge.ui.Widget;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class UIAnimationWidget extends Widget {

    private final Color slateGray = new Color(0.25f);
    
    private final Texture iconsTexture    = Texture.load("icons_animator.png");
    private final Rectangle[] backgrounds = new Rectangle[3];
    
    private final UICheckbox loopingControl;
    
    UIAnimationWidget(ModelAnimator animator) {
        for(int i = 0; i < backgrounds.length; i++) backgrounds[i] = new Rectangle();
        
        loopingControl = new UICheckbox(animator, iconsTexture);
        
        relocate(Window.getSplitScreenType(), Window.getWidth(), Window.getHeight());
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
    }

    @Override
    public void render(Map<String, Shader> glPrograms) {
        for(int i = 0; i < backgrounds.length; i++) {
            var background = backgrounds[i];
            
            var color = switch(i) {
                case 0  -> Color.BLACK;
                default -> slateGray;
            };
            
            background.render(1f, color);
        }
        
        loopingControl.render();
    }

    @Override
    public final void relocate(SplitScreenType splitType, int viewportWidth, int viewportHeight) {
        backgrounds[0].width     = 330;
        backgrounds[0].height    = 390;
        backgrounds[0].positionX = viewportWidth - backgrounds[0].width - 5;
        backgrounds[0].positionY = viewportHeight - backgrounds[0].height - 5;
        
        backgrounds[1].width     = 320;
        backgrounds[1].height    = 120;
        backgrounds[1].positionX = backgrounds[0].positionX + 5;
        backgrounds[1].positionY = (backgrounds[0].positionY + backgrounds[0].height) - (backgrounds[1].height + 5);
        
        backgrounds[2].width     = 320;
        backgrounds[2].height    = 255;
        backgrounds[2].positionX = backgrounds[0].positionX + 5;
        backgrounds[2].positionY = backgrounds[0].positionY + 5;
        
        loopingControl.relocate(backgrounds[1]);
    }

    @Override
    public void processKeyboardInput(int key, int action, int mods, Character character) {
    }

    @Override
    public void processMouseInput(Mouse mouse) {
        loopingControl.onClick(mouse);
    }

    @Override
    public void delete() {
    }

}