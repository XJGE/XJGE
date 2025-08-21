package org.xjge.test;

import org.xjge.core.Command;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class InputState extends Command {

    boolean buttonPressed;
    boolean buttonPressedOnce;
    
    float inputValue;
    
    @Override
    public void execute(double targetDelta, double trueDelta) {
        buttonPressed     = buttonPressed();
        buttonPressedOnce = buttonPressedOnce();
        inputValue        = getInputValue();
        //TODO: extend this to capture more state depending on what's needed
    }
    
}