package org.xjge.core;

import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWGamepadState;
import static org.xjge.core.Control.*;

/**
 * 
 * @author J Hoffman
 * @since 
 */
final class InputDeviceGamepad extends InputDevice2 {
    
    private GLFWGamepadState state;
    
    InputDeviceGamepad(int id) {
        super(id);
        validate();
    }
    
    void freeState() {
        if(state != null) {
            state.free();
            state = null;
            connected = false;
        }
    }

    final boolean validate() {
        freeState();
        
        if(glfwJoystickPresent(id)) {
            boolean isGamepad = glfwJoystickIsGamepad(id);
            
            name = (isGamepad && glfwGetGamepadName(id) != null)
                 ? glfwGetGamepadName(id)
                 : glfwGetJoystickName(id);
            
            if(isGamepad) state = GLFWGamepadState.create();
            
            connected = true;
            return isGamepad;
            
        } else {
            connected = false;
            return false;
        }
    }
    
    @Override
    protected void captureControlState() {
        if(glfwJoystickPresent(id) && glfwGetGamepadState(id, state)) {
            for(Control control : Control.values()) {
                switch(control) {
                    case LEFT_STICK_X, LEFT_STICK_Y, RIGHT_STICK_X, RIGHT_STICK_Y, L2, R2 -> {
                        controlValues.put(control, state.axes(controlBindings.get(control)));
                    }
                    default -> {
                        controlValues.put(control, (float)state.buttons(controlBindings.get(control)));
                    }
                }
            }
        }
    }

}