package org.xjge.core;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
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
        
        settings.put("leftDeadzone", 0.15f);
        settings.put("rightDeadzone", 0.15f);
        
        controlBindings.put(CROSS,         GLFW_GAMEPAD_BUTTON_A);
        controlBindings.put(CIRCLE,        GLFW_GAMEPAD_BUTTON_B);
        controlBindings.put(SQUARE,        GLFW_GAMEPAD_BUTTON_X);
        controlBindings.put(TRIANGLE,      GLFW_GAMEPAD_BUTTON_Y);
        controlBindings.put(L1,            GLFW_GAMEPAD_BUTTON_LEFT_BUMPER);
        controlBindings.put(R1,            GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER);
        controlBindings.put(SHARE,         GLFW_GAMEPAD_BUTTON_BACK);
        controlBindings.put(OPTIONS,       GLFW_GAMEPAD_BUTTON_START);
        controlBindings.put(PS_BUTTON,     GLFW_GAMEPAD_BUTTON_GUIDE);
        controlBindings.put(L3,            GLFW_GAMEPAD_BUTTON_LEFT_THUMB);
        controlBindings.put(R3,            GLFW_GAMEPAD_BUTTON_RIGHT_THUMB);
        controlBindings.put(DPAD_UP,       GLFW_GAMEPAD_BUTTON_DPAD_UP);
        controlBindings.put(DPAD_RIGHT,    GLFW_GAMEPAD_BUTTON_DPAD_RIGHT);
        controlBindings.put(DPAD_DOWN,     GLFW_GAMEPAD_BUTTON_DPAD_DOWN);
        controlBindings.put(DPAD_LEFT,     GLFW_GAMEPAD_BUTTON_DPAD_LEFT);
        controlBindings.put(LEFT_STICK_X,  GLFW_GAMEPAD_AXIS_LEFT_X);
        controlBindings.put(LEFT_STICK_Y,  GLFW_GAMEPAD_AXIS_LEFT_Y);
        controlBindings.put(RIGHT_STICK_X, GLFW_GAMEPAD_AXIS_RIGHT_X);
        controlBindings.put(RIGHT_STICK_Y, GLFW_GAMEPAD_AXIS_RIGHT_Y);
        controlBindings.put(L2,            GLFW_GAMEPAD_AXIS_LEFT_TRIGGER);
        controlBindings.put(R2,            GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER);
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
        if(!glfwJoystickPresent(id)) {
            connected = false;
            return;
        }
        
        if(state != null && glfwGetGamepadState(id, state)) {
            for(Control control : Control.values()) {
                int binding = controlBindings.get(control);
                
                switch(control) {
                    case LEFT_STICK_X, LEFT_STICK_Y, RIGHT_STICK_X, RIGHT_STICK_Y, L2, R2 -> {
                        controlValues.put(control, state.axes(binding));
                    }
                    default -> {
                        controlValues.put(control, state.buttons(binding) == GLFW_PRESS ? 1f : 0f);
                    }
                }
            }
        } else {
            FloatBuffer axes   = glfwGetJoystickAxes(id);
            ByteBuffer buttons = glfwGetJoystickButtons(id);
            
            for(Control control : Control.values()) {
                int binding = controlBindings.get(control);
                
                switch(control) {
                    case LEFT_STICK_X, LEFT_STICK_Y, RIGHT_STICK_X, RIGHT_STICK_Y, L2, R2 -> {
                        if(axes != null && binding >= 0 && binding < axes.limit()) {
                            controlValues.put(control, axes.get(binding));
                        }
                    }
                    default -> {
                        if(buttons != null && binding >= 0 && binding < buttons.limit()) {
                            controlValues.put(control, buttons.get(binding) == GLFW_PRESS ? 1f : 0f);
                        }
                    }
                }
            }
        }
        
        connected = true;
    }

}