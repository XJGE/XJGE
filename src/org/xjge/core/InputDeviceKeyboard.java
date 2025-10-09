package org.xjge.core;

import static org.lwjgl.glfw.GLFW.*;
import static org.xjge.core.Control.*;

/**
 * 
 * @author J Hoffman
 * @since 
 */
final class InputDeviceKeyboard extends InputDevice2 {

    InputDeviceKeyboard() {
        super(Input2.KEYBOARD);
        
        settings.put("leftDeadzone", 0.15f);
        settings.put("rightDeadzone", 0.15f);
        
        controlBindings.put(CROSS,         GLFW_KEY_SPACE);
        controlBindings.put(CIRCLE,        GLFW_KEY_Q);
        controlBindings.put(SQUARE,        GLFW_KEY_R);
        controlBindings.put(TRIANGLE,      GLFW_KEY_E);
        controlBindings.put(L1,            GLFW_KEY_1);
        controlBindings.put(R1,            GLFW_KEY_2);
        controlBindings.put(SHARE,         GLFW_KEY_TAB);
        controlBindings.put(OPTIONS,       GLFW_KEY_ESCAPE);
        controlBindings.put(PS_BUTTON,     GLFW_KEY_ENTER);
        controlBindings.put(L3,            GLFW_KEY_LEFT_SHIFT);
        controlBindings.put(R3,            GLFW_KEY_RIGHT_SHIFT);
        controlBindings.put(DPAD_UP,       GLFW_KEY_UP);
        controlBindings.put(DPAD_RIGHT,    GLFW_KEY_RIGHT);
        controlBindings.put(DPAD_DOWN,     GLFW_KEY_DOWN);
        controlBindings.put(DPAD_LEFT,     GLFW_KEY_LEFT);
        controlBindings.put(LEFT_STICK_X,  GLFW_KEY_A | GLFW_KEY_D);
        controlBindings.put(LEFT_STICK_Y,  GLFW_KEY_W | GLFW_KEY_S);
        controlBindings.put(RIGHT_STICK_X, 0);
        controlBindings.put(RIGHT_STICK_Y, 0);
        controlBindings.put(L2,            0);
        controlBindings.put(R2,            0);
    }

    @Override
    protected void captureControlState() {
    }

}