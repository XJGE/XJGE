package dev.theskidster.xjge2.core;

import static dev.theskidster.xjge2.core.Control.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @author J Hoffman
 * Created: May 3, 2021
 */

/**
 * Provides a single point of access through which the state of peripheral input devices may be managed.
 */
public final class Input {

    /**
     * Special case value associated with the {@link KeyMouseCombo} input device.
     */
    public static final int KEY_MOUSE_COMBO = -1;
    
    private static final boolean connected[] = new boolean[4];
    
    /**
     * A widget object provided by the engine that will be rendered anytime an input device is disconnected. It is left uninitialized by 
     * default- you will need to initialize it with your own subclass of {@link Widget} for it to appear.
     */
    public static Widget missingGamepad;
    
    private static final HashMap<Integer, InputDevice> inputDevices = new HashMap<>();
    
    private static final HashMap<Integer, HashMap<Control, Integer>> controlConfigs = new HashMap<>();
    private static final HashMap<Integer, Float> sensitivityConfigs;
    private static final HashMap<Integer, Float> deadzoneConfigs;
    
    static {
        sensitivityConfigs = new HashMap<>() {{
            put(KEY_MOUSE_COMBO, 1f);
            put(GLFW_JOYSTICK_1, 1f);
            put(GLFW_JOYSTICK_2, 1f);
            put(GLFW_JOYSTICK_3, 1f);
            put(GLFW_JOYSTICK_4, 1f);
        }};
        
        deadzoneConfigs = new HashMap<>() {{
            put(KEY_MOUSE_COMBO, 0.15f);
            put(GLFW_JOYSTICK_1, 0.15f);
            put(GLFW_JOYSTICK_2, 0.15f);
            put(GLFW_JOYSTICK_3, 0.15f);
            put(GLFW_JOYSTICK_4, 0.15f);
        }};
        
        var keyMouseConfig = new HashMap<Control, Integer>() {{
            put(CROSS,         GLFW_KEY_SPACE);
            put(CIRCLE,        GLFW_KEY_Q);
            put(SQUARE,        GLFW_KEY_R);
            put(TRIANGLE,      GLFW_KEY_E);
            put(L1,            GLFW_KEY_1);
            put(R1,            GLFW_KEY_2);
            put(SHARE,         GLFW_KEY_TAB);
            put(OPTIONS,       GLFW_KEY_ESCAPE);
            put(PS_BUTTON,     GLFW_KEY_ENTER);
            put(L3,            GLFW_KEY_LEFT_SHIFT);
            put(R3,            GLFW_KEY_C);
            put(DPAD_UP,       GLFW_KEY_UP);
            put(DPAD_RIGHT,    GLFW_KEY_RIGHT);
            put(DPAD_DOWN,     GLFW_KEY_DOWN);
            put(DPAD_LEFT,     GLFW_KEY_LEFT);
            put(LEFT_STICK_X,  GLFW_KEY_A | GLFW_KEY_D);
            put(LEFT_STICK_Y,  GLFW_KEY_W | GLFW_KEY_S);
            put(RIGHT_STICK_X, 0);
            put(RIGHT_STICK_Y, 0);
            put(L2,            GLFW_MOUSE_BUTTON_LEFT);
            put(R2,            GLFW_MOUSE_BUTTON_RIGHT);
        }};
        
        controlConfigs.put(KEY_MOUSE_COMBO, keyMouseConfig);
        inputDevices.put(KEY_MOUSE_COMBO, new KeyMouseCombo(KEY_MOUSE_COMBO, 1f, 0.15f, keyMouseConfig));
        
        for(int i = 0; i < GLFW_JOYSTICK_5; i++) {
            var gamepadConfig = new HashMap<Control, Integer>() {{
                put(CROSS,         GLFW_GAMEPAD_BUTTON_A);
                put(CIRCLE,        GLFW_GAMEPAD_BUTTON_B);
                put(SQUARE,        GLFW_GAMEPAD_BUTTON_X);
                put(TRIANGLE,      GLFW_GAMEPAD_BUTTON_Y);
                put(L1,            GLFW_GAMEPAD_BUTTON_LEFT_BUMPER);
                put(R1,            GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER);
                put(SHARE,         GLFW_GAMEPAD_BUTTON_BACK);
                put(OPTIONS,       GLFW_GAMEPAD_BUTTON_START);
                put(PS_BUTTON,     GLFW_GAMEPAD_BUTTON_GUIDE);
                put(L3,            GLFW_GAMEPAD_BUTTON_LEFT_THUMB);
                put(R3,            GLFW_GAMEPAD_BUTTON_RIGHT_THUMB);
                put(DPAD_UP,       GLFW_GAMEPAD_BUTTON_DPAD_UP);
                put(DPAD_RIGHT,    GLFW_GAMEPAD_BUTTON_DPAD_RIGHT);
                put(DPAD_DOWN,     GLFW_GAMEPAD_BUTTON_DPAD_DOWN);
                put(DPAD_LEFT,     GLFW_GAMEPAD_BUTTON_DPAD_LEFT);
                put(LEFT_STICK_X,  GLFW_GAMEPAD_AXIS_LEFT_X);
                put(LEFT_STICK_Y,  GLFW_GAMEPAD_AXIS_LEFT_Y);
                put(RIGHT_STICK_X, GLFW_GAMEPAD_AXIS_RIGHT_X);
                put(RIGHT_STICK_Y, GLFW_GAMEPAD_AXIS_RIGHT_Y);
                put(L2,            GLFW_GAMEPAD_AXIS_LEFT_TRIGGER);
                put(R2,            GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER);
            }};
            
            controlConfigs.put(i, gamepadConfig);
        }
        
        glfwSetJoystickCallback((jid, event) -> {
            switch(event) {
                case GLFW_CONNECTED -> {
                    findInputDevices();
                    
                    if(jid < GLFW_JOYSTICK_5) connected[jid] = true;
                    
                    Logger.setDomain("input");
                    Logger.logInfo("Input device \"" + getDeviceName(jid) + "\" " +
                                   "connected at position " + jid + ".");
                    Logger.setDomain(null);
                }
                
                case GLFW_DISCONNECTED -> {
                    findInputDevices();
                    
                    Logger.setDomain("input");
                    Logger.logInfo("Input device \"" + getDeviceName(jid) + "\" " +
                                   "disconnected at position " + jid + ".");
                    Logger.setDomain(null);
                    
                    if(jid < GLFW_JOYSTICK_5 && Window.visible) {
                        connected[jid] = false;
                        
                        disableAllExcept(jid);
                        addDisConWidget(jid);
                        
                        Game.addEvent(new EventGamepad(jid));
                    }
                }
            }
        });
    }
    
