package org.xjge.main;

import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_3;
import org.xjge.core.Game;
import org.xjge.core.Input;
import org.xjge.core.XJGE;

/**
 * Apr 12, 2022
 */

/**
 * @author J Hoffman
 * @since  
 */
public class Main {

    public static void main(String args[]) {
        
        XJGE.init("/org/xjge/assets/", "org.xjge.main.", null);
        
        System.out.println(Input.getDevicePresent(GLFW_JOYSTICK_3));
        
        Game.setScene(new TestScene("test"));
        
        XJGE.start();
        
    }
    
}
