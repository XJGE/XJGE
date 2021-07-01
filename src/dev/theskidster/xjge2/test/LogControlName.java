package dev.theskidster.xjge2.test;

import dev.theskidster.xjge2.core.Command;
import dev.theskidster.xjge2.core.Logger;

/**
 * @author J Hoffman
 * Created: May 3, 2021
 */

public class LogControlName extends Command {

    private String action;
    
    public LogControlName(String action) {
        this.action = action;
    }
    
    @Override
    public void execute() {
        if(buttonPressedOnce()) {
            Logger.logInfo(action + " pressed.");
        } 
        
        if(axisMoved()) {
            Logger.logInfo(action + " moved. " + getInputValue());
        }
        
        if(triggerPulled()) {
            Logger.logInfo(action + " pulled." + getInputValue());
        }
    }

}