    /**
     * Ceases capturing input from all currently connected input devices except the one specified.
     * 
     * @param deviceID the ID number of the input device that will remain active
     */
    private static void disableAllExcept(int deviceID) {
        inputDevices.forEach((id, device) -> {
            if(device instanceof Gamepad) {
                ((Gamepad) device).enabled = (id == deviceID);
            }
        });
    }
    
    /**
     * Displays the implementation-defined widget used to indicate that a controller has been disconnected.
     * 
     * @param jid the unique value provided by GLFW used to identify the input device
     */
    private static void addDisConWidget(int jid) {
        if(missingGamepad != null) {
            if(!XJGE.getViewportActive(jid)) XJGE.addUIWidget(GLFW_JOYSTICK_1, "discon " + jid, missingGamepad);
            else                             XJGE.addUIWidget(jid, "discon " + jid, missingGamepad);
        } else {
            Logger.setDomain("input");
            Logger.logWarning("No warning message has been provided to users " + 
                              "regarding disconnected controllers. Supply one " + 
                              "by initializing the Input.missingGamepad object " + 
                              "with a custom Widget.", 
                              null);
            Logger.setDomain(null);
        }
    }
    
    /**
     * Finds every available input device currently connected to the system.
     */
    static void findInputDevices() {
        for(int i = 0; i < GLFW_JOYSTICK_5; i++) {
            if(inputDevices.containsKey(i)) {
                if(glfwJoystickPresent(i)) {
                    inputDevices.put(i, new Gamepad((Gamepad) inputDevices.get(i)));
                }
            } else {
                if(glfwJoystickPresent(i)) {
                    inputDevices.put(i, new Gamepad(i, sensitivityConfigs.get(i), deadzoneConfigs.get(i), controlConfigs.get(i)));
                    if(i < GLFW_JOYSTICK_5) connected[i] = true;
                }
            }
        }
    }
    
    /**
     * Processes the input actions of every input device connected to the system that isn't currently in a disabled state.
     */
    static void pollInput() {
        inputDevices.forEach((id, device) -> {
            if(device.enabled) device.poll();
        });
    }
    
