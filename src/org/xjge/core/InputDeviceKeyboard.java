package org.xjge.core;

/**
 * 
 * @author J Hoffman
 * @since 
 */
final class InputDeviceKeyboard extends InputDevice2 {

    InputDeviceKeyboard(int id) {
        super(id, "Generic Keyboard");
    }

    @Override
    protected void captureControlState() {
    }

}