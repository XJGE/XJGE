package org.xjge.core;

import static org.lwjgl.glfw.GLFW.*;
import static org.xjge.core.Control.*;

/**
 * 
 * @author J Hoffman
 * @since 
 */
final class InputDeviceKeyboard extends InputDevice2 {

    int[] leftAxisValues  = new int[4];
    int[] rightAxisValues = new int[4];
    
    InputDeviceKeyboard() {
        super(Input2.KEYBOARD);
        name      = "Generic Keyboard";
        connected = true;
        
        settings.put("leftDeadzone", 0.15f);
        settings.put("rightDeadzone", 0.15f);
        
        leftAxisValues[0] = GLFW_KEY_W;
        leftAxisValues[1] = GLFW_KEY_A;
        leftAxisValues[2] = GLFW_KEY_S;
        leftAxisValues[3] = GLFW_KEY_D;
        
        rightAxisValues[0] = GLFW_KEY_I;
        rightAxisValues[1] = GLFW_KEY_J;
        rightAxisValues[2] = GLFW_KEY_K;
        rightAxisValues[3] = GLFW_KEY_L;
        
        controlBindings.put(CROSS,         GLFW_KEY_SPACE);
        controlBindings.put(CIRCLE,        GLFW_KEY_LEFT_SHIFT);
        controlBindings.put(SQUARE,        GLFW_KEY_R);
        controlBindings.put(TRIANGLE,      GLFW_KEY_F);
        controlBindings.put(L1,            GLFW_KEY_Q);
        controlBindings.put(R1,            GLFW_KEY_E);
        controlBindings.put(SHARE,         GLFW_KEY_TAB);
        controlBindings.put(OPTIONS,       GLFW_KEY_ESCAPE);
        controlBindings.put(PS_BUTTON,     GLFW_KEY_ENTER);
        controlBindings.put(L3,            GLFW_KEY_C);
        controlBindings.put(R3,            GLFW_KEY_X);
        controlBindings.put(DPAD_UP,       GLFW_KEY_UP);
        controlBindings.put(DPAD_RIGHT,    GLFW_KEY_RIGHT);
        controlBindings.put(DPAD_DOWN,     GLFW_KEY_DOWN);
        controlBindings.put(DPAD_LEFT,     GLFW_KEY_LEFT);
        controlBindings.put(LEFT_STICK_X,  leftAxisValues[1] | leftAxisValues[3]);
        controlBindings.put(LEFT_STICK_Y,  leftAxisValues[0] | leftAxisValues[2]);
        controlBindings.put(RIGHT_STICK_X, rightAxisValues[1] | rightAxisValues[3]);
        controlBindings.put(RIGHT_STICK_Y, rightAxisValues[0] | rightAxisValues[2]);
        controlBindings.put(L2,            GLFW_KEY_U);
        controlBindings.put(R2,            GLFW_KEY_O);
    }

    private float findAxisValue(int negativeKey, int positiveKey) {
        boolean negativeKeyPressed = Window.getKeyInputValue(negativeKey) == GLFW_PRESS;
        boolean positiveKeyPressed = Window.getKeyInputValue(positiveKey) == GLFW_PRESS;
        return (negativeKeyPressed ^ positiveKeyPressed) ? (negativeKeyPressed ? -1f : 1f) : 0f;
    }
    
    @Override
    protected void captureControlState() {
        if(Window.getMinimized()) return;
        
        for(var entry : controlBindings.entrySet()) {
            Control control = entry.getKey();
            
            float value = switch(control) {
                case LEFT_STICK_X  -> findAxisValue(leftAxisValues[1], leftAxisValues[3]);
                case LEFT_STICK_Y  -> findAxisValue(leftAxisValues[0], leftAxisValues[2]);
                case RIGHT_STICK_X -> findAxisValue(rightAxisValues[1], rightAxisValues[3]);
                case RIGHT_STICK_Y -> findAxisValue(rightAxisValues[0], rightAxisValues[2]);
                default -> Window.getKeyInputValue(entry.getValue());
            };
            
            controlValues.put(control, value);
        }
    }

}