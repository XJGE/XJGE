package org.xjge.test;

import org.xjge.core.Monitor;
import org.xjge.core.Window2;
import org.xjge.core.WindowConfig;
import org.xjge.core.XJGE;

/**
 * Created: Jan 31, 2025
 * 
 * @author J Hoffman
 * @since  
 */
public class Main {

    public static void main(String args[]) {
        
        Monitor primary = XJGE.findMonitors().get(1);
        
        Window2.show(new WindowConfig(false, false, 640, 480, "Test", primary, null));
        
        /*
        XJGE.init("/org/xjge/assets/", "org.xjge.test.", null);
        Game.setScene(new TestScene());
        XJGE.start();
        */
        
    }
    
}