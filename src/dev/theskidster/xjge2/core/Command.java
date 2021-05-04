package dev.theskidster.xjge2.core;

import static dev.theskidster.xjge2.core.Control.*;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

/**
 * @author J Hoffman
 * Created: May 3, 2021
 */

public abstract class Command {

    private float inputValue;
    
    private boolean pressRequested = true;
    
    public final String action;
    private InputDevice device;
    private Control control;
    
    protected Command() {
        action = null;
    }
    
    protected Command(String action) {
        this.action = action;
    }
    
    void execute(float inputValue, InputDevice device, Control control) {
        this.inputValue = inputValue;
        this.device     = device;
        this.control    = control;
        
        execute();
    }
    
    public abstract void execute();
    
    public abstract void undo();
    
    protected float getInputValue() {
        return inputValue;
    }
    
    protected boolean buttonPressed() {
        return inputValue == GLFW_PRESS;
    }
    
    protected boolean buttonPressedOnce() {
        if(buttonPressed() && !pressRequested) {
            pressRequested = true;
            return true;
        } else if(!buttonPressed()) {
            pressRequested = false;
        }
        
        return false;
    }
    
    protected boolean axisMoved() {
        boolean isNotTrigger = (control == LEFT_STICK_X) || 
                               (control == LEFT_STICK_Y) ||
                               (control == RIGHT_STICK_X) ||
                               (control == RIGHT_STICK_Y);
        
        return Math.abs(inputValue) > GLFW_RELEASE && isNotTrigger;
    }
    
    protected boolean triggerPulled() {
        boolean isNotAxis = (control == L2) || (control == R2);
        return ((device instanceof KeyMouseCombo) ? inputValue > 0 : inputValue > -1) && isNotAxis;
    }
    
}