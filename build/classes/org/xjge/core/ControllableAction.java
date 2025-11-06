package org.xjge.core;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public abstract class ControllableAction {
    
    private final ControlState controlState = new ControlState();
    
    void perform(InputDevice device, Control control, double targetDelta, double trueDelta) {
        controlState.update(device, control);
        perform(controlState, targetDelta, trueDelta);
    }
    
    public abstract void perform(ControlState controlState, double targetDelta, double trueDelta);
    
}