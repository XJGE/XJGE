package org.xjge.core;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public abstract class PuppetAction {
    
    private final InputState inputState = new InputState();
    
    void perform(InputDevice2 device, Control control, double targetDelta, double trueDelta) {
        inputState.update(device, control);
        perform(inputState, targetDelta, trueDelta);
    }
    
    public abstract void perform(InputState inputState, double targetDelta, double trueDelta);
    
}