package org.xjge.scenes;

import org.xjge.core.Game;
import org.xjge.core.XJGE;

/**
 * Feb 7, 2022
 */

/**
 * @author J Hoffman
 * @since  
 */
public class Main {

    public static void main(String args[]) {
        
        XJGE.init("/org/xjge/assets/", "org.xjge.scenes.", null);
        
        Game.setScene(new TestScene());
        
        XJGE.start();
        
    }
    
}
