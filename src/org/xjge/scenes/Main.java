package org.xjge.scenes;

import org.xjge.core.Game;
import org.xjge.core.Hardware;
import org.xjge.core.Speaker;
import org.xjge.core.XJGE;

/**
 * Mar 6, 2022
 */

/**
 * @author J Hoffman
 * @since  
 */
public class Main {

    public static void main(String args[]) {
        
        XJGE.init("/org/xjge/assets/", "org.xjge.scenes.", null);
        
        Game.setScene(new TestScene());
        
        Speaker speaker = Hardware.getCurrentSpeaker();
        
        speaker.listExtensions();
        
        XJGE.start();
        
    }
    
}
