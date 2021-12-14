package org.xjge.test;

import org.xjge.core.Game;
import org.xjge.core.XJGE;
import org.xjge.scenes.TestScene;

/**
 * Nov 9, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class Main {

    public static void main(String args[]) {
        
        XJGE.init("/org/xjge/assets/", "org.xjge.scenes.", null, false, true, true, false, false);
        
        Game.setScene(new TestScene());
        
        XJGE.start();
    }
    
}