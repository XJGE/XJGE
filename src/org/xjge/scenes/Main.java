package org.xjge.scenes;

import org.joml.Vector2i;
import org.xjge.core.Game;
import org.xjge.core.XJGE;

/**
 * Jan 12, 2022
 */

/**
 * @author J Hoffman
 * @since  
 */
public class Main {

    public static void main(String args[]) {
        
        XJGE.init("/org/xjge/assets/", "org.xjge.scenes.", new Vector2i(384, 216));
        
        Game.setScene(new TestScene());
        
        XJGE.start();
        
    }
    
}
