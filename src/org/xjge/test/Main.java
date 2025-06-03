package org.xjge.test;

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
                
        XJGE.init(true, "/org/xjge/assets/", "org.xjge.test.");
        
        /*
        WindowConfig windowConfig = new WindowConfig();
        windowConfig.setSize(640, 480);
        windowConfig.setResizable(true);
        windowConfig.setTitle("test");
        */
        
        Window2.show(new WindowConfig());
        
    }
    
}