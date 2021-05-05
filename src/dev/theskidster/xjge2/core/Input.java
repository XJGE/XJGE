package dev.theskidster.xjge2.core;

import static dev.theskidster.xjge2.core.Control.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @author J Hoffman
 * Created: May 3, 2021
 */

public final class Input {

    public static final int KEY_MOUSE_COMBO = -1;
    
    static final HashMap<Integer, InputDevice> inputDevices = new HashMap<>();
    
    private static final HashMap<Integer, HashMap<Control, Integer>> controlConfigs = new HashMap<>();
    private static final HashMap<Integer, Float> sensitivityConfigs;
    
    static {
        sensitivityConfigs = new HashMap<>() {{
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
        inputDevices.put(KEY_MOUSE_COMBO, new KeyMouseCombo(KEY_MOUSE_COMBO, 0.15f, keyMouseConfig));
        
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
    }
    
    static void findInputDevices() {
        for(int i = 0; i < GLFW_JOYSTICK_5; i++) {
            if(inputDevices.containsKey(i)) {
                if(glfwJoystickPresent(i)) {
                    inputDevices.put(i, new Gamepad((Gamepad) inputDevices.get(i)));
                }
            } else {
                if(glfwJoystickPresent(i)) {
                    inputDevices.put(i, new Gamepad(i, sensitivityConfigs.get(i), controlConfigs.get(i)));
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
    
    static void importControls() {
        try {
            InputStream stream = new FileInputStream(XJGE.PWD.toString() + "\\controls.cfg");
            XMLStreamReader xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
            
            int id = 0;
            
            while(xmlReader.hasNext()) {
                final int ID = id;
                
                switch(xmlReader.next()) {
                    case XMLStreamConstants.START_ELEMENT -> {
                        if(xmlReader.getName().getLocalPart().equals("device")) {
                            id = Integer.parseInt(xmlReader.getAttributeValue(null, "id"));
                            sensitivityConfigs.put(id, Float.parseFloat(xmlReader.getAttributeValue(null, "sensitivity")));
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
    
    static void exportControls() {
        try {
            FileWriter file = new FileWriter("controls.cfg");
            
            try(PrintWriter output = new PrintWriter(file)) {
                output.println("<config>");
                
                controlConfigs.forEach((deviceID, mapping) -> {
                    float sensitivity = 0.15f;
                    
                    if(inputDevices.containsKey(deviceID)) {
                        sensitivity = inputDevices.get(deviceID).sensitivity;
                    }
                    
                    output.append("\t<device id=\"")
                            .append(deviceID + "\" ")
                            .append("sensitivity=\"")
                            .append(sensitivity + "\">")
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
    
    public static int getNumDevices() {
        return inputDevices.size();
    }
    
    public static boolean getDevicePresent(int id) {
        return false;
    }
    
    public static float getDeviceSensitivity(int id) {
        return 0;
    }
    
    public static boolean getDeviceEnabled(int id) {
        return false;
    }
    
    public static HashMap<Control, Integer> getDeviceControls(int id) {
        return null;
    }
    
    public static int[] getKeyMouseAxisValues() {
        return null;
    }
    
    public static Puppet getInputDevicePuppet(int id) {
        return null;
    }
    
    public static void setDeviceSensitivity(int id, float sensitivity) {
        
    }
    
    public static void setDeviceEnabled(int id, boolean enabled) {
        
    }
    
    public static void setDeviceControls(int id, HashMap<Control, Integer> config) {
        if(inputDevices.containsKey(id)) {
            inputDevices.get(id).config.putAll(config);
        } else {
            //todo log
        }
    }
    
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