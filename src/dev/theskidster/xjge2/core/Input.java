package dev.theskidster.xjge2.core;

import java.util.HashMap;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_5;
import static org.lwjgl.glfw.GLFW.glfwJoystickPresent;

/**
 * @author J Hoffman
 * Created: May 3, 2021
 */

public final class Input {

    public static final int KEY_MOUSE_COMBO = -1;
    
    static final HashMap<Integer, InputDevice> inputDevices = new HashMap<>();
    
    static {
        inputDevices.put(KEY_MOUSE_COMBO, new KeyMouseCombo(KEY_MOUSE_COMBO));
    }
    
    static void findInputDevices() {
        for(int i = 0; i < GLFW_JOYSTICK_5; i++) {
            if(inputDevices.containsKey(i)) {
                if(glfwJoystickPresent(i)) {
                    inputDevices.put(i, new Gamepad((Gamepad) inputDevices.get(i)));
                }
            } else {
                if(glfwJoystickPresent(i)) {
                    inputDevices.put(i, new Gamepad(i));
                    //if(i < GLFW_JOYSTICK_5) window.connected[i] = true;
                }
            }
        }
    }
    
    static void pollInput() {
        inputDevices.forEach((id, device) -> {
            if(device.enabled) device.poll();
        });
    }
    
    public static int getNumDevices() {
        return inputDevices.size();
    }
    
    public static void setDeviceSensitivity(int id, float sensitivity) {
        
    }
    
    public static void setDeviceEnabled(int id, boolean enabled) {
        
    }
    
    public static void setDevicePuppet(int id, Puppet puppet) {
        if(inputDevices.containsKey(id)) {
            InputDevice device = inputDevices.get(id);
            device.puppetSetEvents.add(puppet);
        } else {
            //TODO: log warning
        }
    }
    
    public static void bindPreviousPuppet(int id) {
        
    }
    
}