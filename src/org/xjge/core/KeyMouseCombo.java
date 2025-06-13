package org.xjge.core;

import static org.xjge.core.Control.*;
import java.nio.DoubleBuffer;
import java.util.HashMap;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.system.MemoryStack;

/**
 * Created: May 3, 2021
 * <p>
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
     * @param id the unique number used to identify the device in other parts of
     *           the engine
     * @param controls a collection of various {@link Control} mappings
     * @param settings a collection containing additional user preferences
     */
    KeyMouseCombo(int id, HashMap<Control, Integer> controls, HashMap<String, Float> settings) {
        super(id, controls, settings);
        
        name = "Keyboard and Mouse";
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            cursorPosX = stack.mallocDouble(1);
            cursorPosY = stack.mallocDouble(1);
        }
    }
    
    /**
     * Calculates an input value of the virtual right analog stick by using the 
     * current and previous positions of the mouse cursor.
     * <p>
     * NOTE: You may want to attenuate the value produced by this method with 
     * some sort of sensitivity setting.
     * 
     * @param currValue the current position of the mouse cursor along a single 
     *                  axis
     * @param prevValue the previous position of the mouse cursor along a 
     *                  single axis
     * 
     * @return a value indicating the intensity of the movement
     * 
     * @see Input#setDeviceSetting(int, String, float) 
     */
    private float findAxisValue(float currValue, float prevValue) {
        if(firstMouse) {
            prevValue  = currValue;
            firstMouse = false;
        }
        
        return (currValue - prevValue);
    }
    
    @Override
    protected void poll(double targetDelta, double trueDelta, Puppet puppet, Control control, Command command) {
        //TODO: reimplement this or use callback
        
        /*
        switch(control) {
            case LEFT_STICK_X, LEFT_STICK_Y -> {
                int key1 = (controls.get(control) & ((control == LEFT_STICK_X) ? axisValues[0] : axisValues[2]));
                int key2 = (controls.get(control) & ((control == LEFT_STICK_X) ? axisValues[1] : axisValues[3]));

                if(glfwGetKey(Window.HANDLE, key1) == GLFW_PRESS) {
                    command.execute(-1, this, control, controls.get(control), targetDelta, trueDelta);
                } else if(glfwGetKey(Window.HANDLE, key2) == GLFW_PRESS) {
                    command.execute(1, this, control, controls.get(control), targetDelta, trueDelta);
                } else {
                    command.execute(0, this, control, controls.get(control), targetDelta, trueDelta);
                }
            }

            case RIGHT_STICK_X ->  {
                glfwGetCursorPos(Window.HANDLE, cursorPosX, cursorPosY);

                if((float) cursorPosX.get(0) != prevAxisX) {
                    command.execute(findAxisValue((float) cursorPosX.get(0), prevAxisX), 
                                    this, 
                                    control, 
                                    controls.get(control), 
                                    targetDelta, trueDelta);
                    prevAxisX = (float) cursorPosX.get(0);
                }
            }

            case RIGHT_STICK_Y -> {
                glfwGetCursorPos(Window.HANDLE, cursorPosX, cursorPosY);

                if((float) cursorPosY.get(0) != prevAxisY) {
                    command.execute(findAxisValue((float) cursorPosY.get(0), prevAxisY), 
                                    this, 
                                    control, 
                                    controls.get(control), 
                                    targetDelta, trueDelta);
                    prevAxisY = (float) cursorPosY.get(0);
                }
            }

            case L2, R2 -> {
                command.execute(glfwGetMouseButton(Window.HANDLE, controls.get(control)), 
                                this, control, 
                                controls.get(control), 
                                targetDelta, trueDelta);
            }

            default -> {
                command.execute(glfwGetKey(Window.HANDLE, controls.get(control)), 
                                this, 
                                control, 
                                controls.get(control), 
                                targetDelta, trueDelta);
            }
        }
        */
    }

}