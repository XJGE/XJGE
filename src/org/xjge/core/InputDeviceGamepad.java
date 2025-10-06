package org.xjge.core;

import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWGamepadState;
import org.lwjgl.system.MemoryStack;
import static org.xjge.core.Control.*;

/**
 * 
 * @author J Hoffman
 * @since 
 */
final class InputDeviceGamepad extends InputDevice2 {

    private GLFWGamepadState state;
    
    InputDeviceGamepad(int id) {
        super(id, glfwJoystickIsGamepad(id) ? glfwGetGamepadName(id) : glfwGetJoystickName(id));
        
        if(glfwJoystickIsGamepad(id)) {
            try(MemoryStack stack = MemoryStack.stackPush()) {
                state = new GLFWGamepadState(stack.malloc(40));
                state.buttons(stack.malloc(15));
                state.axes(stack.mallocFloat(6));
            }
        } else {
            Logger.logWarning("Unsupported controller \"" + name + "\" connected at index " + id, null);
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