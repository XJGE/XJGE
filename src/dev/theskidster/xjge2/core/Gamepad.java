package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.input.Control;
import static dev.theskidster.xjge2.input.Control.*;
import java.util.HashMap;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWGamepadState;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: May 3, 2021
 */

final class Gamepad extends InputDevice {

    private GLFWGamepadState state;
    
    Gamepad(int id, float sensitivity, HashMap<Control, Integer> config) {
        super(id, sensitivity, config);
        validate();
    }
    
    Gamepad(Gamepad gamepad) {
        super(gamepad);
        validate();
    }

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
            Logger.logWarning("Unsupported controller \"" + name + 
                              "\" connected at index " + id + ".", 
                              null);
            Logger.setDomain(null);
        }
    }
    
    @Override
    protected void poll() {
        if(glfwGetGamepadState(id, state) && !puppets.empty() && puppets.peek() != null) {
            puppets.peek().commands.forEach((control, command) -> {
                switch(control) {
                    case LEFT_STICK_X, LEFT_STICK_Y, RIGHT_STICK_X, RIGHT_STICK_Y -> {
                        if(Math.abs(state.axes(config.get(control))) >= sensitivity) {
                            command.execute(state.axes(config.get(control)), this, control);
                        }
                    }
                    
                    case L2, R2 -> command.execute(state.axes(config.get(control)), this, control);
                    
                    default -> command.execute(state.buttons(config.get(control)), this, control);
                }
            });
        }
        
        resolvePuppetSetRequest();
    }

}