    /**
     * Imports and applies user input preferences from a configuration file generated by the engine.
     */
    static void importControls() {
        try {
            InputStream stream = new FileInputStream(XJGE.PWD.toString() + "/controls.cfg");
            XMLStreamReader xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
            
            int id = 0;
            
            while(xmlReader.hasNext()) {
                final int ID = id;
                
                switch(xmlReader.next()) {
                    case XMLStreamConstants.START_ELEMENT -> {
                        if(xmlReader.getName().getLocalPart().equals("device")) {
                            id = Integer.parseInt(xmlReader.getAttributeValue(null, "id"));
                            sensitivityConfigs.put(id, Float.parseFloat(xmlReader.getAttributeValue(null, "sensitivity")));
                            deadzoneConfigs.put(id, Float.parseFloat(xmlReader.getAttributeValue(null, "deadzone")));
                        } else if(xmlReader.getName().getLocalPart().equals("mapping")) {
                            String control = xmlReader.getAttributeValue(null, "control");
                            int button     = Integer.parseInt(xmlReader.getAttributeValue(null, "button"));
                            
                            controlConfigs.get(ID).put(Control.valueOf(control), button);
                        }
                    }
                    
                    case XMLStreamConstants.END_ELEMENT -> {
                        if(xmlReader.getName().getLocalPart().equals("config")) {
                            xmlReader.close();
                        }
                    }
                }
            }
            
        } catch(FileNotFoundException | XMLStreamException | IllegalArgumentException e) {
            Logger.setDomain("input");
            Logger.logWarning("Failed to import user-defined controls.", e);
            Logger.setDomain(null);
        }
    }
    
    /**
     * Exports a configuration file containing user input preferences.
     */
    static void exportControls() {
        try {
            FileWriter file = new FileWriter("controls.cfg");
            
            try(PrintWriter output = new PrintWriter(file)) {
                output.println("<config>");
                
                controlConfigs.forEach((deviceID, mapping) -> {
                    float sensitivity = 1f;
                    float deadzone    = 0.15f;
                    
                    if(inputDevices.containsKey(deviceID)) {
                        sensitivity = inputDevices.get(deviceID).sensitivity;
                        deadzone    = inputDevices.get(deviceID).deadzone;
                    }
                    
                    output.append("\t<device id=\"")
                          .append(deviceID + "\" ")
                          .append("sensitivity=\"")
                          .append(sensitivity + "\" ")
                          .append("deadzone=\"")
                          .append(deadzone + "\">")
                          .append(System.lineSeparator());
                    
                    mapping.forEach((control, button) -> {
                        output.append("\t\t<mapping control=\"")
                              .append(control.toString())
                              .append("\" button=\"")
                              .append(button + "\"/>")
                              .append(System.lineSeparator());
                    });
                    
                    output.println("\t</device>");
                });
                
                output.append("</config>");
            }
        } catch(IOException e) {
            Logger.setDomain("input");
            Logger.logWarning("Failed to export user defined controls.", e);
            Logger.setDomain(null);
        }
    }
    
    /**
     * Obtains the amount of input devices that are currently connected to the system. The value provided by this method includes only 
     * peripheral devices such as {@linkplain Gamepad gamepads}.
     * 
     * @return the number of connected input devices
     */
    public static int getNumDevices() {
        return inputDevices.size() - 1;
    }
    
    /**
     * Checks whether or not an input device is currently connected to the system.
     * 
     * @param deviceID the number which corresponds to the input device in question. One of: 
     * {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK_1}, {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_2}, 
     * {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_3 GLFW_JOYSTICK_3}, {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_4 GLFW_JOYSTICK_4}, or 
     * {@link KEY_MOUSE_COMBO}.
     * 
     * @return true if the input device is connected
     */
    public static boolean getDevicePresent(int deviceID) {
        if(inputDevices.containsKey(deviceID)) {
            return connected[deviceID];
        } else {
            return false;
        }
    }
    
