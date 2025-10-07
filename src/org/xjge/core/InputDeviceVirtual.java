package org.xjge.core;

/**
 * 
 * @author J Hoffman
 * @since 
 */
final class InputDeviceVirtual extends InputDevice2 {

    InputDeviceVirtual(int id) {
        super(id, "Virtual Device");
    }

    @Override
    protected void captureControlState() {}

}