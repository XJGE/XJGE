package org.xjge.core;

import static org.xjge.core.Control.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Created: May 3, 2021
 * <p>
 * Provides a single point of access through which the state of peripheral 
 * input devices may be managed.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Input {

    /**
     * Used to remove an input device from a puppet object or signify that none
     * is currently set.
     */
    public static final int NO_DEVICE = -1;
    
    public static final int AI_GAMEPAD_1  = 17;
    public static final int AI_GAMEPAD_2  = 18;
    public static final int AI_GAMEPAD_3  = 19;
    public static final int AI_GAMEPAD_4  = 20;
    public static final int AI_GAMEPAD_5  = 21;
    public static final int AI_GAMEPAD_6  = 22;
    public static final int AI_GAMEPAD_7  = 23;
    public static final int AI_GAMEPAD_8  = 24;
    public static final int AI_GAMEPAD_9  = 25;
    public static final int AI_GAMEPAD_10 = 26;
    public static final int AI_GAMEPAD_11 = 27;
    public static final int AI_GAMEPAD_12 = 28;
    public static final int AI_GAMEPAD_13 = 29;
    public static final int AI_GAMEPAD_14 = 30;
    public static final int AI_GAMEPAD_15 = 31;
    public static final int AI_GAMEPAD_16 = 32;
    
    /**
     * Special case value associated with the {@link KeyMouseCombo} input device.
     */
    public static final int KEY_MOUSE_COMBO = 16;
    
    private static final boolean connected[] = new boolean[4];
    
    private static final HashMap<Integer, InputDevice> inputDevices = new HashMap<>();
    
    private static final Queue<Puppet> puppetAddEvents   = new LinkedList<>();
    private static final HashMap<String, Puppet> puppets = new HashMap<>();
    
    private static final HashMap<Integer, HashMap<Control, Integer>> controlConfigs = new HashMap<>();
    private static final HashMap<Integer, HashMap<String, Float>> settingConfigs    = new HashMap<>();
    
    static final Map<Integer, Key> keyChars;
    
    static {
        var keys = new HashMap<Integer, Key>() {{
            put(GLFW_KEY_SPACE,      new Key(' ', ' '));
            put(GLFW_KEY_APOSTROPHE, new Key('\'', '\"'));
            put(GLFW_KEY_COMMA,      new Key(',', '<'));
            put(GLFW_KEY_MINUS,      new Key('-', '_'));
            put(GLFW_KEY_PERIOD,     new Key('.', '>'));
            put(GLFW_KEY_SLASH,      new Key('/', '?'));
            put(GLFW_KEY_0, new Key('0', ')'));
            put(GLFW_KEY_1, new Key('1', '!'));
            put(GLFW_KEY_2, new Key('2', '@'));
            put(GLFW_KEY_3, new Key('3', '#'));
            put(GLFW_KEY_4, new Key('4', '$'));
            put(GLFW_KEY_5, new Key('5', '%'));
            put(GLFW_KEY_6, new Key('6', '^'));
            put(GLFW_KEY_7, new Key('7', '&'));
            put(GLFW_KEY_8, new Key('8', '*'));
            put(GLFW_KEY_9, new Key('9', '('));
            put(GLFW_KEY_SEMICOLON, new Key(';', ':'));
            put(GLFW_KEY_EQUAL,     new Key('=', '+'));
            put(GLFW_KEY_A, new Key('a', 'A'));
            put(GLFW_KEY_B, new Key('b', 'B'));
            put(GLFW_KEY_C, new Key('c', 'C'));
            put(GLFW_KEY_D, new Key('d', 'D'));
            put(GLFW_KEY_E, new Key('e', 'E'));
            put(GLFW_KEY_F, new Key('f', 'F'));
            put(GLFW_KEY_G, new Key('g', 'G'));
            put(GLFW_KEY_H, new Key('h', 'H'));
            put(GLFW_KEY_I, new Key('i', 'I'));
            put(GLFW_KEY_J, new Key('j', 'J'));
            put(GLFW_KEY_K, new Key('k', 'K'));
            put(GLFW_KEY_L, new Key('l', 'L'));
            put(GLFW_KEY_M, new Key('m', 'M'));
            put(GLFW_KEY_N, new Key('n', 'N'));
            put(GLFW_KEY_O, new Key('o', 'O'));
            put(GLFW_KEY_P, new Key('p', 'P'));
            put(GLFW_KEY_Q, new Key('q', 'Q'));
            put(GLFW_KEY_R, new Key('r', 'R'));
            put(GLFW_KEY_S, new Key('s', 'S'));
            put(GLFW_KEY_T, new Key('t', 'T'));
            put(GLFW_KEY_U, new Key('u', 'U'));
            put(GLFW_KEY_V, new Key('v', 'V'));
            put(GLFW_KEY_W, new Key('w', 'W'));
            put(GLFW_KEY_X, new Key('x', 'X'));
            put(GLFW_KEY_Y, new Key('y', 'Y'));
            put(GLFW_KEY_Z, new Key('z', 'Z'));
            put(GLFW_KEY_LEFT_BRACKET,  new Key('[', '{'));
            put(GLFW_KEY_BACKSLASH,     new Key('\\', '|'));
            put(GLFW_KEY_RIGHT_BRACKET, new Key(']', '}'));
            put(GLFW_KEY_GRAVE_ACCENT,  new Key('`', '~'));
        }};
        
        keyChars = Collections.unmodifiableMap(keys);
        
        for(int i = 0; i < 17; i++) {
            switch(i) {
                case GLFW_JOYSTICK_1, GLFW_JOYSTICK_2, GLFW_JOYSTICK_3, GLFW_JOYSTICK_4, KEY_MOUSE_COMBO -> {
                    HashMap<String, Float> settings = new HashMap<>(){{
                        put("leftDeadzone", 0.15f);
                        put("rightDeadzone", 0.15f);
                    }};

                    settingConfigs.put(i, settings);
                    
                    if(i != KEY_MOUSE_COMBO) {
                        //TODO: reimplement this
                        //XJGE.observable.properties.put("GAMEPAD_" + i + "_CONNECTED", connected[i]);
                    }
                }
            }
        }
        
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
        inputDevices.put(KEY_MOUSE_COMBO, new KeyMouseCombo(KEY_MOUSE_COMBO, keyMouseConfig, settingConfigs.get(KEY_MOUSE_COMBO)));
        setKeyMouseAxisValues(GLFW_KEY_A, GLFW_KEY_D, GLFW_KEY_W, GLFW_KEY_S);
        
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
        
        for(int i = 17; i <= AI_GAMEPAD_16; i++) {
            inputDevices.put(i, new VirtualGamepad(i));
        }
        
        glfwSetJoystickCallback((jid, event) -> {
            switch(event) {
                case GLFW_CONNECTED -> {
                    findInputDevices();
                    
                    if(jid < GLFW_JOYSTICK_5) {
                        connected[jid] = true;
                        //TODO: 
                        //XJGE.observable.notifyObservers("GAMEPAD_" + jid + "_CONNECTED", connected[jid]);
                    }
                    
                    Logger.logInfo("Input device \"" + getDeviceName(jid) + "\" " +
                                   "connected at position " + jid);
                }
                
                case GLFW_DISCONNECTED -> {
                    findInputDevices();
                    
                    if(jid < GLFW_JOYSTICK_5) {
                        connected[jid] = false;
                        //TODO: 
                        //XJGE.observable.notifyObservers("GAMEPAD_" + jid + "_CONNECTED", connected[jid]);
                    }
                    
                    Logger.logInfo("Input device \"" + getDeviceName(jid) + "\" " +
                                   "disconnected at position " + jid);
                }
            }
        });
    }
    
    /**
     * Processes the input actions of every input device connected to the system 
     * that isn't currently in a disabled state and updates the internally 
     * maintained list of usable puppet objects.
     *
     * @param targetDelta a constant value denoting the desired time (in 
     *                    seconds) it should take for one game tick to complete
     * @param trueDelta the actual time (in seconds) it took the current game
     *                  tick to complete
     */
    static void update(double targetDelta, double trueDelta) {
        while(!puppetAddEvents.isEmpty()) {
            Puppet puppet = puppetAddEvents.poll();
            puppets.put(puppet.name, puppet);
        }
        
        puppets.forEach((name, puppet) -> {
            puppet.processInput(targetDelta, trueDelta);
        });
        
        puppets.values().removeIf(puppet -> puppet.removalRequested());
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
                    inputDevices.put(i, new Gamepad(i, controlConfigs.get(i), settingConfigs.get(i)));
                    if(i < GLFW_JOYSTICK_5) connected[i] = true;
                }
            }
        }
        
        inputDevices.get(KEY_MOUSE_COMBO).controls.putAll(controlConfigs.get(KEY_MOUSE_COMBO));
        inputDevices.get(KEY_MOUSE_COMBO).settings.putAll(settingConfigs.get(KEY_MOUSE_COMBO));
    }
    
    /**
     * Imports and applies user input preferences from a configuration file 
     * generated by the engine.
     */
    static void importControls() {
        try {
            InputStream stream = new FileInputStream(XJGE.PRESENT_WORKING_DIRECTORY.toString() + "/controls.cfg");
            XMLStreamReader xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
            
            int id = 0;
            
            while(xmlReader.hasNext()) {
                final int ID = id;
                final HashMap<String, Float> settings = new HashMap<>();
                
                switch(xmlReader.next()) {
                    case XMLStreamConstants.START_ELEMENT -> {
                        if(xmlReader.getName().getLocalPart().equals("device")) {
                            
                            for(int a = 0; a < xmlReader.getAttributeCount(); a++) {
                                String attribName = xmlReader.getAttributeLocalName(a);
                                
                                if(attribName.equals("id")) {
                                    id = Integer.parseInt(xmlReader.getAttributeValue(null, "id"));
                                } else {
                                    settings.put(attribName, Float.valueOf(xmlReader.getAttributeValue(null, attribName)));
                                }
                            }
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
            Logger.logWarning("Failed to import user-defined controls", e);
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
                    output.append("\t<device id=\"")
                          .append(deviceID + "\" ")
                          .append("leftDeadzone=\"")
                          .append(settingConfigs.get(deviceID).get("leftDeadzone") + "\" ")
                          .append("rightDeadzone=\"")
                          .append(settingConfigs.get(deviceID).get("rightDeadzone") + "\"");
                    
                    if(inputDevices.containsKey(deviceID)) {
                        InputDevice device = inputDevices.get(deviceID);
                        
                        device.settings.forEach((name, value) -> {
                            if(!name.equals("leftDeadzone") && !name.equals("rightDeadzone")) {
                                output.append(" " + name + "=\"")
                                      .append(value + "\"");
                            }
                        });
                    }
                    
                    output.append(">")
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
            Logger.logWarning("Failed to export user defined controls", e);
        }
    }
    
    /**
     * Adds the specified puppet object to a collection maintained internally by
     * the engine. Once puppet objects have been added, they will begin polling
     * input from whichever input device has been assigned to it.
     * 
     * @param puppet the puppet object to add
     */
    static void addPuppet(Puppet puppet) {
        puppetAddEvents.add(puppet);
    }
    
    /**
     * Used internally by the engine to revert the enabled state of the 
     * keyboard and mouse.
     */
    static void revertKeyboardEnabledState() {
        InputDevice keyboard = inputDevices.get(KEY_MOUSE_COMBO);
        
        try {
            keyboard.enabledStates.pop();
            keyboard.enabled = keyboard.enabledStates.peek();
        } catch(EmptyStackException e) {
            keyboard.enabled = true;
        }
    }
    
    /**
     * Obtains an input device at the specified index.
     * 
     * @param deviceID the number used to identify the input device. One of: 
     * <table><caption></caption>
     * <tr><td>{@link NO_DEVICE}</td>
     * <td>{@link KEY_MOUSE_COMBO}</td></tr>
     * <tr><td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_1}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_2}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_3 GLFW_JOYSTICK_3}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_4 GLFW_JOYSTICK_4}</td></tr>
     * <tr><td>{@link AI_GAMEPAD_1}</td><td>{@link AI_GAMEPAD_2}</td>
     * <td>{@link AI_GAMEPAD_3}</td><td>{@link AI_GAMEPAD_4}</td></tr>
     * <tr><td>{@link AI_GAMEPAD_5}</td><td>{@link AI_GAMEPAD_6}</td>
     * <td>{@link AI_GAMEPAD_7}</td><td>{@link AI_GAMEPAD_8}</td></tr>
     * <tr><td>{@link AI_GAMEPAD_9}</td><td>{@link AI_GAMEPAD_10}</td>
     * <td>{@link AI_GAMEPAD_11}</td><td>{@link AI_GAMEPAD_12}</td></tr>
     * <tr><td>{@link AI_GAMEPAD_13}</td><td>{@link AI_GAMEPAD_14}</td>
     * <td>{@link AI_GAMEPAD_15}</td><td>{@link AI_GAMEPAD_16}</td></tr></table>
     * 
     * @return the object representing some peripheral input device
     */
    static InputDevice getDevice(int deviceID) {
        return inputDevices.get(deviceID);
    } 
    
    /**
     * Obtains the amount of input devices that are currently connected to the 
     * system. The value provided by this method includes only peripheral 
     * devices such as {@linkplain Gamepad gamepads}.
     * 
     * @return the number of connected input devices
     */
    public static int getNumDevices() {
        return inputDevices.size() - 17;
    }
    
    /**
     * Checks whether or not an input device is currently connected to the system.
     * <p>
     * NOTE: The keyboard/mouse along with virtual AI controlled gamepads
     * are always connected by default.
     * 
     * @param deviceID the number used to identify the input device. One of: 
     * <table><caption></caption>
     * <tr><td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK_1}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_2}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_3 GLFW_JOYSTICK_3}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_4 GLFW_JOYSTICK_4}</td></tr>
     * </table>
     * 
     * @return true if the input device is connected
     */
    public static boolean getDevicePresent(int deviceID) {
        if(inputDevices.containsKey(deviceID)) {
            return switch(deviceID) {
                case GLFW_JOYSTICK_1, GLFW_JOYSTICK_2, 
                     GLFW_JOYSTICK_3, GLFW_JOYSTICK_4 -> connected[deviceID];
                default -> true;
            };
        } else {
            return false;
        }
    }
    
    /**
     * Obtains the value of some setting that an input device can use to adjust 
     * the responsiveness of input actions during gameplay.
     * <p>
     * It's recommended that gameplay systems instead use 
     * {@link Command#getDeviceSetting(String)} and reserve this method for 
     * instances where the current value of the setting in question needs to be
     * exposed to the user- such as an interface that allows the user to edit 
     * their devices preferences.
     * <p>
     * NOTE: By default the engine provides deadzone settings for both the left
     * and right analog sticks of each input device. These settings determine 
     * how much a stick will need to be moved before its input is recognized. 
     * The values of these settings can be queried with "leftDeadzone" and 
     * "rightDeadzone" respectively.
     * 
     * @param deviceID the number used to identify the input device. One of:  
     * <table><caption></caption>
     * <tr><td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK_1}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_2}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_3 GLFW_JOYSTICK_3}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_4 GLFW_JOYSTICK_4}</td></tr>
     * <tr><td>{@link KEY_MOUSE_COMBO}</td></tr></table>
     * @param name the name of the setting to parse a value from
     * 
     * @return the value of the setting or {@code NaN} if the device and/or 
     *         setting could not be found
     */
    public static float getDeviceSetting(int deviceID, String name) {
        if(inputDevices.containsKey(deviceID)) {
            if(inputDevices.get(deviceID).settings.containsKey(name)) {
                return inputDevices.get(deviceID).settings.get(name);
            } else {
                return Float.NaN;
            }
        } else {
            return Float.NaN;
        }
    }
    
    /**
     * Checks whether or not an input device is currently allowing the engine 
     * to process its input actions.
     * 
     * @param deviceID the number used to identify the input device. One of:  
     * <table><caption></caption>
     * <tr><td>{@link NO_DEVICE}</td><td>{@link KEY_MOUSE_COMBO}</td></tr>
     * <tr><td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_1}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_2}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_3 GLFW_JOYSTICK_3}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_4 GLFW_JOYSTICK_4}</td></tr>
     * <tr><td>{@link AI_GAMEPAD_1}</td><td>{@link AI_GAMEPAD_2}</td>
     * <td>{@link AI_GAMEPAD_3}</td><td>{@link AI_GAMEPAD_4}</td></tr>
     * <tr><td>{@link AI_GAMEPAD_5}</td><td>{@link AI_GAMEPAD_6}</td>
     * <td>{@link AI_GAMEPAD_7}</td><td>{@link AI_GAMEPAD_8}</td></tr>
     * <tr><td>{@link AI_GAMEPAD_9}</td><td>{@link AI_GAMEPAD_10}</td>
     * <td>{@link AI_GAMEPAD_11}</td><td>{@link AI_GAMEPAD_12}</td></tr>
     * <tr><td>{@link AI_GAMEPAD_13}</td><td>{@link AI_GAMEPAD_14}</td>
     * <td>{@link AI_GAMEPAD_15}</td><td>{@link AI_GAMEPAD_16}</td></tr></table>
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
     * <p>
     * NOTE: Device names are not guaranteed to be unique and typically only 
     * reflect the model and/or manufacturer. This information can often help 
     * better identify a device, but shouldn't be used to refer to it.
     * 
     * @param deviceID the number used to identify the input device. One of: 
     * <table><caption></caption>
     * <tr><td>{@link Input#NO_DEVICE NO_DEVICE}</td>
     * <td>{@link Input#KEY_MOUSE_COMBO KEY_MOUSE_COMBO}</td></tr>
     * <tr><td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_1}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_2}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_3 GLFW_JOYSTICK_3}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_4 GLFW_JOYSTICK_4}</td></tr>
     * <tr><td>{@link AI_GAMEPAD_1}</td><td>{@link AI_GAMEPAD_2}</td>
     * <td>{@link AI_GAMEPAD_3}</td><td>{@link AI_GAMEPAD_4}</td></tr>
     * <tr><td>{@link AI_GAMEPAD_5}</td><td>{@link AI_GAMEPAD_6}</td>
     * <td>{@link AI_GAMEPAD_7}</td><td>{@link AI_GAMEPAD_8}</td></tr>
     * <tr><td>{@link AI_GAMEPAD_9}</td><td>{@link AI_GAMEPAD_10}</td>
     * <td>{@link AI_GAMEPAD_11}</td><td>{@link AI_GAMEPAD_12}</td></tr>
     * <tr><td>{@link AI_GAMEPAD_13}</td><td>{@link AI_GAMEPAD_14}</td>
     * <td>{@link AI_GAMEPAD_15}</td><td>{@link AI_GAMEPAD_16}</td></tr></table>
     * 
     * @return a human-readable description of the input device
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
     * <p>
     * Control configurations are user-defined input preferences that will be 
     * retained between runtime sessions. These configurations dictate how 
     * {@linkplain Control controls} on an input device will be mapped to 
     * actions in game.
     * 
     * @param deviceID the number used to identify the input device. One of:  
     * <table><caption></caption>
     * <tr><td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK_1}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_2}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_3 GLFW_JOYSTICK_3}</td></tr>
     * <tr><td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_4 GLFW_JOYSTICK_4}</td> 
     * <td>{@link KEY_MOUSE_COMBO}</td></tr></table>
     * 
     * @return an immutable collection containing the current control 
     *         configuration of an input device
     * 
     * @see setDeviceControls(int, Map)
     */
    public static Map<Control, Integer> getDeviceControls(int deviceID) {
        if(inputDevices.containsKey(deviceID) && (deviceID > NO_DEVICE && deviceID < GLFW_JOYSTICK_5 || deviceID == KEY_MOUSE_COMBO)) {
            return Collections.unmodifiableMap(controlConfigs.get(deviceID));
        } else {
            return null;
        }
    }
    
    /**
     * Obtains an array of values denoting which GLFW keys will be used by the 
     * {@link KeyMouseCombo} input device to mimic the action of an analog stick.
     * 
     * @return an array containing the axis values corresponding to various 
     *         GLFW keyboard keys
     * 
     * @see setKeyMouseAxisValues(int, int, int, int)
     */
    public static int[] getKeyMouseAxisValues() {
        return ((KeyMouseCombo) inputDevices.get(KEY_MOUSE_COMBO)).axisValues;
    }
    
    /**
     * Obtains the letter/symbol of a keyboard key.
     * <p>
     * NOTE: This method assumes that the keyboard producing the input is an 
     * unmodified english model. As such, the letter/symbol of the key in 
     * question must correspond to an ASCII character between the ranges of 
     * 32-127, any characters outside of this range will return <b>null</b>.
     * 
     * @param key the value supplied by GLFW of a single key on the keyboard
     * @param mods a value supplied by GLFW denoting whether any mod keys 
     *             where held (such as shift or control)
     * 
     * @return a character corresponding to the pressed key or <b>null</b> if
     *         one cannot be found
     */
    public static Character getKeyChar(int key, int mods) {
        if(keyChars.containsKey(key)) {
            return keyChars.get(key).getChar((mods == GLFW_MOD_SHIFT));
        } else {
            return null;
        }
    }
    
    /**
     * Changes the setting value of the specified input device.
     * <p>
     * This method can also be used to provide additional settings the 
     * implementation may require from input devices. To achieve this, simply 
     * pass the value obtained from {@link getDeviceSetting} to the 
     * {@code value} argument of this method and it will be retained between 
     * runtime sessions.
     * <p>
     * For example, you might do something like this:
     * <blockquote><pre>
     * XJGE.init()...
     * 
     * float prevValue = Input.getDeviceSetting(KEY_MOUSE_COMBO, "settingName");
     * Input.setDeviceSetting(KEY_MOUSE_COMBO, "settingName", prevValue);
     * 
     * XJGE.start()...
     * </pre></blockquote>
     * 
     * @param deviceID the number used to identify the input device. One of: 
     * <table><caption></caption>
     * <tr><td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK_1}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_2}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_3 GLFW_JOYSTICK_3}</td></tr>
     * <tr><td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_4 GLFW_JOYSTICK_4}</td> 
     * <td>{@link KEY_MOUSE_COMBO}</td></tr></table>
     * @param name  the name of the setting that will be changed
     * @param value the new value to change the setting to
     * 
     * @see getDeviceSetting(int, String)
     */
    public static void setDeviceSetting(int deviceID, String name, float value) {
        if(deviceID > AI_GAMEPAD_1) {
            if(inputDevices.containsKey(deviceID)) {
                inputDevices.get(deviceID).settings.put(name, value);
                settingConfigs.get(deviceID).put(name, value);
            } else {
                Logger.logWarning("Failed to add extra configuration \"" + name + "\". Could " + 
                                  "not find an input device at index " + deviceID,
                                  null);
            }
        } else {
            Logger.logWarning("Failed to add extra configuration \"" + name + "\". AI " + 
                              "controlled gamepads do not exhibit individualized user settings", 
                              null);
        }
    }
    
    /**
     * Sets the current enabled state of an input device.
     * 
     * @param deviceID the number used to identify the input device. One of: 
     * <table><caption></caption>
     * <tr><td>{@link Input#NO_DEVICE NO_DEVICE}</td>
     * <td>{@link Input#KEY_MOUSE_COMBO KEY_MOUSE_COMBO}</td></tr>
     * <tr><td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_1}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_2}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_3 GLFW_JOYSTICK_3}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_4 GLFW_JOYSTICK_4}</td></tr>
     * <tr><td>{@link AI_GAMEPAD_1}</td><td>{@link AI_GAMEPAD_2}</td>
     * <td>{@link AI_GAMEPAD_3}</td><td>{@link AI_GAMEPAD_4}</td></tr>
     * <tr><td>{@link AI_GAMEPAD_5}</td><td>{@link AI_GAMEPAD_6}</td>
     * <td>{@link AI_GAMEPAD_7}</td><td>{@link AI_GAMEPAD_8}</td></tr>
     * <tr><td>{@link AI_GAMEPAD_9}</td><td>{@link AI_GAMEPAD_10}</td>
     * <td>{@link AI_GAMEPAD_11}</td><td>{@link AI_GAMEPAD_12}</td></tr>
     * <tr><td>{@link AI_GAMEPAD_13}</td><td>{@link AI_GAMEPAD_14}</td>
     * <td>{@link AI_GAMEPAD_15}</td><td>{@link AI_GAMEPAD_16}</td></tr></table>
     * @param enabled if true, the device will allow the engine to process its 
     *                input actions
     * 
     * @see getDeviceEnabled(int)
     */
    public static void setDeviceEnabled(int deviceID, boolean enabled) {
        if(inputDevices.containsKey(deviceID)) {
            InputDevice device = inputDevices.get(deviceID);
            
            device.enabledStates.add(enabled);
            device.enabled = device.enabledStates.peek();
        } else {
            Logger.logWarning("Failed to change the enabled state of input device " + 
                              deviceID + ". Could not find an input device at this index",
                              null);
        }
    }
    
    /**
     * Ceases capturing input from all currently connected input devices except 
     * the one specified.
     * 
     * @param deviceID the number used to identify the input device that will
     *                 remain active. One of: 
     * <table><caption></caption>
     * <tr><td>{@link Input#NO_DEVICE NO_DEVICE}</td>
     * <td>{@link Input#KEY_MOUSE_COMBO KEY_MOUSE_COMBO}</td></tr>
     * <tr><td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_1}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_2}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_3 GLFW_JOYSTICK_3}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_4 GLFW_JOYSTICK_4}</td></tr>
     * <tr><td>{@link AI_GAMEPAD_1}</td><td>{@link AI_GAMEPAD_2}</td>
     * <td>{@link AI_GAMEPAD_3}</td><td>{@link AI_GAMEPAD_4}</td></tr>
     * <tr><td>{@link AI_GAMEPAD_5}</td><td>{@link AI_GAMEPAD_6}</td>
     * <td>{@link AI_GAMEPAD_7}</td><td>{@link AI_GAMEPAD_8}</td></tr>
     * <tr><td>{@link AI_GAMEPAD_9}</td><td>{@link AI_GAMEPAD_10}</td>
     * <td>{@link AI_GAMEPAD_11}</td><td>{@link AI_GAMEPAD_12}</td></tr>
     * <tr><td>{@link AI_GAMEPAD_13}</td><td>{@link AI_GAMEPAD_14}</td>
     * <td>{@link AI_GAMEPAD_15}</td><td>{@link AI_GAMEPAD_16}</td></tr></table>
     * @param includeAIGamepads if true, AI controlled input devices will also
     *                          be disabled
     */
    public static void disableAllExcept(int deviceID, boolean includeAIGamepads) {
        inputDevices.forEach((id, device) -> {
            if(includeAIGamepads) {
                if(device instanceof Gamepad || device instanceof VirtualGamepad) {
                    device.enabled = id == deviceID;
                }
            } else {
                if(device instanceof Gamepad) {
                    device.enabled = id == deviceID;
                }
            }
        });
    }
    
    /**
     * Sets the current control configuration of an input device.
     * <p>
     * NOTE: When setting the configuration of controls 
     * {@link Control#LEFT_STICK_X LEFT_STICK_X} and 
     * {@link Control#LEFT_STICK_Y LEFT_STICK_Y} for the {@link KeyMouseCombo} 
     * input device, a bitwise OR (symbolized as |) should be used to pass two 
     * values representing the GLFW keyboard keys that will be used to move the 
     * analog stick along a single axis.
     * <p>
     * For example, you might do something like this:
     * <blockquote><pre>
     * var keyMouseConfig = new HashMap() {{
     *     ...
     *     put(LEFT_STICK_X,  GLFW_KEY_A | GLFW_KEY_D);
     *     put(LEFT_STICK_Y,  GLFW_KEY_W | GLFW_KEY_S);
     *     ...
     * }};
     * </pre></blockquote>
     * 
     * @param deviceID the number used to identify the input device. One of:  
     * <table><caption></caption>
     * <tr><td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK_1}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_2}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_3 GLFW_JOYSTICK_3}</td></tr>
     * <tr><td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_4 GLFW_JOYSTICK_4}</td> 
     * <td>{@link KEY_MOUSE_COMBO}</td></tr></table>
     * @param config the collection containing the new control configuration to 
     *               apply
     * 
     * @see getDeviceControls(int)
     */
    public static void setDeviceControls(int deviceID, Map<Control, Integer> config) {
        if(deviceID > AI_GAMEPAD_1) {
            if(inputDevices.containsKey(deviceID)) {
                controlConfigs.get(deviceID).putAll(config);

                Logger.logInfo("Changed the button configuration of input device " + 
                               deviceID + " \"" + inputDevices.get(deviceID).name + "\"");
            } else {
                Logger.logWarning("Failed to change the button configuration of input device " + 
                                  deviceID + ". Could not find an input device at this index",
                                  null);
            }
        } else {
            Logger.logWarning("Failed to change the button configuration of input device " + 
                              deviceID + ". AI controlled gamepads do not exhibit " + 
                              "control preferences", 
                              null);
        }
    }
    
    /**
     * Sets the GLFW keys the keyboard will use to mimic the action of an 
     * analog stick. Typically you'd use this anytime the player wishes to use
     * a key combination other than WASD to move (in first person for example).
     * 
     * @param x1 the GLFW key used to move left along the x-axis
     * @param x2 the GLFW key used to move right along the x-axis
     * @param y1 the GLFW key used to move down along the y-axis
     * @param y2 the GLFW key that to move up along the y-axis
     * 
     * @see getKeyMouseAxisValues()
     */
    public static void setKeyMouseAxisValues(int x1, int x2, int y1, int y2) {
        ((KeyMouseCombo) inputDevices.get(KEY_MOUSE_COMBO)).axisValues[0] = x1;
        ((KeyMouseCombo) inputDevices.get(KEY_MOUSE_COMBO)).axisValues[1] = x2;
        ((KeyMouseCombo) inputDevices.get(KEY_MOUSE_COMBO)).axisValues[2] = y1;
        ((KeyMouseCombo) inputDevices.get(KEY_MOUSE_COMBO)).axisValues[3] = y2;
    }
    
    /**
     * Changes the input value of a virtual gamepads control component. Use 
     * this if you'd like to hook up an AI to an existing puppet object.
     * 
     * @param deviceID the number used to identify the input device. One of: 
     * <table><caption></caption>
     * <tr><td>{@link AI_GAMEPAD_1}</td><td>{@link AI_GAMEPAD_2}</td>
     * <td>{@link AI_GAMEPAD_3}</td><td>{@link AI_GAMEPAD_4}</td></tr>
     * <tr><td>{@link AI_GAMEPAD_5}</td><td>{@link AI_GAMEPAD_6}</td>
     * <td>{@link AI_GAMEPAD_7}</td><td>{@link AI_GAMEPAD_8}</td></tr>
     * <tr><td>{@link AI_GAMEPAD_9}</td><td>{@link AI_GAMEPAD_10}</td>
     * <td>{@link AI_GAMEPAD_11}</td><td>{@link AI_GAMEPAD_12}</td></tr>
     * <tr><td>{@link AI_GAMEPAD_13}</td><td>{@link AI_GAMEPAD_14}</td>
     * <td>{@link AI_GAMEPAD_15}</td><td>{@link AI_GAMEPAD_16}</td></tr></table>
     * @param control    a component such as a button whos input value will 
     *                   be changed
     * @param inputValue the input value denoting to what extent the control 
     *                   was manipulated or zero if not at all
     */
    public static void setVirtualGamepadInput(int deviceID, Control control, float inputValue) {
        if(deviceID >= AI_GAMEPAD_1 && deviceID <= AI_GAMEPAD_16) {
            ((VirtualGamepad) inputDevices.get(deviceID)).inputValues.put(control, inputValue);
        }
    }
    
}