package org.xjge.core;

/**
 * 
 * @author J Hoffman
 * @since 
 */
final class InputDeviceKeyboard extends InputDevice2 {

    InputDeviceKeyboard() {
        super(Input2.KEYBOARD);
    }

    @Override
    protected void captureControlState() {
    }

}