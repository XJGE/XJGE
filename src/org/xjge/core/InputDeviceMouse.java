package org.xjge.core;

/**
 * 
 * @author J Hoffman
 * @since 
 */
final class InputDeviceMouse extends InputDevice2 {

    InputDeviceMouse() {
        super(Input2.MOUSE);
    }

    @Override
    protected void captureControlState() {
    }

}