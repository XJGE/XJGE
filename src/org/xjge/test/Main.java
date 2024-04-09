package org.xjge.test;

//Created: Apr 8, 2024

import org.xjge.core.Game;
import org.xjge.core.XJGE;


/**
 * @author J Hoffman
 * @since  
 */
public class Main {

    public static void main(String args[]) {
        
        XJGE.init("/org/xjge/assets/", "org.xjge.main.", null);
        
        Game.setScene(new TestScene());
        
        XJGE.start();
        
    }
    
}