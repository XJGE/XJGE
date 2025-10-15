package org.xjge.test;

import java.util.Map;
import org.xjge.core.Control;
import org.xjge.core.Input;
import org.xjge.core.Mouse;
import org.xjge.core.SplitScreenType;
import org.xjge.core.Window;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Texture;
import org.xjge.ui.Icon;
import org.xjge.ui.Widget;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class WidgetCrystalBall extends Widget {

    private boolean spinning;
    
    private final TurnContext turnContext;
    private final Icon ballIcon = new Icon(new Texture("image_crystalball.png"), 100, 100, true);
    private final Icon wandIcon = new Icon(new Texture("image_wand.png"), 100, 20, false);
    private final Icon[] numbers = new Icon[6];
    
    WidgetCrystalBall(TurnContext turnContext) {
        this.turnContext = turnContext;
        
        ballIcon.scale.set(2f);
        wandIcon.scale.set(1.5f);
        wandIcon.rotation.z = -150f;
        
        Texture numberTexture = new Texture("image_numbers.png");
        
        for(int i = 0; i < numbers.length; i++) {
            numbers[i] = new Icon(numberTexture, 30, 30, true);
            
            int x = (i >= 2) ? i - 3 : i;
            int y = (i >= 2) ? 1 : 0;
            
            numbers[i].setSubImage(x, y);
            numbers[i].scale.set(2.5f);
        }
        
        relocate(Window.getSplitScreenType(), Window.getResolutionWidth(), Window.getResolutionHeight());
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        if(turnContext.unit.inputDeviceID == Input.KEYBOARD) {
            if(turnContext.unit.axisMoved(Control.RIGHT_STICK_X, 1)) {
                wandIcon.position.x = turnContext.unit.getInputValue(Control.RIGHT_STICK_X, 1);
            }
            if(turnContext.unit.axisMoved(Control.RIGHT_STICK_Y, 1)) {
                wandIcon.position.y = turnContext.unit.getInputValue(Control.RIGHT_STICK_Y, 1);
            }
        } else {
            if(turnContext.unit.axisMoved(Control.RIGHT_STICK_X, 0)) {
                wandIcon.position.x += turnContext.unit.getInputValue(Control.RIGHT_STICK_X, 0) * 4f;
            }
            if(turnContext.unit.axisMoved(Control.RIGHT_STICK_Y, 0)) {
                wandIcon.position.y += -(turnContext.unit.getInputValue(Control.RIGHT_STICK_Y, 0)) * 4f;
            }
        }
        
        /*
        1. Wave wand to "charge" the crystal ball, numbers will start spinning
        2. Once the ball is fully charged the numbers will continue to cycle
        3. Press the action button to select a number (light RNG)
        */
        
        if(spinning) {
            
        }
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        ballIcon.render();
        wandIcon.render();
        
        for(int i = 0; i < numbers.length; i++) {
            numbers[i].render();
        }
    }

    @Override
    public final void relocate(SplitScreenType splitType, int viewportWidth, int viewportHeight) {
        ballIcon.position.set(viewportWidth / 2, viewportHeight / 2);
        for(Icon number : numbers) number.position.set(ballIcon.position);
    }

    @Override
    public void processKeyboardInput(int key, int action, int mods, Character character) {}

    @Override
    public void processMouseInput(Mouse mouse) {}

    @Override
    public void delete() {
    }

}