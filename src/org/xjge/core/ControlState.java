package org.xjge.core;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.xjge.core.Control.LEFT_STICK_X;
import static org.xjge.core.Control.LEFT_STICK_Y;
import static org.xjge.core.Control.RIGHT_STICK_X;
import static org.xjge.core.Control.RIGHT_STICK_Y;

/**
 * 
 * @author J Hoffman
 * @since  4.0.0
 */
public final class ControlState {

    private boolean pressRequested;
    
    private float currentInputValue;
    private float previousInputValue;
    
    private int deviceID;
    
    private InputDevice2 device;
    private Control control;
    
    void update(InputDevice2 device, Control control) {
        this.device  = device;
        this.control = control;
        previousInputValue = currentInputValue;
        currentInputValue  = device.controlValues.get(control);
        deviceID = device.id;
    }
    
    public boolean axisMoved() {
        float deadzone       = 0;
        boolean isNotTrigger = false;
        
        if(control == LEFT_STICK_X || control == LEFT_STICK_Y) {
            deadzone     = device.settings.get("leftDeadzone");
            isNotTrigger = true;
        } else if(control == RIGHT_STICK_X || control == RIGHT_STICK_Y) {
            deadzone     = device.settings.get("rightDeadzone");
            isNotTrigger = true;
        }
        
        if(!isNotTrigger) return false;
        
        float inputValue = 0;
        
        if(device instanceof InputDeviceMouse) {
            inputValue = currentInputValue - previousInputValue;
        } else {
            inputValue = currentInputValue;
        }
        
        return Math.abs(inputValue) > (deadzone);
    }
    
    public boolean buttonPressed() {
        return currentInputValue == GLFW_PRESS;
    }
    
    public boolean buttonPressedOnce() {
        if(buttonPressed() && !pressRequested) {
            pressRequested = true;
            return true;
        } else if(!buttonPressed()) {
            pressRequested = false;
        }
        
        return false;
    }
    
    public boolean triggerPulled() {
        boolean isNotAxis = (control == Control.L2) || (control == Control.R2);
        return ((deviceID == Input2.KEYBOARD || deviceID == Input2.MOUSE) ? currentInputValue > 0 : currentInputValue > -1) && isNotAxis;
    }
    
    public float getInputValue() {
        return currentInputValue;
    }
    
    public int getDeviceID() {
        return deviceID;
    }
    
}