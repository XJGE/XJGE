package org.xjge.test;

import java.beans.PropertyChangeListener;
import org.xjge.core.Game;
import org.xjge.core.Scene;
import org.xjge.core.XJGE;

//Created: May 30, 2024

/**
 * @author J Hoffman
 * @since  
 */
public class Main {

    public static void main(String args[]) {
        XJGE.init("/org/xjge/assets/", "org.xjge.test.", null);
        
        Scene testScene = new TestScene();
        XJGE.addObserver((PropertyChangeListener) testScene);
        Game.setScene(testScene);
        
        XJGE.start();
    }
    
}