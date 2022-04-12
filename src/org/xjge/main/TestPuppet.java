package org.xjge.main;

import org.xjge.core.Command;
import org.xjge.core.Control;
import org.xjge.core.Puppet;

/**
 * Apr 12, 2022
 */

/**
 * @author J Hoffman
 * @since  
 */
public class TestPuppet {

    Puppet puppet = new Puppet(this);
    
    TestPuppet() {
        puppet.commands.put(Control.CROSS, new TestCommand());
    }
    
    private class TestCommand extends Command {

        @Override
        public void execute() {
            if(buttonPressedOnce()) {
                System.out.println("button pressed once");
            }
        }
        
    }
    
}
