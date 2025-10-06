package org.xjge.core;

/**
 * 
 * @author J Hoffman
 * @since 
 */
final class InputDeviceMouse extends InputDevice2 {

    InputDeviceMouse(int id) {
        super(id, "Generic Mouse");
    }

    @Override
    protected void captureControlState() {
    }

}