package org.xjge.core;

import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import static org.lwjgl.glfw.GLFW.*;
import static org.xjge.core.InputDevice.CONTROL_UNSUPPORTED;
import static org.xjge.core.InputDevice.CONTROL_VIRTUAL;

/**
 * Provides a single point of access through which the state of peripheral 
 * input devices may be managed.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class InputSystem {
    
    public static final int NO_DEVICE = -1;
    public static final int KEYBOARD  = 4;
    public static final int MOUSE     = 5;
    
    private static final Observable observable = new Observable(InputSystem.class);
    
    private static final AtomicInteger virtualDeviceCounter     = new AtomicInteger(100);
    private static final Queue<Runnable> deviceConnectionEvents = new ConcurrentLinkedQueue<>();
    private static final Queue<Controllable> controllableQueue  = new LinkedList<>();
    private static final Map<UUID, Controllable> controllables  = new HashMap<>();
    private static final Map<Integer, InputDevice> inputDevices = new HashMap<>();
    
    static void init() {
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
            InputDevice device = inputDevices.get(controllable.getInputDeviceID());
            
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
    
    static void freeDevices() {
        inputDevices.values().forEach(device -> {
            device.connected = false;
            if(device instanceof InputDeviceGamepad gamepad) gamepad.freeState();
        });
    }
    
    static void addTerminalObservers(Terminal terminal) {
        terminal.observable.addObserver((InputDeviceMouse) inputDevices.get(MOUSE));
        terminal.observable.addObserver((InputDeviceKeyboard) inputDevices.get(KEYBOARD));
    }
    
    public static void addObserver(PropertyChangeListener observer) {
        observable.addObserver(observer);
    }
    
    public static void removeObserver(PropertyChangeListener observer) {
        observable.removeObserver(observer);
    }
    
    public static int getGamepadCount() {
        int count = 0;
        
        for(InputDevice device : inputDevices.values()) {
            if(device instanceof InputDeviceGamepad gamepad && gamepad.connected) count++;
        }
        
        return count;
    }
    
    public static int createVirtualDevice() {
        int deviceID = virtualDeviceCounter.getAndIncrement();
        
        deviceConnectionEvents.add(() -> {
            var virtualDevice = new InputDeviceVirtual(deviceID);
            inputDevices.put(deviceID, virtualDevice);
            observable.notifyObservers("INPUT_DEVICE_" + deviceID + "_CONNECTED", virtualDevice.connected);
            Logger.logInfo("New virtual input device created and assigned to index " + deviceID);
        });
        
        return deviceID;
    }
    
    public static void destroyVirtualDevice(int deviceID) {
        deviceConnectionEvents.add(() -> {
            InputDevice device = inputDevices.get(deviceID);
            
            if(device == null) {
                Logger.logWarning("Attempted to destroy a virtual input device at index " + 
                                  deviceID + " but it does not exist", null);
                return;
            }
            
            if(!(device instanceof InputDeviceVirtual)) {
                Logger.logWarning("Operation ignored. User attempted to destroy a " + 
                                  "non-virtual input device at index " + deviceID, null);
                return;
            }
            
            device.connected = false;
            observable.notifyObservers("INPUT_DEVICE_" + deviceID + "_CONNECTED", device.connected);
            Logger.logInfo("Virtual input device " + deviceID + " destroyed successfully");
            inputDevices.remove(deviceID);
        });
    }
    
    public static void setVirtualDeviceInput(int deviceID, Control control, float inputValue) {
        InputDevice device = inputDevices.get(deviceID);
        
        if(device == null) {
            Logger.logWarning("Attempted to change the input state of a virtual " + 
                              "input device at index " + deviceID + " but it does not exist", null);
            return;
        }
        
        if(!(device instanceof InputDeviceVirtual)) {
            Logger.logWarning("Operation ignored. User attempted to change the " + 
                              "input state of a non-virtual input device at index " + deviceID, null);
            return;
        }
        
        device.controlValues.put(control, XJGE.clampValue(-1f, 1f, inputValue));
    }
    
    public static int[] getKeyboardAxisBindings(boolean leftStick) {
        InputDeviceKeyboard keyboard = (InputDeviceKeyboard) inputDevices.get(KEYBOARD);
        return (leftStick) ? keyboard.leftAxisBindings.clone() : keyboard.rightAxisBindings.clone();
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
    
    public static Map<String, Float> getDeviceSettings(int deviceID) {
        return Collections.unmodifiableMap(inputDevices.get(deviceID).settings);
    }
    
    public static Map<Control, Integer> getDeviceControlBindings(int deviceID) {
        return Collections.unmodifiableMap(inputDevices.get(deviceID).controlBindings);
    }
    
    public static boolean setDeviceSettings(int deviceID, Map<String, Float> settings) {
        InputDevice device = inputDevices.get(deviceID);
        
        if(device == null) {
            Logger.logWarning("Operation ignored. User attempted to modify settings for " + 
                              "a non-existent input device at index " + deviceID, null);
            return false;
        }
        
        for(var entry : settings.entrySet()) {
            String key = entry.getKey();
            Float value = entry.getValue();

            if(value == null || Float.isNaN(value) || Float.isInfinite(value)) {
                Logger.logWarning("Operation ignored. Invalid setting value used for \"" + 
                                  key + "\" on the input device at index " + deviceID, null);
                continue;
            }

            device.settings.put(key, value);
        }
        
        return true;
    }
    
    public static boolean setDeviceControlBindings(int deviceID, Map<Control, Integer> controlBindings) {
        InputDevice device = inputDevices.get(deviceID);
        
        if(device == null) {
            Logger.logWarning("Operation ignored. User attempted to change control " + 
                              "bindings for a non-existent input device at index " + deviceID, null);
            return false;
        }
        
        for(var entry : controlBindings.entrySet()) {
            Control control = entry.getKey();
            int binding     = entry.getValue();

            //Skip unsupported or virtual bindings
            if(binding == CONTROL_UNSUPPORTED || binding == CONTROL_VIRTUAL) {
                Logger.logInfo("Control rebind skipped for " + control + ". The input device at index " + 
                               deviceID + " does not support this control or provides it virtually");
                continue;
            }

            //Validate control type per device
            if(!device.controlBindings.containsKey(control)) {
                Logger.logWarning("Control " + control + " not recognized by the input device at index " + deviceID, null);
                continue;
            }

            device.controlBindings.put(control, binding);
        }
        
        return true;
    }
    
}