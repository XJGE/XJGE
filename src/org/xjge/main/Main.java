package org.xjge.main;

//Created: Jan 25, 2023

import org.joml.Vector2i;
import org.xjge.core.Game;
import org.xjge.core.XJGE;


/**
 * @author J Hoffman
 * @since  
 */
public class Main {

    public static void main(String args[]) {
        
        XJGE.init("/org/xjge/assets/", "org.xjge.test.", new Vector2i(16, 9));
        Game.setScene(new TestScene());
        XJGE.start();
        
    }
    
}
