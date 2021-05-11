package dev.theskidster.xjge2.test;

import dev.theskidster.xjge2.core.Game;
import dev.theskidster.xjge2.core.XJGE;

/**
 * @author J Hoffman
 * Created: Apr 28, 2021
 */

public class Test {

    /*
    TODO:
    this class and package are temporary and provided for testing purposes only!
    they should not be included in releases!
    */
    
    public static void main(String args[]) {
        
        XJGE.init("/dev/theskidster/xjge2/assets/", true, true);
        
        Game.setScene(new TestScene());
        
        XJGE.start();
        
    }
    
}