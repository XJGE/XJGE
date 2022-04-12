package org.xjge.core;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import static org.xjge.core.Control.*;

//Created: Apr 12, 2022

/**
 * A virtual input device that can be used to facilitate the actions of an 
 * AI on an existing {@link Puppet} object. This works by overriding the input
 * values of a theoretical input device with the 
 * {@link Input#setVirtualGamepadInput(int, org.xjge.core.Control, float) setVirtualGamepadInput()}
 * method in the {@link Input} class.
 * 
 * @author J Hoffman
 * @since  2.1.6
 */
class VirtualGamepad extends InputDevice {

    final Map<Control, Float> inputValues;
    
    /**
     * Creates a new virtual gamepad object. These objects are used by the 
     * engine to facilitate the actions of an AI controlled puppet object.
     * 
     * @param id the unique number used to identify the device in other parts 
     *           of the engine
     */
    VirtualGamepad(int id) {
        super(id, new HashMap<>(), new HashMap<>());
        name    = "Virtual Gamepad " + (Math.abs(id) - 1);
        enabled = false;
        
        inputValues = new EnumMap<>(Control.class) {{
            put(CROSS,         0f);
            put(CIRCLE,        0f);
            put(SQUARE,        0f);
            put(TRIANGLE,      0f);
            put(L1,            0f);
            put(R1,            0f);
            put(SHARE,         0f);
            put(OPTIONS,       0f);
            put(PS_BUTTON,     0f);
            put(L3,            0f);
            put(R3,            0f);
            put(DPAD_UP,       0f);
            put(DPAD_RIGHT,    0f);
            put(DPAD_DOWN,     0f);
            put(DPAD_LEFT,     0f);
            put(LEFT_STICK_X,  0f);
            put(LEFT_STICK_Y,  0f);
            put(RIGHT_STICK_X, 0f);
            put(RIGHT_STICK_Y, 0f);
            put(L2,            0f);
            put(R2,            0f);
        }};
    }

    @Override
    protected void poll() {
        if(!puppets.empty() && puppets.peek() != null) {
            puppets.peek().commands.forEach((control, command) -> {
                command.execute(inputValues.get(control), this, control);
            });
        }
        
        resolvePuppetSetRequest();
    }
    
}