package dev.theskidster.xjge2.core;

import static dev.theskidster.xjge2.core.Control.*;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

/**
 * @author J Hoffman
 * Created: May 3, 2021
 */

/**
 * Abstraction of the Gang of Four pattern of the same name, command objects 
 * serve as a point at which a {@link Control} from an {@link InputDevice} can 
 * be coupled to the various actions of a {@link Puppet} object.
 * <br><br>
 * More generally, whenever you want to create a new action a player can 
 * perform- such as walking, jumping, or shooting, you'll define that 
 * functionality in an object that extends this class. Objects of this type are 
 * automatically supplied with information pertaining to the current state of 
 * the players input device. This information in conjunction with several 
 * convenience methods supplied by the parent class can be used to create 
 * player actions that exhibit a high level of fidelity.
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
    
    public final String action;
    private InputDevice device;
    private Control control;
    
    /**
     * Creates a new command object with no additional functionality.
     * <br><br>
     * It is strongly recommended that game implementations overload the default 
     * constructors of the command class to supply whatever additional state the 
     * command needs to sufficiently perform player actions.
     */
    protected Command() {
        action = null;
    }
    
    /**
     * Creates a new command object that provides the subclass with a greater 
     * degree of granularity to better discern the intent of player actions. 
     * This is particularly useful when a two commands of the same type are 
     * coupled to single interactive component (or {@link Control}).
     * <br><br>
     * Typically player 
     * actions exhibit a 1:1 relationship with commands however there are some 
     * instances where simply reusing the command can be beneficial. Take for 
     * instance a player action in a 2D side scrolling game where the left and 
     * right buttons on a controller are both used for movement but require 
     * different values to change the direction the player is facing. You might 
     * create a single "MovementCommand" subclass and supply one constructor 
     * with "left" and the other "right" then check for which action is being 
     * done in the {@link execute} method change the direction the player is 
     * facing accordingly.
     * <br><br>
     * It is strongly recommended that game implementations overload the default 
     * constructors of the command class to supply whatever additional state the 
     * command needs to sufficiently perform player actions.
     * 
     * @param action the specific player action we'd like to check for when the 
     *               command is executed
     */
    protected Command(String action) {
        this.action = action;
    }
    
    /**
     * Captures the state of the input device executing this command and 
     * provides it to the subclass.
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
     * Executes a command defined by the subclass that will control a player 
     * action.
     */
    public abstract void execute();
    
    /**
     * Provides subclasses with a value describing the current state of the 
     * {@link Control} coupled to this command. The value provided may be 
     * between -1 to 1 or 0 to 1 depending on the input device used and the 
     * interactive component being pressed.
     * 
     * @return a floating point value of a single interactive component
     */
    protected float getInputValue() {
        return inputValue;
    }
    
    /**
     * Convenience method used to check if the input state of the 
     * {@link Control} coupled to this command is of a certain value. This is 
     * useful when we want to check if a button on a {@link Gamepad} has
     * been pressed or is being held down. 
     * <br><br>
     * Generally speaking gamepad buttons include;
     * <table>
     * <tr><td>{@link Control#CROSS CROSS},</td> <td>{@link Control#CIRCLE CIRCLE},</td>
     * <td>{@link Control#SQUARE SQUARE},</td> <td>{@link Control#TRIANGLE TRIANGLE},</td></tr>
     * <tr><td>{@link Control#DPAD_UP DPAD_UP},</td> <td>{@link Control#DPAD_DOWN DPAD_DOWN},</td>
     * <td>{@link Control#DPAD_LEFT DPAD_LEFT},</td> <td>{@link Control#DPAD_RIGHT DPAD_RIGHT},</td></tr>
     * <tr><td>{@link Control#L1 L1},</td> <td>{@link Control#R1 R1},</td> 
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
     * Convenience method used to check if the input state of the 
     * {@link Control} coupled to this command is of a certain value. This is 
     * useful for when we only need a command to execute once per button press.
     * <br><br>
     * Generally speaking gamepad buttons include; 
     * <table>
     * <tr><td>{@link Control#CROSS CROSS},</td> <td>{@link Control#CIRCLE CIRCLE},</td>
     * <td>{@link Control#SQUARE SQUARE},</td> <td>{@link Control#TRIANGLE TRIANGLE},</td></tr>
     * <tr><td>{@link Control#DPAD_UP DPAD_UP},</td> <td>{@link Control#DPAD_DOWN DPAD_DOWN},</td>
     * <td>{@link Control#DPAD_LEFT DPAD_LEFT},</td> <td>{@link Control#DPAD_RIGHT DPAD_RIGHT},</td></tr>
     * <tr><td>{@link Control#L1 L1},</td> <td>{@link Control#R1 R1},</td> 
     * <td>{@link Control#L3 L3},</td> <td>{@link Control#R3 R3}.</td></tr>
     * </table>
     * 
     * @return true if the input value of the interactive component equals 1 
     *          but only upon the initial press of the interactive component
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
     * {@link Control} coupled to this command is of a certain value.
     * <br><br>
     * - what can it be used for?
     * - about dead zones
     * - mention -1 to 1 value?
     * - key mouse combo vs. gamepad
     * - which buttons?
     * 
     * @return true if the input value of the interactive component is a non-zero value 
     */
    protected boolean axisMoved() {
        boolean isNotTrigger = (control == LEFT_STICK_X) || 
                               (control == LEFT_STICK_Y) ||
                               (control == RIGHT_STICK_X) ||
                               (control == RIGHT_STICK_Y);
        
        return Math.abs(inputValue) > GLFW_RELEASE && isNotTrigger;
    }
    
    /**
     * Convenience method used to check if the input state of the 
     * {@link Control} coupled to this command is of a certain value.
     * <br><br>
     * 
     * 
     * @return true if the input value of the interactive component is a non-zero value 
     */
    protected boolean triggerPulled() {
        boolean isNotAxis = (control == L2) || (control == R2);
        return ((device instanceof KeyMouseCombo) ? inputValue > 0 : inputValue > -1) && isNotAxis;
    }
    
}