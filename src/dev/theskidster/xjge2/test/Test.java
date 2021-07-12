package dev.theskidster.xjge2.test;

import dev.theskidster.xjge2.core.Game;
import dev.theskidster.xjge2.core.Input;
import static dev.theskidster.xjge2.core.Input.KEY_MOUSE_COMBO;
import dev.theskidster.xjge2.core.XJGE;
import org.joml.Vector2i;

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
        
        Vector2i res = new Vector2i(384, 216);
        
        XJGE.init("/dev/theskidster/xjge2/assets/", "dev.theskidster.xjge2.test.", true, null, true);
        
        //Input.setDeviceEnabled(0, false);
        
        Game.setScene(new TestScene());
        
        XJGE.start();
        
    }
    
}