package org.xjge.test;

import org.xjge.core.Game;
import org.xjge.core.XJGE;

/**
 * Created: Jan 31, 2025
 * 
 * @author J Hoffman
 * @since  
 */
public class Main {

    public static void main(String args[]) {
        
        XJGE.init("/org/xjge/assets/", "org.xjge.test.", null);
        
        Game.setScene(new TestScene());
        
        XJGE.start();
        
    }
    
}