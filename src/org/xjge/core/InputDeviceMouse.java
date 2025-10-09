package org.xjge.core;

import static org.lwjgl.glfw.GLFW.*;
import static org.xjge.core.Control.*;

/**
 * 
 * @author J Hoffman
 * @since 
 */
final class InputDeviceMouse extends InputDevice2 {

    private final Mouse mouse;
    
    InputDeviceMouse(Mouse mouse) {
        super(Input2.MOUSE);
        this.mouse = mouse;
        name       = "Generic Mouse";
        connected  = true;
        
        settings.put("leftDeadzone", 0.15f);
        settings.put("rightDeadzone", 0.15f);
        
        for(Control control : Control.values()) {
            switch(control) {
                default            -> controlBindings.put(control, CONTROL_UNSUPPORTED);
                case CROSS         -> controlBindings.put(control, GLFW_MOUSE_BUTTON_6);
                case CIRCLE        -> controlBindings.put(control, GLFW_MOUSE_BUTTON_7);
                case SQUARE        -> controlBindings.put(control, GLFW_MOUSE_BUTTON_8);
                case TRIANGLE      -> controlBindings.put(control, GLFW_MOUSE_BUTTON_MIDDLE);
                case L1            -> controlBindings.put(control, GLFW_MOUSE_BUTTON_4);
                case R1            -> controlBindings.put(control, GLFW_MOUSE_BUTTON_5);
                case LEFT_STICK_X  -> controlBindings.put(control, 0);
                case LEFT_STICK_Y  -> controlBindings.put(control, 0);
                case RIGHT_STICK_X -> controlBindings.put(control, 0);
                case RIGHT_STICK_Y -> controlBindings.put(control, 0);
                case L2            -> controlBindings.put(control, GLFW_MOUSE_BUTTON_LEFT);
                case R2            -> controlBindings.put(control, GLFW_MOUSE_BUTTON_RIGHT);
            }
        }
    }

    @Override
    protected void captureControlState() {
        if(Window.getMinimized()) return;
        
        for(var entry : controlBindings.entrySet()) {
            Control control  = entry.getKey();
            int binding      = entry.getValue();
            float value      = 0f;
            
            if(binding == CONTROL_UNSUPPORTED) continue;
            
            switch(control) {
                case CROSS, CIRCLE, SQUARE, TRIANGLE -> {
                    value = mouse.getButtonID();
                    controlValues.put(control, value);
                }
                case L2 -> value = mouse.leftHeld ? 1f : 0f;
                case R2 -> value = mouse.rightHeld ? 1f : 0f;
            }
        }
    }

}