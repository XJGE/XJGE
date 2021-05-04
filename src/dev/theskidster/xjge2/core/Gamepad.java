package dev.theskidster.xjge2.core;

import static dev.theskidster.xjge2.core.Control.*;
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
    
    private final HashMap<Control, Integer> buttons = new HashMap<>();
    private final HashMap<Control, Integer> axes    = new HashMap<>();
    
    Gamepad(int id) {
        super(id);
        
        validate();
        
        buttons.put(CROSS,      GLFW_GAMEPAD_BUTTON_A);
        buttons.put(CIRCLE,     GLFW_GAMEPAD_BUTTON_B);
        buttons.put(SQUARE,     GLFW_GAMEPAD_BUTTON_X);
        buttons.put(TRIANGLE,   GLFW_GAMEPAD_BUTTON_Y);
        buttons.put(L1,         GLFW_GAMEPAD_BUTTON_LEFT_BUMPER);
        buttons.put(R1,         GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER);
        buttons.put(SHARE,      GLFW_GAMEPAD_BUTTON_BACK);
        buttons.put(OPTIONS,    GLFW_GAMEPAD_BUTTON_START);
        buttons.put(PS_BUTTON,  GLFW_GAMEPAD_BUTTON_GUIDE);
        buttons.put(L3,         GLFW_GAMEPAD_BUTTON_LEFT_THUMB);
        buttons.put(R3,         GLFW_GAMEPAD_BUTTON_RIGHT_THUMB);
        buttons.put(DPAD_UP,    GLFW_GAMEPAD_BUTTON_DPAD_UP);
        buttons.put(DPAD_RIGHT, GLFW_GAMEPAD_BUTTON_DPAD_RIGHT);
        buttons.put(DPAD_DOWN,  GLFW_GAMEPAD_BUTTON_DPAD_DOWN);
        buttons.put(DPAD_LEFT,  GLFW_GAMEPAD_BUTTON_DPAD_LEFT);
        
        axes.put(LEFT_STICK_X,  GLFW_GAMEPAD_AXIS_LEFT_X);
        axes.put(LEFT_STICK_Y,  GLFW_GAMEPAD_AXIS_LEFT_Y);
        axes.put(RIGHT_STICK_X, GLFW_GAMEPAD_AXIS_RIGHT_X);
        axes.put(RIGHT_STICK_Y, GLFW_GAMEPAD_AXIS_RIGHT_Y);
        axes.put(L2,            GLFW_GAMEPAD_AXIS_LEFT_TRIGGER);
        axes.put(R2,            GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER);
    }
    
    Gamepad(Gamepad gamepad) {
        super(gamepad);
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
            puppets.peek().commands.forEach((widget, command) -> {
                switch(widget) {
                    case LEFT_STICK_X, LEFT_STICK_Y, RIGHT_STICK_X, RIGHT_STICK_Y -> {
                        if(Math.abs(state.axes(axes.get(widget))) >= sensitivity) {
                            command.execute(state.axes(axes.get(widget)), this);
                        }
                    }
                    
                    case L2, R2 -> command.execute(state.axes(axes.get(widget)), this);
                    
                    default -> command.execute(state.buttons(buttons.get(widget)), this);
                }
            });
        }
        
        resolvePuppetSetRequest();
    }

    @Override
    protected void importControlConfig(String filename) {
    }

    @Override
    protected void exportControlConfig() {
    }

}