package org.xjge.test;

import org.xjge.core.Game;
import org.xjge.core.Input;
import org.xjge.core.XJGE;

/**
 * Sep 7, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class Main {

    public static void main(String args[]) {
        
        XJGE.init("/org/xjge/assets/", "org.xjge.test.", true, null, false, false);
        
        Game.setScene(new TestScene("test"));
        
        //Input.setDeviceSetting(-1, "sens", Input.getDeviceSetting(-1, "sens"));
        //Input.setDeviceSetting(0, "sens", 2);
        //Input.setDeviceExtraValue(-1, "other", 2);
        
        //Input.setDeviceSetting(-1, "sens", 0.24f);
        
        XJGE.start();
    }
    
}