    /**
     * Obtains the sensitivity value an input device will use to adjust the responsiveness of input actions during gameplay.
     * <br><br>
     * It's recommended that gameplay systems instead use {@link Command#getDeviceSensitivity()} and reserve this method for instances 
     * where the current sensitivity value needs to be exposed to the user- such as an interface that allows users to edit their 
     * control preferences.
     * 
     * @param deviceID the number which corresponds to the input device in question. One of: 
     * {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK_1}, {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_2}, 
     * {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_3 GLFW_JOYSTICK_3}, {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_4 GLFW_JOYSTICK_4}, or 
     * {@link KEY_MOUSE_COMBO}.
     * 
     * @return the sensitivity value of the specified input device or the default value of 1.0 if the device cannot be found
     * 
     * @see setDeviceSensitivity(int, float)
     */
    public static float getDeviceSensitivity(int deviceID) {
        if(inputDevices.containsKey(deviceID)) {
            return inputDevices.get(deviceID).sensitivity;
        } else {
            return 1;
        }
    }
    
        /**
     * Obtains the deadzone value an input device will use to determine how much pressure must be applied to an analog stick before its 
     * input is recognized.
     * <br><br>
     * The deadzones effect is applied automatically by the engine. This method should be used in instances where the current deadzone
     * value needs to be exposed to the user- such as an interface that allows users to edit their control preferences.
     * 
     * @param deviceID the number which corresponds to the input device in question. One of: 
     * {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK_1}, {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_2}, 
     * {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_3 GLFW_JOYSTICK_3}, {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_4 GLFW_JOYSTICK_4}, or 
     * {@link KEY_MOUSE_COMBO}.
     * 
     * @return the deadzone value of the specified input device or the default value of 0.15 if the device cannot be found
     * 
     * @see setDeviceSensitivity(int, float)
     */
    public static float getDeviceDeadzone(int deviceID) {
        if(inputDevices.containsKey(deviceID)) {
            return inputDevices.get(deviceID).deadzone;
        } else {
            return 0.15f;
        }
    }
    
    /**
     * Checks whether or not an input device is currently allowing the engine to process its input actions.
     * 
     * @param deviceID the number which corresponds to the input device in question. One of: 
     * {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK_1}, {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_2}, 
     * {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_3 GLFW_JOYSTICK_3}, {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_4 GLFW_JOYSTICK_4}, or 
     * {@link KEY_MOUSE_COMBO}.
     * 
     * @return true if the input device is in an enabled state
     * 
     * @see setDeviceEnabled(int, boolean)
     */
    public static boolean getDeviceEnabled(int deviceID) {
        if(inputDevices.containsKey(deviceID)) {
            return inputDevices.get(deviceID).enabled;
        } else {
            return false;
        }
    }
    
    /**
     * Obtains the name of an input device. 
     * <br><br>
     * Device names are not guaranteed to be unique and typically reflect the model and or manufacturer of the device.
     * 
     * @param deviceID the number which corresponds to the input device in question. One of: 
     * {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK_1}, {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_2}, 
     * {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_3 GLFW_JOYSTICK_3}, {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_4 GLFW_JOYSTICK_4}, or 
     * {@link KEY_MOUSE_COMBO}.
     * 
     * @return a human-readable string of text used to further identify an input device
     */
    public static String getDeviceName(int deviceID) {
        if(inputDevices.containsKey(deviceID)) {
            return inputDevices.get(deviceID).name;
        } else {
            return null;
        }
    }
    
    /**
     * Obtains the current control configuration of an input device.
     * <br><br>
     * Control configurations are a part of user-defined preferences that will be retained between runtime sessions. These 
     * configurations dictate how {@link Control controls} on an input device will be mapped to actions in game.
     * <br><br>
     * More generally, a player may prefer the button associated with jumping to be changed to another button on the controller- the 
     * player can alter their devices control configuration to achieve this.
     * 
     * @param deviceID the number which corresponds to the input device in question. One of: 
     * {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK_1}, {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_2}, 
     * {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_3 GLFW_JOYSTICK_3}, {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_4 GLFW_JOYSTICK_4}, or 
     * {@link KEY_MOUSE_COMBO}.
     * 
     * @return an immutable collection containing the current control configuration of an input device
     * 
     * @see setDeviceControls(int, Map)
     */
    public static Map<Control, Integer> getDeviceControls(int deviceID) {
        if(inputDevices.containsKey(deviceID)) {
            return Collections.unmodifiableMap(controlConfigs.get(deviceID));
        } else {
            return null;
        }
    }
    
    /**
     * Obtains the axis values of the GLFW keys that will be used to mimic the action of an analog stick with the keyboard.
     * 
     * @return an array containing the values corresponding to the GLFW keys being used
     */
    public static int[] getKeyMouseAxisValues() {
        return ((KeyMouseCombo) inputDevices.get(KEY_MOUSE_COMBO)).axisValues;
    }
    
