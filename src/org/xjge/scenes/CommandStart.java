package org.xjge.scenes;

import org.xjge.core.Command;
import org.xjge.core.Timer;

/**
 * Feb 6, 2022
 */

/**
 * @author J Hoffman
 * @since  
 */
public class CommandStart extends Command {

    private Timer timer;
    
    boolean start;
    
    CommandStart(Timer timer) {
        this.timer = timer;
    }
    
    @Override
    public void execute() {
        if(buttonPressedOnce()) {
            start = !start;
            
            if(start) {
                timer.start();
                System.out.println("timer started");
            } else {
                timer.restart();
                System.out.println("timer reset");
            }
        }
    }

}
