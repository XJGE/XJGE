package org.xjge.core;

import static org.xjge.core.Control.*;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

//Created: May 3, 2021

/**
 * Abstraction of the Gang of Four pattern by the same name- command objects 
 * serve as a point at which a {@link Control} from an {@link InputDevice} can 
 * be coupled to various player actions and passed to a {@link Puppet} object. 
 * <p>
 * More generally, whenever you want to create a new action the player can 
 * perform- such as walking, jumping, or shooting, you'll define that 
 * behavior in an object that extends this class. Objects of this type are 
 * automatically supplied with information about the current state of the 
 * players input device. This information in conjunction with several 
 * convenience methods provided by the parent class can be used to create 
 * player actions that exhibit a high level of fidelity.
 * <p>
 * The command superclass lacks constructors by design to encourage subclasses 
 * to define their own. This open-ended approach allows subclasses to capture 
 * whatever supplemental information they require to sufficiently perform the 
 * player actions they've been assigned to.
 * 
 * @author J Hoffman
 * @since  2.0.0
 * 
 * @see axisMoved()
 * @see buttonPressed()
 * @see buttonPressedOnce()
 * @see getInputValue()
 * @see triggerPulled()
 */
public abstract class Command {

    private int deviceID;
    private int glfwButtonID;
    
    private float inputValue;
    
    private boolean pressRequested = true;
    
    private InputDevice device;
    private Control control;
    
    /**
     * Captures the state of the input device executing this command and 
     * provides it to the subclass.
     * 
     * @param inputValue   the floating point value of a single interactive 
     *                     component
     * @param device       the input device that is executing this command
     * @param control      the current interactive component being acted upon
     * @param glfwButtonID 
     */
    void execute(float inputValue, InputDevice device, Control control, int glfwButtonID) {
        this.inputValue   = inputValue;
        this.device       = device;
        this.control      = control;
        this.deviceID     = device.id;
        this.glfwButtonID = glfwButtonID;
        
        execute();
    }
    
    /**
     * Organizes the input logic used to control player actions. This method is 
     * called automatically once every game tick.
     */
    public abstract void execute();
    
    /**
     * Provides subclasses with a value describing the current state of the 
     * {@link Control} coupled to this command. The value provided may be 
     * between -1 to 1 or 0 to 1 depending on the input device used and the 
     * interactive component being pressed.
     * 
     * @return a value denoting the current state of a single interactive 
     *         component on the input device
     */
    protected float getInputValue() {
        return inputValue;
    }
    
    /**
     * Obtains a value used by GLFW to identify an interactive component. More
     * specifically, this method can be used to return which GLFW button has 
     * been assigned to the current {@link Control} object being used to execute 
     * this command. This is particularly useful in instances where we want to 
     * give users the ability to manually reconfigure the layout of the 
     * interactive components on their input devices.
     * <p>
     * NOTE: This should <i>not</i> be used to supplement the functionality of 
     * the control object- which exists to decouple device-specific button 
     * layouts from game-defined ones.
     * 
     * @return the GLFW value used to identify the interactive component 
     *         executing this command
     */
    protected int getButtonID() {
        return glfwButtonID;
    }
    
    /**
     * Provides subclasses with a value that denotes some preference of the 
     * input device currently being used to execute this command.
     * <p>
     * NOTE: By default the engine provides deadzone settings for both the left
     * and right analog sticks of each input device. These settings determine 
     * how much a stick will need to be moved before its input is recognized. 
     * The values of these settings can be queried with "leftDeadzone" and 
     * "rightDeadzone" respectively.
     * <p>
     * Additional settings can be defined with 
     * {@link Input#setDeviceSetting(int, String, float)}.
     * 
     * @param name the name of the setting to parse a value from
     * 
     * @return the value of the setting or {@code NaN} if the setting of the 
     *         name specified could not be found
     */
    protected float getDeviceSetting(String name) {
        if(device.settings.containsKey(name)) {
            return device.settings.get(name);
        } else {
            return Float.NaN;
        }
    }
    
    /**
     * Obtains the ID number of the device currently executing this command.
     * 
     * @return a value indicating which input device is being used
     */
    protected int getDeviceID() {
        return deviceID;
    }
    