    /**
     * Obtains the current puppet object an input device is using (if any).
     * 
     * @param deviceID the number which corresponds to the input device in question. One of: 
     * {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK_1}, {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_2}, 
     * {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_3 GLFW_JOYSTICK_3}, {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_4 GLFW_JOYSTICK_4}, or 
     * {@link KEY_MOUSE_COMBO}.
     * 
     * @return the current puppet object of the specified input device or <b>null</b> if no puppet object is bound/the device cannot be found
     */
    public static Puppet getDevicePuppet(int deviceID) {
        if(inputDevices.containsKey(deviceID)) {
            try {
                return inputDevices.get(deviceID).puppets.peek();
            } catch(EmptyStackException e) {
                Logger.setDomain("input");
                Logger.logWarning("Input device " + deviceID + " \"" + inputDevices.get(deviceID).name + 
                                  "\" has no currently bound puppet object.", 
                                  e);
                Logger.setDomain(null);
                
                return null;
            }
        } else {
            Logger.setDomain("input");
            Logger.logWarning("Failed to get the current puppet of input device " + deviceID + 
                              ". Could not find an input device at this index.", 
                              null);
            Logger.setDomain(null);
            
            return null;
        }
    }
    
    /**
     * Sets the sensitivity value an input device will use to adjust the responsiveness of input actions during gameplay.
     * 
     * @param deviceID the number which corresponds to the input device in question. One of: 
     * {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK_1}, {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_2}, 
     * {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_3 GLFW_JOYSTICK_3}, {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_4 GLFW_JOYSTICK_4}, or 
     * {@link KEY_MOUSE_COMBO}.
     * @param sensitivity the new sensitivity value the device will use
     * 
     * @see getDeviceSensitivity(int)
     */
    public static void setDeviceSensitivity(int deviceID, float sensitivity) {
        Logger.setDomain("input");
        
        if(inputDevices.containsKey(deviceID)) {
            inputDevices.get(deviceID).sensitivity = sensitivity;
            sensitivityConfigs.put(deviceID, sensitivity);
            
            Logger.logInfo("Changed the sensitivity of input device " + deviceID + " \"" + 
                           inputDevices.get(deviceID).name + "\" to (" + sensitivity + ")");
        } else {
            Logger.logWarning("Failed to change the devices sensitivity. Could " + 
                              "not find an input device at index " + deviceID + ".",
                              null);
        }
        
        Logger.setDomain(null);
    }
    
    /**
     * 
     * @param deviceID the number which corresponds to the input device in question. One of: 
     * {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK_1}, {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_2}, 
     * {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_3 GLFW_JOYSTICK_3}, {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_4 GLFW_JOYSTICK_4}, or 
     * {@link KEY_MOUSE_COMBO}.
     * @param deadzone 
     */
    public static void setDeviceDeadzone(int deviceID, float deadzone) {
        Logger.setDomain("input");
        
        if(inputDevices.containsKey(deviceID)) {
            inputDevices.get(deviceID).deadzone = deadzone;
            deadzoneConfigs.put(deviceID, deadzone);
        } else {
            Logger.logWarning("Failed to change the devices deadzone value. Could " + 
                              "not find an input device at index " + deviceID + ".",
                              null);
        }
        
        Logger.setDomain(null);
    }
    
    /**
     * 
     * @param deviceID the number which corresponds to the input device in question. One of: 
     * {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK_1}, {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_2}, 
     * {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_3 GLFW_JOYSTICK_3}, {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_4 GLFW_JOYSTICK_4}, or 
     * {@link KEY_MOUSE_COMBO}.
     * @param enabled 
     */
    public static void setDeviceEnabled(int deviceID, boolean enabled) {
        Logger.setDomain("input");
        
        if(inputDevices.containsKey(deviceID)) {
            InputDevice device = inputDevices.get(deviceID);
            
            device.enabledStates.add(enabled);
            device.enabled = device.enabledStates.peek();
            
            if(device.enabled) Logger.logInfo("Enabled input device " + deviceID + " \"" + device.name + "\"");
            else               Logger.logInfo("Disabled input device " + deviceID + " \"" + device.name + "\"");
        } else {
            Logger.logWarning("Failed to change the enabled state of input device " + 
                              deviceID + "Could not find an input device at this index.",
                              null);
        }
        
        Logger.setDomain(null);
    }
    
