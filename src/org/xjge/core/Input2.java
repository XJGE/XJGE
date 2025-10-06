package org.xjge.core;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import static org.lwjgl.glfw.GLFW.*;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class Input2 {

    private static final boolean gamepadConnected[] = new boolean[4];
    
    public static final int NO_DEVICE = -1;
    public static final int KEYBOARD  = 4;
    public static final int MOUSE     = 5;
    
    private static final Observable observable = new Observable(Input2.class);
    
    private static final Queue<Controllable> controllableQueue = new LinkedList<>();
    private static final Map<UUID, Controllable> controllables = new HashMap<>();
    private static final Map<Integer, InputDevice2> inputDevices = new HashMap<>();
    
    static {
        for(int i = 0; i < GLFW_JOYSTICK_5; i++) {
            observable.properties.put("GAMEPAD_" + i + "_CONNECTED", gamepadConnected[i]);
        }
        
        glfwSetJoystickCallback((jid, event) -> {
            switch(event) {
                case GLFW_CONNECTED -> {
                    findDevices();
                    
                    if(jid < GLFW_JOYSTICK_5) {
                        gamepadConnected[jid] = true;
                        observable.notifyObservers("GAMEPAD_" + jid + "_CONNECTED", gamepadConnected[jid]);
                    }
                    
                    Logger.logInfo("Input device \"" + getDeviceName(jid) + "\" " +
                                   "connected at position " + jid);
                }
                case GLFW_DISCONNECTED -> {
                    findDevices();
                    
                    if(jid < GLFW_JOYSTICK_5) {
                        gamepadConnected[jid] = false;
                        observable.notifyObservers("GAMEPAD_" + jid + "_CONNECTED", gamepadConnected[jid]);
                    }
                    
                    Logger.logInfo("Input device \"" + getDeviceName(jid) + "\" " +
                                   "disconnected at position " + jid);
                }
            }
        });
    }
    
    static void findDevices() {
        
    }
    
    static void update(double targetDelta, double trueDelta) {
        //Cache the current control state of all input devices for this frame
        inputDevices.values().forEach(device -> device.captureControlState());
        
        //Fulfill any pending requests to register controllable objects
        while(!controllableQueue.isEmpty()) {
            Controllable controllable = controllableQueue.poll();
            controllables.put(controllable.uuid, controllable);
        }
        
        //Process the user-defined actions of registered controllable objects
        controllables.values().forEach(controllable -> {
            InputDevice2 device = inputDevices.get(controllable.getInputDeviceID());
            
            if(device != null && device.enabled && !controllable.actions.isEmpty()) {
                controllable.actions.forEach((control, action) -> {
                    action.perform(device, control, targetDelta, trueDelta);
                });
            }
        });
        
        //Remove any registered controllable objects that have no input device configured
        controllables.values().removeIf(controllable -> controllable.getInputDeviceID() == NO_DEVICE);
    }
    
    static void addControllable(Controllable controllable) {
        
    }
    
    public static void addObserver(PropertyChangeListener observer) {
        observable.addObserver(observer);
    }
    
    public static void removeObserver(PropertyChangeListener observer) {
        observable.removeObserver(observer);
    }
    
    public static int createVirtualDevice() {
        return 0;
    }
    
    public static void destroyVirtualDevice(int deviceID) {
        
    }
    
    public static void setVirtualDeviceInput(int deviceID, Control control, float inputValue) {
        
    }
    
    public static boolean getDeviceEnabled(int deviceID) {
        if(inputDevices.containsKey(deviceID)) {
            return inputDevices.get(deviceID).enabled;
        } else {
            return false;
        }
    }
    
    public static boolean getDeviceConnectionStatus(int deviceID) {
        if(inputDevices.containsKey(deviceID)) {
            return switch(deviceID) {
                case GLFW_JOYSTICK_1, GLFW_JOYSTICK_2, 
                     GLFW_JOYSTICK_3, GLFW_JOYSTICK_4 -> gamepadConnected[deviceID];
                default -> true;
            };
        } else {
            return false;
        }
    }
    
    public static String getDeviceName(int deviceID) {
        if(inputDevices.containsKey(deviceID)) {
            return inputDevices.get(deviceID).name;
        } else {
            return null;
        }
    }
    
}