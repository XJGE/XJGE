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
        
        /*
        windowConfig.setFullscreen(false);
        windowConfig.setResizable(false);
        windowConfig.setSize(1280, 780);
        windowConfig.setMonitor(XJGE.getPrimaryMonitor());
        windowConfig.setResolution(384, 216);
        
        Window2.show(windowConfig);
        */
        
        
        XJGE.init();
        
        WindowConfig windowConfig = new WindowConfig();
        
        Window2.show(windowConfig);
        
        /*
        XJGE.init("/org/xjge/assets/", "org.xjge.test.", null);
        Game.setScene(new TestScene());
        XJGE.start();
        */
        
    }
    
}