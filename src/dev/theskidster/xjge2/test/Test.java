package dev.theskidster.xjge2.test;

import dev.theskidster.xjge2.core.Logger;
import dev.theskidster.xjge2.core.WinKit;
import dev.theskidster.xjge2.core.Window;
import dev.theskidster.xjge2.core.XJGE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

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
        
        TestEntity e = new TestEntity();
        
        XJGE.setScene(new TestScene());
        
        //TODO: draw triangle.
        
        //Input.setDevicePuppet(GLFW_JOYSTICK_1, e.puppet);
        //Input.setDevicePuppet(KEY_MOUSE_COMBO, e.puppet);
        
        glfwSetKeyCallback(Window.HANDLE, (window, key, scancode, action, mods) -> {
            if(action == GLFW_PRESS) {
                switch(key) {
                    case GLFW_KEY_F1 -> {
                        Window.setFullscreen(!Window.getFullscreen());
                    }
                    
                    case GLFW_KEY_LEFT -> {
                        WinKit.setVSyncEnabled(!WinKit.getVSyncEnabled());
                    }
                    
                    case GLFW_KEY_RIGHT -> {
                        Window.setMonitor("next");
                    }
                    
                    case GLFW_KEY_UP -> {
                        Logger.logInfo(Window.getMonitor().getInfo());
                    }
                    
                    case GLFW_KEY_DOWN -> {
                        Window.getMonitor().setVideoMode("prev");
                    }
                }
            }
        });
        
        XJGE.start();
        
    }
    
}