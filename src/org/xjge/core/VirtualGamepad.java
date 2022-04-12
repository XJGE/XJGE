package org.xjge.core;

import java.util.HashMap;
import static org.lwjgl.glfw.GLFW.glfwGetGamepadState;
import static org.xjge.core.Control.LEFT_STICK_Y;
import static org.xjge.core.Control.R2;
import static org.xjge.core.Control.RIGHT_STICK_X;
import static org.xjge.core.Control.RIGHT_STICK_Y;

/**
 * Apr 12, 2022
 */

/**
 * @author J Hoffman
 * @since  2.1.6
 */
class VirtualGamepad extends InputDevice {

    private class VirtualState {
        
    }
    
    VirtualGamepad(int id, HashMap<Control, Integer> controls, HashMap<String, Float> settings) {
        super(id, controls, settings);
        name    = "virtual gamepad " + id;
        enabled = false;
    }

    @Override
    protected void poll() {
        if(!puppets.empty() && puppets.peek() != null) {
            puppets.peek().commands.forEach((control, command) -> {
                switch(control) {
                    case LEFT_STICK_X, LEFT_STICK_Y, RIGHT_STICK_X, RIGHT_STICK_Y -> {
                        command.execute(state.axes(controls.get(control)), this, control);
                    }
                    
                    case L2, R2 -> command.execute(state.axes(controls.get(control)), this, control);
                    
                    default -> command.execute(state.buttons(controls.get(control)), this, control);
                }
            });
        }
        
        resolvePuppetSetRequest();
    }
    
}