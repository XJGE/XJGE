package org.xjge.core;

import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import static org.lwjgl.glfw.GLFW.*;

/**
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public class Input2 {
    
    public static final int NO_DEVICE = -1;
    public static final int KEYBOARD  = 4;
    public static final int MOUSE     = 5;
    
    private static final Observable observable = new Observable(Input2.class);
    
    private static final Queue<Runnable> deviceConnectionEvents  = new ConcurrentLinkedQueue<>();
    private static final Queue<Controllable> controllableQueue   = new LinkedList<>();
    private static final Map<UUID, Controllable> controllables   = new HashMap<>();
    private static final Map<Integer, InputDevice2> inputDevices = new HashMap<>();
    
    static {
        for(int i = 0; i < GLFW_JOYSTICK_5; i++) {
            var gamepad = new InputDeviceGamepad(i);
            inputDevices.put(i, gamepad);
            observable.properties.put("INPUT_DEVICE_" + i + "_CONNECTED", gamepad.connected);
        }
        
        inputDevices.put(KEYBOARD, new InputDeviceKeyboard());
        inputDevices.put(MOUSE, new InputDeviceMouse(Window.getMouse()));
        
        glfwSetJoystickCallback((id, event) -> {
            if(id < GLFW_JOYSTICK_5) {
                deviceConnectionEvents.add(() -> {
                    var gamepad = (InputDeviceGamepad) inputDevices.get(id);

                    switch(event) {
                        case GLFW_CONNECTED -> {
                            Logger.logInfo((gamepad.validate() ? "Gamepad" : "Generic joystick") +
                                           " \"" + gamepad.name + "\" connected at index " + id);
                        }

                        case GLFW_DISCONNECTED -> {
                            gamepad.freeState();
                            Logger.logInfo("Input device \"" + gamepad.name + "\" disconnected at index " + id);
                        }
                    }
                    
                    observable.notifyObservers("INPUT_DEVICE_" + id + "_CONNECTED", gamepad.connected);
                });
            }
        });
    }
    
    static void update(double targetDelta, double trueDelta) {
        //Proccess any input device connection events
        while(!deviceConnectionEvents.isEmpty()) deviceConnectionEvents.poll().run();
        
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
            
            if(device != null && device.connected && device.enabled && !controllable.actions.isEmpty()) {
                controllable.actions.forEach((control, action) -> {
                    action.perform(device, control, targetDelta, trueDelta);
                });
            }
        });
        
        //Remove any registered controllable objects that have no input device configured
        controllables.values().removeIf(controllable -> controllable.getInputDeviceID() == NO_DEVICE);
    }
    
    static void registerControllable(Controllable controllable) {
        controllableQueue.add(controllable);
    }
    
    public static void addObserver(PropertyChangeListener observer) {
        observable.addObserver(observer);
    }
    
    public static void removeObserver(PropertyChangeListener observer) {
        observable.removeObserver(observer);
    }
    
    public static int createVirtualDevice() {
        return 0; //TODO: create queue for adding this so we don't risk a concurrent modification of inputDevices
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
                     GLFW_JOYSTICK_3, GLFW_JOYSTICK_4 -> inputDevices.get(deviceID).connected;
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
    
    public static void setDeviceSettings(int deviceID, Map<String, Float> settings) {
        //TODO: add this
    }
    
    public static void setDeviceControlBindings(int deviceID, Map<Control, Integer> controlBindings) {
        //TODO: add this (remember to include check for CONTROL_UNSUPPORTED binding)
    }
    
    public static Map<String, Float> getDeviceSettings(int deviceID) {
        return Collections.unmodifiableMap(inputDevices.get(deviceID).settings);
    }
    
    public static Map<Control, Integer> getDeviceControlBindings(int deviceID) {
        return Collections.unmodifiableMap(inputDevices.get(deviceID).controlBindings);
    }
    
    public static void setKeyboardAxisBindings(boolean leftStick, int upKey, int leftKey, int downKey, int rightKey) {
        InputDeviceKeyboard keyboard = (InputDeviceKeyboard) inputDevices.get(KEYBOARD);
        
        if(leftStick) {
            keyboard.leftAxisBindings[0] = upKey;
            keyboard.leftAxisBindings[1] = leftKey;
            keyboard.leftAxisBindings[2] = downKey;
            keyboard.leftAxisBindings[3] = rightKey;
        } else {
            keyboard.rightAxisBindings[0] = upKey;
            keyboard.rightAxisBindings[1] = leftKey;
            keyboard.rightAxisBindings[2] = downKey;
            keyboard.rightAxisBindings[3] = rightKey;
        }
    }
    
    public static int[] getKeyboardAxisBindings(boolean leftStick) {
        InputDeviceKeyboard keyboard = (InputDeviceKeyboard) inputDevices.get(KEYBOARD);
        return (leftStick) ? keyboard.leftAxisBindings : keyboard.rightAxisBindings;
    }
    
}