package org.xjge.test;

import java.util.Map;
import org.joml.Vector2i;
import org.xjge.core.Mouse;
import org.xjge.core.SplitScreenType;
import org.xjge.core.Window;
import org.xjge.graphics.Color;
import org.xjge.graphics.ModelAnimator;
import org.xjge.graphics.Shader;
import org.xjge.graphics.Texture;
import org.xjge.ui.Font;
import org.xjge.ui.Rectangle;
import org.xjge.ui.Widget;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class UIAnimationWidget extends Widget {

    private final Vector2i textPos = new Vector2i();
    
    private final Color slateGray = new Color(0.25f);
    
    private final Texture iconsTexture    = Texture.load("icons_animator.png");
    private final Rectangle[] backgrounds = new Rectangle[3];
    
    private final UICheckbox loopingControl;
    private final UISpinbox speedControl;
    private final UISpinbox timeControl;
    private final UISlider timeSlider;
    private final UILabelButton pausePlayButton;
    private final UILabelButton resetSpeedButton;
    
    UIAnimationWidget(ModelAnimator animator) {
        for(int i = 0; i < backgrounds.length; i++) backgrounds[i] = new Rectangle();
        
        loopingControl   = new UICheckbox(animator, iconsTexture);
        speedControl     = new UISpinbox(animator, 90, iconsTexture, "Speed");
        timeControl      = new UISpinbox(animator, 90, iconsTexture, "Animation Time");
        timeSlider       = new UISlider(animator);
        pausePlayButton  = new UILabelButton(animator, "Pause", 300);
        resetSpeedButton = new UILabelButton(animator, "Reset Speed", 300);
        
        relocate(Window.getSplitScreenType(), Window.getWidth(), Window.getHeight());
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        speedControl.update();
        timeControl.update();
        timeSlider.update();
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
        
        textPos.x = backgrounds[2].positionX + 132;
        textPos.y = backgrounds[2].positionY + backgrounds[2].height - 32;
        Font.FALLBACK.drawString("Speed", textPos.x, textPos.y, Color.WHITE, 1f);
        
        textPos.x = backgrounds[2].positionX + 10;
        textPos.y = backgrounds[2].positionY + backgrounds[2].height - 78;
        Font.FALLBACK.drawString("Animation Time", textPos.x, textPos.y, Color.WHITE, 1f);
        
        loopingControl.render();
        speedControl.render();
        timeControl.render();
        timeSlider.render();
        pausePlayButton.render();
        resetSpeedButton.render();
    }

    @Override
    public final void relocate(SplitScreenType splitType, int viewportWidth, int viewportHeight) {
        backgrounds[0].width     = 330;
        backgrounds[0].height    = 370;
        backgrounds[0].positionX = viewportWidth - backgrounds[0].width - 5;
        backgrounds[0].positionY = viewportHeight - backgrounds[0].height - 5;
        
        backgrounds[1].width     = 320;
        backgrounds[1].height    = 120;
        backgrounds[1].positionX = backgrounds[0].positionX + 5;
        backgrounds[1].positionY = (backgrounds[0].positionY + backgrounds[0].height) - (backgrounds[1].height + 5);
        
        backgrounds[2].width     = 320;
        backgrounds[2].height    = 235;
        backgrounds[2].positionX = backgrounds[0].positionX + 5;
        backgrounds[2].positionY = backgrounds[0].positionY + 5;
        
        loopingControl.relocate(backgrounds[1]);
        speedControl.relocate(backgrounds[2], 200, 38);
        timeControl.relocate(backgrounds[2], 200, 86);
        timeSlider.relocate(backgrounds[2]);
        pausePlayButton.relocate(backgrounds[2], 10, 55);
        resetSpeedButton.relocate(backgrounds[2], 10, 10);
    }

    @Override
    public void processKeyboardInput(int key, int action, int mods, Character character) {
        speedControl.processKeyInput(key, action, mods);
        timeControl.processKeyInput(key, action, mods);
    }

    @Override
    public void processMouseInput(Mouse mouse) {
        loopingControl.onClick(mouse);
        speedControl.processMouseInput(mouse);
        timeControl.processMouseInput(mouse);
        timeSlider.processMouseInput(mouse);
        pausePlayButton.processMouseInput(mouse, speedControl);
        resetSpeedButton.processMouseInput(mouse, speedControl);
    }

    @Override
    public void delete() {
    }

}