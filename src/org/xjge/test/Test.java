package org.xjge.test;

import org.xjge.core.Game;
import org.xjge.core.XJGE;
import org.joml.Vector2i;

/**
 * @author J Hoffman
 * Created: Apr 28, 2021
 */

public class Test {

    /*
    this class and package are temporary and provided for testing purposes only!
    they should not be included in releases!
    */
    
    public static void main(String args[]) {
        
        Vector2i res = new Vector2i(384, 216);
        
        XJGE.init("/org/xjge/assets/", "org.xjge.test.", true, null, true, false);
        
        //Input.setDeviceEnabled(0, false);
        
        //Window.setDimensions(1200, 400);
        
        Game.setScene(new TestScene());
        
        XJGE.start();
        
    }
    
}