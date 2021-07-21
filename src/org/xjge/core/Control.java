package org.xjge.core;

//Created: May 3, 2021

/**
 * Represents an interactive component on a {@link Gamepad} such as an analog 
 * stick, button, or trigger.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public enum Control {
    
    /**
     * A button typically located on the right-hand side of the gamepad.
     */
    CROSS,
    
    /**
     * A button typically located on the right-hand side of the gamepad.
     */
    CIRCLE,
    
    /**
     * A button typically located on the right-hand side of the gamepad.
     */
    SQUARE,
    
    /**
     * A button typically located on the right-hand side of the gamepad.
     */
    TRIANGLE,
    
    /**
     * A trigger-like button usually located on the upper-left side of the 
     * gamepad. Sometimes referred to as a bumper.
     */
    L1,
    
    /**
     * A trigger-like button usually located on the upper-right side of the 
     * gamepad. Sometimes referred to as a bumper.
     */
    R1,
    
    /**
     * A button generally located somewhere in the center of the gamepad.
     */
    SHARE,
    
    /**
     * A button generally located somewhere in the center of the gamepad.
     */
    OPTIONS,
    
    /**
     * A button typically located directly in the center of the gamepad. This 
     * may be the power button.
     */
    PS_BUTTON,
    
    /**
     * A button that is likely integrated directly into the gamepads left 
     * analog stick.
     */
    L3,
    
    /**
     * A button that is likely integrated directly into the gamepads right 
     * analog stick.
     */
    R3,
    
    /**
     * The up direction on the controllers d-pad.
     */
    DPAD_UP,
    
    /**
     * The right direction on the controllers d-pad.
     */
    DPAD_RIGHT,
    
    /**
     * The down direction on the controllers d-pad.
     */
    DPAD_DOWN,
    
    /**
     * The left direction on the controllers d-pad.
     */
    DPAD_LEFT,
    
    /**
     * Represents the analog stick located on the left-hand side of the 
     * gamepad. {@link Command} objects coupled to this control will only check
     * for changes in its horizontal axis.
     */
    LEFT_STICK_X,
    
    /**
     * Represents the analog stick located on the left-hand side of the 
     * gamepad. {@link Command} objects coupled to this control will only check
     * for changes in its vertical axis. 
     */
    LEFT_STICK_Y,
    
    /**
     * Represents the analog stick located on the right-hand side of the 
     * gamepad. {@link Command} objects coupled to this control will only check
     * for changes in its horizontal axis.
     */
    RIGHT_STICK_X,
    
    /**
     * Represents the analog stick located on the right-hand side of the 
     * gamepad. {@link Command} objects coupled to this control will only check
     * for changes in its vertical axis.
     */
    RIGHT_STICK_Y,
    
    /**
     * A responsive trigger generally located at the upper-left side of the 
     * gamepad directly under the button-like "bumper" trigger ({@link L1}).
     */
    L2,
    
    /**
     * A responsive trigger generally located at the upper-right side of the 
     * gamepad directly under the button-like "bumper" trigger ({@link R1}).
     */
    R2;
    
}