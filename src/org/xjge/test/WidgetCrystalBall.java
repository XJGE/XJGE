package org.xjge.test;

import java.util.Map;
import org.xjge.core.Control;
import org.xjge.core.Input;
import org.xjge.core.Mouse;
import org.xjge.core.SplitScreenType;
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

    private final TurnContext turnContext;
    private final Icon wandIcon = new Icon(new Texture("image_wand.png"), 100, 20, false);
    
    WidgetCrystalBall(TurnContext turnContext) {
        this.turnContext = turnContext;
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
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        wandIcon.render();
    }

    @Override
    public void relocate(SplitScreenType splitType, int viewportWidth, int viewportHeight) {
    }

    @Override
    public void processKeyboardInput(int key, int action, int mods, Character character) {
    }

    @Override
    public void processMouseInput(Mouse mouse) {
    }

    @Override
    public void delete() {
    }

}