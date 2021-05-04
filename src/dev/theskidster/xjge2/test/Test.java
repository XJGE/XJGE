package dev.theskidster.xjge2.test;

import dev.theskidster.xjge2.core.Input;
import dev.theskidster.xjge2.core.XJGE;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;

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
        
        XJGE.init("/dev/theskidster/xjge2/assets/", false);
        
        Entity e = new Entity();
        
        Input.setDevicePuppet(GLFW_JOYSTICK_1, e.puppet);
        //Input.setDevicePuppet(KEY_MOUSE_COMBO, e.puppet);
        
        /*
        glfwSetKeyCallback(Window.HANDLE, (window, key, scancode, action, mods) -> {
            if(action == GLFW_PRESS) {
                switch(key) {
                    case GLFW_KEY_F1 -> {
                        Window.setFullscreen(!Window.getFullscreen());
                    }
                    
                    case GLFW_KEY_LEFT -> {
                        Window.setMonitor("prev");
                    }
                    
                    case GLFW_KEY_RIGHT -> {
                        Window.setMonitor("next");
                    }
                    
                    case GLFW_KEY_UP -> {
                        Window.getMonitor().setVideoMode("-1ty");
                    }
                    
                    case GLFW_KEY_DOWN -> {
                        Window.getMonitor().setVideoMode("prev");
                    }
                }
            }
        });
        */
        
        XJGE.start();
        
    }
    
}