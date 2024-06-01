package org.xjge.core;

import static org.xjge.core.Control.*;
import java.util.HashMap;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWGamepadState;
import org.lwjgl.system.MemoryStack;

//Created: May 3, 2021

/**
 * Represents a generic game controller held with two hands that exhibits two 
 * analog sticks, two or more triggers, and several buttons.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class Gamepad extends InputDevice {

    private GLFWGamepadState state;
    
    /**
     * Creates a new gamepad object and applies the users settings to its 
     * control configuration.
     * 
     * @param id       the unique number used to identify the device in 
     *                 other parts of the engine
     * @param controls a collection of various {@link Control} mappings
     * @param settings a collection containing additional user preferences
     */
    Gamepad(int id, HashMap<Control, Integer> controls, HashMap<String, Float> settings) {
        super(id, controls, settings);
        validate();
    }
    
    /**
     * Creates a new gamepad object from an existing one.
     * 
     * @param gamepad the gamepad to copy 
     */
    Gamepad(Gamepad gamepad) {
        super(gamepad);
        validate();
    }

    /**
     * Determines if this controller is eligible for use by the engine. GLFW 
     * recognizes Joysticks which exhibit predictable {@link Control} layouts 
     * as gamepads. Gamepads often include mappings for controls which 
     * correspond well to GLFWs definitions.
     */
    private void validate() {
        if(glfwJoystickIsGamepad(id)) {
            name = glfwGetGamepadName(id);
            
            try(MemoryStack stack = MemoryStack.stackPush()) {
                state = new GLFWGamepadState(stack.malloc(40));
                
                state.buttons(stack.malloc(15));
                state.axes(stack.mallocFloat(6));
            }
        } else {
            name = glfwGetJoystickName(id);
            
            Logger.setDomain("input");
            Logger.logWarning("Unsupported controller \"" + name + "\" connected at index " + id, null);
            Logger.setDomain(null);
        }
    }
    
    @Override
    protected void poll(double targetDelta, double trueDelta, Puppet puppet, Control control, Command command) {
        if(glfwJoystickPresent(id) && glfwGetGamepadState(id, state)) {
            switch(control) {
                case LEFT_STICK_X, LEFT_STICK_Y, RIGHT_STICK_X, RIGHT_STICK_Y -> {
                    command.execute(state.axes(controls.get(control)), 
                                    this, 
                                    control, 
                                    controls.get(control), 
                                    targetDelta, trueDelta);
                }

                case L2, R2 -> {
                    command.execute(state.axes(
                            controls.get(control)), 
                            this, 
                            control, 
                            controls.get(control), 
                            targetDelta, trueDelta);
                }

                default -> {
                    command.execute(
                            state.buttons(controls.get(control)), 
                            this, 
                            control, 
                            controls.get(control), 
                            targetDelta, trueDelta);
                }
            }
        }
    }

}