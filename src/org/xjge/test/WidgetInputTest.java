package org.xjge.test;

import java.util.Map;
import org.xjge.core.Control;
import static org.xjge.core.Control.*;
import org.xjge.core.ControlState;
import org.xjge.core.Controllable;
import org.xjge.core.ControllableAction;
import org.xjge.core.Mouse;
import org.xjge.core.SplitScreenType;
import org.xjge.graphics.GLProgram;
import org.xjge.ui.Widget;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class WidgetInputTest extends Widget {

    private final Controllable controllable = new Controllable("test");
    
    private class InputState extends ControllableAction {
        boolean axisMoved;
        boolean buttonPressed;
        boolean buttonPressedOnce;
        boolean triggerPulled;
        float inputValue;
        
        @Override
        public void perform(ControlState controlState, double targetDelta, double trueDelta) {
            axisMoved = controlState.axisMoved();
            buttonPressed = controlState.buttonPressed();
            buttonPressedOnce = controlState.buttonPressedOnce();
            triggerPulled = controlState.triggerPulled();
            inputValue = controlState.getInputValue();
        }
        
    }
    
    WidgetInputTest() {
        for(Control control : Control.values()) {
            controllable.actions.put(control, new InputState());
        }
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
    }

    @Override
    public void relocate(SplitScreenType splitType, int viewportWidth, int viewportHeight) {
    }

    @Override
    public void processKeyboardInput(int key, int action, int mods) {
    }

    @Override
    public void processMouseInput(Mouse mouse) {
    }

    @Override
    public void delete() {
    }

}