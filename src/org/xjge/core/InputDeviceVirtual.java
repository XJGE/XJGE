package org.xjge.core;

/**
 * 
 * @author J Hoffman
 * @since  4.0.0
 */
final class InputDeviceVirtual extends InputDevice2 {

    InputDeviceVirtual(int id) {
        super(id);
        name      = "Virtual Device";
        connected = true;
        
        settings.put("leftDeadzone", 0.15f);
        settings.put("rightDeadzone", 0.15f);
        
        for(Control control : Control.values()) {
            controlValues.put(control, 0f);
            controlBindings.put(control, CONTROL_VIRTUAL);
        }
    }

    @Override
    protected void captureControlState() {}

}