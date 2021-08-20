package org.xjge.test;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_HIDDEN;
import org.xjge.core.Game;
import org.xjge.core.Window;
import org.xjge.core.XJGE;

/*
 * Created: Aug 14, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class Main {
    
    public static void main(String args[]) {
        
        XJGE.init("/org/xjge/assets/", "org.xjge.test.", true, null, true, false);
        
        Game.setScene(new TestScene());
        
        XJGE.start();
    }

}
