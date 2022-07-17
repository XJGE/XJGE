package org.xjge.main;

import org.joml.Vector2i;
import org.xjge.core.Game;
import org.xjge.core.Window;
import org.xjge.core.XJGE;

/**
 *
 * @author thesk
 */
public class Main {

    public static void main(String args[]) {
        
        XJGE.init("/org/xjge/assets/", "org.xjge.main.", new Vector2i(384, 216));
        
        Game.setScene(new TestScene());
        
        XJGE.start();
        
    }
    
}