    /**
     * Convenience method used to check if the input state of the 
     * {@link Control} coupled to this command is of a certain value. This is 
     * useful when we want to check if a button on a {@link Gamepad} has is 
     * being held down. 
     * <p>
     * Generally speaking gamepad buttons include:
     * <table><caption></caption>
     * <tr>
     * <td>{@link Control#CROSS CROSS}</td>
     * <td>{@link Control#CIRCLE CIRCLE}</td>
     * <td>{@link Control#SQUARE SQUARE}</td> 
     * <td>{@link Control#TRIANGLE TRIANGLE}</td>
     * </tr><tr>
     * <td>{@link Control#DPAD_UP DPAD_UP}</td>
     * <td>{@link Control#DPAD_DOWN DPAD_DOWN}</td>
     * <td>{@link Control#DPAD_LEFT DPAD_LEFT}</td> 
     * <td>{@link Control#DPAD_RIGHT DPAD_RIGHT}</td>
     * </tr><tr>
     * <td>{@link Control#L1 L1}</td><td>{@link Control#R1 R1}</td> 
     * <td>{@link Control#L3 L3}</td><td>{@link Control#R3 R3}</td>
     * </tr>
     * </table>
     * <p>
     * If you wish to check whether a button has been pressed once use 
     * {@link buttonPressedOnce()}.
     * 
     * @return true if the input value of the interactive component equals 1
     */
    protected boolean buttonPressed() {
        return inputValue == GLFW_PRESS;
    }
    
    /**
     * Convenience method used to check if the input state of the 
     * {@link Control} coupled to this command is of a certain value. This is 
     * useful for when we want a command to only execute once per button press.
     * <p>
     * Generally speaking gamepad buttons include:
     * <table><caption></caption>
     * <tr>
     * <td>{@link Control#CROSS CROSS}</td>
     * <td>{@link Control#CIRCLE CIRCLE}</td>
     * <td>{@link Control#SQUARE SQUARE}</td> 
     * <td>{@link Control#TRIANGLE TRIANGLE}</td>
     * </tr><tr>
     * <td>{@link Control#DPAD_UP DPAD_UP}</td>
     * <td>{@link Control#DPAD_DOWN DPAD_DOWN}</td>
     * <td>{@link Control#DPAD_LEFT DPAD_LEFT}</td> 
     * <td>{@link Control#DPAD_RIGHT DPAD_RIGHT}</td>
     * </tr><tr>
     * <td>{@link Control#L1 L1}</td><td>{@link Control#R1 R1}</td> 
     * <td>{@link Control#L3 L3}</td><td>{@link Control#R3 R3}</td>
     * </tr>
     * </table>
     * 
     * @return true if the input value of the interactive component equals 1 
     *         but only upon its initial press
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
     * Convenience method used to check if the input state of the 
     * {@link Control} coupled to this command is of a certain value. This is 
     * especially useful when we want to determine whether an analog stick on 
     * the {@link Gamepad} has been moved.
     * <p>
     * NOTE: It's important to remember that analog sticks are notoriously 
     * unreliable in the sense that their resting values rarely reach true 
     * zero. To mitigate this effect input devices provide what's known as a 
     * "deadzone" value to indicate how much an analog stick is allowed to move 
     * before it's recognized as a conscious decision on the players part. 
     * Therefore, the result of this check may not always yield true even if an 
     * analog stick is ostensibly moved.
     * <p>
     * Analog sticks are grouped by their axes;
     * <table><caption></caption>
     * <tr>
     * <td>{@link Control#LEFT_STICK_X LEFT_STICK_X}</td>
     * <td>{@link Control#LEFT_STICK_Y LEFT_STICK_Y}</td>
     * </tr><tr>
     * <td>{@link Control#RIGHT_STICK_X RIGHT_STICK_X}</td>
     * <td>{@link Control#RIGHT_STICK_Y RIGHT_STICK_Y}</td>
     * </tr>
     * </table>
     * 
     * @return true if the input value of the interactive component exhibits a 
     *         greater absolute value than the deadzone value
     */
    protected boolean axisMoved() {        
        float deadzone       = 0;
        boolean isNotTrigger = false;
        
        if(control == LEFT_STICK_X || control == LEFT_STICK_Y) {
            deadzone     = device.settings.get("leftDeadzone");
            isNotTrigger = true;
        } else if(control == RIGHT_STICK_X || control == RIGHT_STICK_Y) {
            deadzone     = device.settings.get("rightDeadzone");
            isNotTrigger = true;
        }
        
        return Math.abs(inputValue) > (deadzone) && isNotTrigger;
    }
    
    /**
     * Convenience method used to check if the input state of the 
     * {@link Control} coupled to this command is of a certain value. This is 
     * useful anytime we want to detect a change in the value of a responsive 
     * trigger.
     * <p>
     * Triggers on {@link Gamepad} style controllers may exhibit fluid input 
     * values much in the same way analog sticks do. Their function is 
     * significantly more predictable however so no deadzone value is used when 
     * determining its pulled state.
     * <p>
     * The interactive components used to represent triggers are 
     * {@link Control#L2 L2} and {@link Control#R2 R2}.
     * 
     * @return true if the input value of the interactive component is greater 
     *         than -1 (or 0 in the instance of {@link KeyMouseCombo})
     */
    protected boolean triggerPulled() {
        boolean isNotAxis = (control == L2) || (control == R2);
        return ((device instanceof KeyMouseCombo) ? inputValue > 0 : inputValue > -1) && isNotAxis;
    }
    
}