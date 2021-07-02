package dev.theskidster.xjge2.core;

import static dev.theskidster.xjge2.core.Control.*;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

/**
 * @author J Hoffman
 * Created: May 3, 2021
 */

/**
 * Abstraction of the Gang of Four pattern by the same name- command objects serve as a point at which a {@link Control} from an 
 * {@link InputDevice} can be coupled to various player actions and passed to a {@link Puppet} object. 
 * <br><br>
 * More generally, whenever you want to create a new action the player can perform- such as walking, jumping, or shooting, you'll define 
 * that behavior in an object that extends this class. Objects of this type are automatically supplied with information about the current 
 * state of the players input device. This information in conjunction with several convenience methods provided by the parent class can be 
 * used to create player actions that exhibit a high level of fidelity.
 * <br><br>
 * The command superclass lacks constructors by design to encourage subclasses to define their own. This open-ended approach allows 
 * subclasses to capture whatever supplemental information they require to sufficiently perform the player actions they've been assigned to.
 * 
 * @see axisMoved()
 * @see buttonPressed()
 * @see buttonPressedOnce()
 * @see getInputValue()
 * @see triggerPulled()
 */
public abstract class Command {

    private float inputValue;
    
    private boolean pressRequested = true;
    
    private InputDevice device;
    private Control control;
    
    /**
     * Captures the state of the input device executing this command and provides it to the subclass.
     * 
     * @param inputValue the floating point value of a single interactive component
     * @param device     the input device that is executing this command
     * @param control    the interactive component being acted on
     */
    void execute(float inputValue, InputDevice device, Control control) {
        this.inputValue = inputValue;
        this.device     = device;
        this.control    = control;
        
        execute();
    }
    
    /**
     * Organizes the input logic used to control player actions. This method is called automatically once every game tick.
     */
    public abstract void execute();
    
    /**
     * Provides subclasses with a value describing the current state of the {@link Control} coupled to this command. The value provided 
     * may be between -1 to 1 or 0 to 1 depending on the input device used and the interactive component being pressed.
     * 
     * @return a floating point value of a single interactive component
     */
    protected float getInputValue() {
        return inputValue;
    }
    
    /**
     * Provides subclasses with the sensitivity preferences of the input device being used to execute this command.
     * 
     * @return a floating point value denoting the input devices desired sensitivity to changes in {@link Control} input values
     */
    protected float getDeviceSensitivity() {
        return device.sensitivity;
    }
    
    /**
     * Convenience method used to check if the input state of the {@link Control} coupled to this command is of a certain value. This 
     * is useful when we want to check if a button on a {@link Gamepad} has is being held down. 
     * <br><br>
     * Generally speaking gamepad buttons include;
     * <table>
     * <tr><td>{@link Control#CROSS CROSS},</td> <td>{@link Control#CIRCLE CIRCLE},</td> <td>{@link Control#SQUARE SQUARE},</td> 
     * <td>{@link Control#TRIANGLE TRIANGLE},</td></tr><tr><td>{@link Control#DPAD_UP DPAD_UP},</td> 
     * <td>{@link Control#DPAD_DOWN DPAD_DOWN},</td><td>{@link Control#DPAD_LEFT DPAD_LEFT},</td> 
     * <td>{@link Control#DPAD_RIGHT DPAD_RIGHT},</td></tr><tr><td>{@link Control#L1 L1},</td> <td>{@link Control#R1 R1},</td> 
     * <td>{@link Control#L3 L3},</td> <td>{@link Control#R3 R3}.</td></tr>
     * </table>
     * <br>
     * If you wish to check whether a button has been pressed once use 
     * {@link buttonPressedOnce()}.
     * 
     * @return true if the input value of the interactive component equals 1
     */
    protected boolean buttonPressed() {
        return inputValue == GLFW_PRESS;
    }
    
    /**
     * Convenience method used to check if the input state of the {@link Control} coupled to this command is of a certain value. This is 
     * useful for when we want a command to only execute once per button press.
     * <br><br>
     * Gamepad buttons typically include; 
     * <table>
     * <tr><td>{@link Control#CROSS CROSS},</td> <td>{@link Control#CIRCLE CIRCLE},</td> <td>{@link Control#SQUARE SQUARE},</td> 
     * <td>{@link Control#TRIANGLE TRIANGLE},</td></tr><tr><td>{@link Control#DPAD_UP DPAD_UP},</td> 
     * <td>{@link Control#DPAD_DOWN DPAD_DOWN},</td><td>{@link Control#DPAD_LEFT DPAD_LEFT},</td> 
     * <td>{@link Control#DPAD_RIGHT DPAD_RIGHT},</td></tr><tr><td>{@link Control#L1 L1},</td> <td>{@link Control#R1 R1},</td> 
     * <td>{@link Control#L3 L3},</td> <td>{@link Control#R3 R3}.</td></tr>
     * </table>
     * 
     * @return true if the input value of the interactive component equals 1 but only upon its initial press
     */
    protected boolean buttonPressedOnce() {
        if(buttonPressed() && !pressRequested) {
            pressRequested = true;
            return true;
        } else if(!buttonPressed()) {
            pressRequested = false;
        }
        
        return false;
    }
    
    /**
     * Convenience method used to check if the input state of the {@link Control} coupled to this command is of a certain value. This is 
     * especially useful when we want to determine whether an analog stick on the {@link Gamepad} has been moved.
     * <br><br>
     * It's important to remember that analog sticks are notoriously unreliable in the sense that their resting values rarely reach true 
     * zero. To mitigate this effect input devices provide what's known as a "deadzone" value to indicate how much an analog stick is 
     * allowed to move before it's recognized as a conscious decision on the players part. Therefore, the result of this check may not 
     * always yield true even if an analog stick is ostensibly moved.
     * <br><br>
     * Analog sticks are grouped by their axes;
     * <table>
     * <tr><td>{@link Control#LEFT_STICK_X LEFT_STICK_X},</td> <td>{@link Control#LEFT_STICK_Y LEFT_STICK_Y},</td></tr>
     * <tr><td>{@link Control#RIGHT_STICK_X RIGHT_STICK_X},</td><td>{@link Control#RIGHT_STICK_Y RIGHT_STICK_Y},</td></tr>
     * </table>
     * 
     * @return true if the input value of the interactive component exhibits a greater absolute value than the deadzone value
     */
    protected boolean axisMoved() {
        boolean isNotTrigger = (control == LEFT_STICK_X) || 
                               (control == LEFT_STICK_Y) ||
                               (control == RIGHT_STICK_X) ||
                               (control == RIGHT_STICK_Y);
        
        return Math.abs(inputValue) > (device.deadzone) && isNotTrigger;
    }
    
    /**
     * Convenience method used to check if the input state of the {@link Control} coupled to this command is of a certain value. This is 
     * useful anytime we want to detect a change in the value of a responsive trigger.
     * <br><br>
     * Triggers on {@link Gamepad} style controllers may exhibit fluid input values much in the same way analog sticks do. Their function is 
     * significantly more predictable however so no deadzone value is used when determining its pulled state.
     * <br><br>
     * The interactive components used to represent triggers are {@link Control#L2 L2} and {@link Control#R2 R2}.
     * 
     * @return true if the input value of the interactive component is greater than -1 (or 0 in the instance of {@link KeyMouseCombo})
     */
    protected boolean triggerPulled() {
        boolean isNotAxis = (control == L2) || (control == R2);
        return ((device instanceof KeyMouseCombo) ? inputValue > 0 : inputValue > -1) && isNotAxis;
    }
    
}