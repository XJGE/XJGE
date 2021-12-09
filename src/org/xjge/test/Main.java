package org.xjge.test;

import org.xjge.core.Game;
import org.xjge.core.XJGE;
import org.xjge.scenes.PlaneScene;

/**
 * Nov 9, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class Main {

    public static void main(String args[]) {
        
        /*
        TODO:
        - add bloom effect from lighting testbed.
        
        - provide option to apply gaussian blur to shadowmap texture?
        
        - provide options to omit/disable expensive lighting features- not all
          games may choose to utilize such features so why add the extra overhead?
        
        - clean up codebase/remove any remaining todos.
        */
        
        XJGE.init("/org/xjge/assets/", "org.xjge.scenes.", null, false, true, true, false, false);
        
        Game.setScene(new PlaneScene());
        
        XJGE.start();
    }
    
}