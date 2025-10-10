package org.xjge.core;

import static org.lwjgl.glfw.GLFW.*;
import static org.xjge.core.Control.*;

/**
 * 
 * @author J Hoffman
 * @since  4.0.0
 */
final class InputDeviceMouse extends InputDevice {

    private static double scrollSpeedX;
    private static double scrollSpeedY;
    
    private final Mouse mouse;
    
    InputDeviceMouse(Mouse mouse) {
        super(Input.MOUSE);
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
                case LEFT_STICK_X  -> controlBindings.put(control, CONTROL_VIRTUAL);
                case LEFT_STICK_Y  -> controlBindings.put(control, CONTROL_VIRTUAL);
                case RIGHT_STICK_X -> controlBindings.put(control, CONTROL_VIRTUAL);
                case RIGHT_STICK_Y -> controlBindings.put(control, CONTROL_VIRTUAL);
                case L2            -> controlBindings.put(control, GLFW_MOUSE_BUTTON_LEFT);
                case R2            -> controlBindings.put(control, GLFW_MOUSE_BUTTON_RIGHT);
            }
        }
    }
    
    static void setScrollSpeedValues(double scrollSpeedX, double scrollSpeedY) {
        InputDeviceMouse.scrollSpeedX = scrollSpeedX;
        InputDeviceMouse.scrollSpeedY = scrollSpeedY;
    }

    @Override
    protected void captureControlState() {
        if(Window.getMinimized()) return;
        
        for(var entry : controlBindings.entrySet()) {
            Control control = entry.getKey();
            float value     = 0f;
            
            switch(control) {
                case CROSS, CIRCLE, SQUARE, TRIANGLE, L1, R1, L2, R2 -> {
                    value = Window.getMouseButtonInputValue(entry.getValue());
                }
                case LEFT_STICK_X -> {
                    value = (float) scrollSpeedX;
                    scrollSpeedX = 0;
                }
                case LEFT_STICK_Y -> {
                    value = (float) scrollSpeedY;
                    scrollSpeedY = 0;
                }
                case RIGHT_STICK_X -> value = (float) mouse.getCursorPositionX();
                case RIGHT_STICK_Y -> value = (float) mouse.getCursorPositionY();
            }
            
            controlValues.put(control, value);
        }
    }

}