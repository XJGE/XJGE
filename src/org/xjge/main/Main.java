package org.xjge.main;

import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_2;
import org.xjge.core.Game;
import org.xjge.core.Input;
import static org.xjge.core.Input.AI_GAMEPAD_1;
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
        
        Input.setDeviceEnabled(AI_GAMEPAD_1, true);
        Input.setDeviceEnabled(GLFW_JOYSTICK_2, false);
        
        Game.setScene(new TestScene("test"));
        
        XJGE.start();
        
    }
    
}