    /**
     * 
     * @param deviceID the number which corresponds to the input device in question. One of: 
     * {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK_1}, {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_2}, 
     * {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_3 GLFW_JOYSTICK_3}, {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_4 GLFW_JOYSTICK_4}, or 
     * {@link KEY_MOUSE_COMBO}.
     */
    public static void revertEnabledState(int deviceID) {
        Logger.setDomain("input");
        
        if(inputDevices.containsKey(deviceID)) {
            InputDevice device = inputDevices.get(deviceID);
            
            try {
                device.enabledStates.pop();
                device.enabled = device.enabledStates.peek();
                Logger.logInfo("Reverted the enabled state of input device " + deviceID + 
                               " \"" + device.name + "\" to (" + device.enabled + ")");
            } catch(EmptyStackException e) {
                device.enabled = true;
            }
        } else {
            Logger.logWarning("Failed to revert the enabled state of input device " + 
                              deviceID + " Could not find an input device at this index.",
                              null);
        }
        
        Logger.setDomain(null);
    }
    
    /**
     * 
     * @param deviceID the number which corresponds to the input device in question. One of: 
     * {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK_1}, {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_2}, 
     * {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_3 GLFW_JOYSTICK_3}, {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_4 GLFW_JOYSTICK_4}, or 
     * {@link KEY_MOUSE_COMBO}.
     * @param config 
     */
    public static void setDeviceControls(int deviceID, Map<Control, Integer> config) {
        Logger.setDomain("input");
        
        if(inputDevices.containsKey(deviceID)) {
            controlConfigs.get(deviceID).putAll(config);
            
            Logger.logInfo("Changed the button configuration of input device " + 
                           deviceID + " \"" + inputDevices.get(deviceID).name + "\"");
        } else {
            Logger.logWarning("Failed to change the button configuration of input device " + 
                              deviceID + " Could not find an input device at this index.",
                              null);
        }
        
        Logger.setDomain(null);
    }
    
    /**
     * 
     * @param x1
     * @param x2
     * @param y1
     * @param y2 
     */
    public static void setKeyMouseAxisValues(int x1, int x2, int y1, int y2) {
        /*
        this needs to be set to whatever bitwise values are passed in place of the axis buttons.
        */
        
        ((KeyMouseCombo) inputDevices.get(KEY_MOUSE_COMBO)).axisValues[0] = x1;
        ((KeyMouseCombo) inputDevices.get(KEY_MOUSE_COMBO)).axisValues[1] = x2;
        ((KeyMouseCombo) inputDevices.get(KEY_MOUSE_COMBO)).axisValues[2] = y1;
        ((KeyMouseCombo) inputDevices.get(KEY_MOUSE_COMBO)).axisValues[3] = y2;
    }
    
    public static void setDevicePuppet(int id, Puppet puppet) {
        Logger.setDomain("input");
        
        if(inputDevices.containsKey(id)) {
            inputDevices.get(id).puppetSetEvents.add(puppet);
            
            Logger.logInfo("Changed the current puppet object of input device " + id + " \"" + 
                            inputDevices.get(id).name + "\" to (" + puppet.object.getClass().getCanonicalName() + ")");
        } else {
            Logger.logWarning("Failed to change the devices current puppet object. " + 
                              "Could not find an input device at index " + id + ".",
                              null);
        }
        
        Logger.setDomain(null);
    }
    
    /**
     * 
     * @param deviceID the number which corresponds to the input device in question. One of: 
     * {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK_1}, {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_2}, 
     * {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_3 GLFW_JOYSTICK_3}, {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_4 GLFW_JOYSTICK_4}, or 
     * {@link KEY_MOUSE_COMBO}.
     */
    public static void bindPreviousPuppet(int deviceID) {
        Logger.setDomain("input");
        
        if(inputDevices.containsKey(deviceID)) {
            InputDevice device = inputDevices.get(deviceID);
            
            if(device.puppets.size() > 1) {
                device.puppets.pop();
                setDevicePuppet(deviceID, device.puppets.peek());
            } else {
                Logger.logWarning("Failed to bind the previous puppet object for input device " + deviceID + 
                                  " \"" + device.name + "\". This device has no prior puppet objects.", 
                                  null);
            }
        } else {
            Logger.logWarning("Failed to bind previous puppet for input device " + deviceID + 
                              ". Could not find an input device at this index.",
                              null);
        }
        
        Logger.setDomain(null);
    }
    
}