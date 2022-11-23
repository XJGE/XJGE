package org.xjge.main;

import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Game;
import org.xjge.core.XJGE;

/**
 *
 * @author thesk
 */
public class Main {

    public static void main(String args[]) {
        
        XJGE.init("/org/xjge/assets/", "org.xjge.main.", null);
        
        Game.setScene(new TestScene());
        
        XJGE.start();
        
    } 
    
}
