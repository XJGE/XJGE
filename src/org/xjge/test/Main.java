package org.xjge.test;

import org.xjge.core.Game;
import org.xjge.core.XJGE;
import org.xjge.scenes.TestScene;

/**
 * Oct 31, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class Main {

    public static void main(String args[]) {
        
        XJGE.init("/org/xjge/assets/", "org.xjge.scenes.", true, null, true, false);
        
        //TODO: fix font memory leak
        
        Game.setScene(new TestScene());
        
        XJGE.start();
        
    }
    
}