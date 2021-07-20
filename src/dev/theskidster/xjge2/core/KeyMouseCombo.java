package dev.theskidster.xjge2.core;

import static dev.theskidster.xjge2.core.Control.*;
import java.nio.DoubleBuffer;
import java.util.HashMap;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.system.MemoryStack;

//Created: May 3, 2021

/**
 * A virtual input device comprised of the keyboard and mouse that attempts to 
 * mimic the actions of a {@link Gamepad}.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class KeyMouseCombo extends InputDevice {

    int[] axisValues = new int[4];
    
    private float prevAxisX = Window.getWidth() / 2;
    private float prevAxisY = Window.getHeight() / 2;
    
    private boolean firstMouse = true;
    
    private final DoubleBuffer cursorPosX;
    private final DoubleBuffer cursorPosY;
    
    /**
     * Creates a new KeyMouseCombo object and applies the users settings to its 
     * control configuration.
     * 
     * @param id          the unique number used to identify the device in 
     *                    other parts of the engine
     * @param sensitivity a value used by gameplay systems to adjust the 
     *                    responsiveness of input actions based off the users 
     *                    preference
     * @param deadzone    a value used to indicate how much pressure must be 
     *                    applied to an analog stick before its input is 
     *                    recognized
     * @param config      a collection of various {@link Control} mappings
     */
    KeyMouseCombo(int id, float sensitivity, float deadzone, HashMap<Control, Integer> config) {
        super(id, sensitivity, deadzone, config);
        
        name = "KeyMouseCombo";
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            cursorPosX = stack.mallocDouble(1);
            cursorPosY = stack.mallocDouble(1);
        }
    }
    
    /**
     * Calculates an input value of the virtual right analog stick by using the 
     * current and previous positions of the mouse cursor.
     * 
     * @param currValue the current position of the mouse cursor along a single 
     *                  axis
     * @param prevValue the previous position of the mouse cursor along a 
     *                  single axis
     * 
     * @return a value indicating the intensity of the movement
     */
    private float findAxisValue(float currValue, float prevValue) {
        if(firstMouse) {
            prevValue  = currValue;
            firstMouse = false;
        }
        
        return (currValue - prevValue) * sensitivity;
    }
    
    @Override
    protected void poll() {
        if(!puppets.empty() && puppets.peek() != null) {
            puppets.peek().commands.forEach((control, command) -> {
                switch(control) {
                    case LEFT_STICK_X, LEFT_STICK_Y -> {
                        int key1 = (config.get(control) & ((control == LEFT_STICK_X) ? axisValues[0] : axisValues[2]));
                        int key2 = (config.get(control) & ((control == LEFT_STICK_X) ? axisValues[1] : axisValues[3]));
                        
                        if(glfwGetKey(Window.HANDLE, key1) == GLFW_PRESS) {
                            command.execute(-1, this, control);
                        } else if(glfwGetKey(Window.HANDLE, key2) == GLFW_PRESS) {
                            command.execute(1, this, control);
                        }
                    }
                    
                    case RIGHT_STICK_X ->  {
                        glfwGetCursorPos(Window.HANDLE, cursorPosX, cursorPosY);

                        if((float) cursorPosX.get(0) != prevAxisX) {
                            command.execute(findAxisValue((float) cursorPosX.get(0), prevAxisX), this, control);
                            prevAxisX = (float) cursorPosX.get(0);
                        }
                    }
                        
                    case RIGHT_STICK_Y -> {
                        glfwGetCursorPos(Window.HANDLE, cursorPosX, cursorPosY);
                        
                        if((float) cursorPosY.get(0) != prevAxisY) {
                            command.execute(findAxisValue((float) cursorPosY.get(0), prevAxisY), this, control);
                            prevAxisY = (float) cursorPosY.get(0);
                        }
                    }
                        
                    case L2, R2 -> command.execute(glfwGetMouseButton(Window.HANDLE, config.get(control)), this, control);
                    
                    default -> command.execute(glfwGetKey(Window.HANDLE, config.get(control)), this, control);
                }
            });
        }
        
        resolvePuppetSetRequest();
    }

}