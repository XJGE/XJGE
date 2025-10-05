package org.xjge.core;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.xjge.core.Control.LEFT_STICK_X;
import static org.xjge.core.Control.LEFT_STICK_Y;
import static org.xjge.core.Control.RIGHT_STICK_X;
import static org.xjge.core.Control.RIGHT_STICK_Y;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public final class ControlState {

    private boolean pressRequested;
    
    private float inputValue;
    
    private int deviceID;
    
    private InputDevice2 device;
    private Control control;
    
    void update(InputDevice2 device, Control control) {
        this.device  = device;
        this.control = control;
        inputValue   = device.inputValues.get(control);
        deviceID     = device.id;
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
        
        return Math.abs(inputValue) > (deadzone) && isNotTrigger;
    }
    
    public boolean buttonPressed() {
        return inputValue == GLFW_PRESS;
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
        return false; //TODO: reimplement this
    }
    
    public float getInputValue() {
        return inputValue;
    }
    
    public int getDeviceID() {
        return deviceID;
    }
    
}