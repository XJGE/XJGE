package org.xjge.core;

import java.util.HashMap;
import java.util.Stack;

/**
 * Created: May 3, 2021
 * <p>
 * Represents a peripheral device that can capture input actions from a user.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
abstract class InputDevice {

    final int id;
    
    boolean enabled = true;
    
    protected String name;
    
    Stack<Boolean> enabledStates = new Stack<>();
    
    final HashMap<Control, Integer> controls;
    final HashMap<String, Float> settings;
    
    /**
     * Creates a new input device object and applies the users settings to its 
     * control configuration.
     * 
     * @param id the unique number used to identify the device in other parts of 
     *           the engine
     * @param controls a collection of various {@link Control} mappings
     * @param settings a collection containing additional user preferences
     */
    InputDevice(int id, HashMap<Control, Integer> controls, HashMap<String, Float> settings) {
        this.id       = id;
        this.controls = controls;
        this.settings = settings;
    }
    
    /**
     * Creates a new input device object from an existing one.
     * 
     * @param inputDevice the input device to copy
     */
    InputDevice(InputDevice inputDevice) {
        id            = inputDevice.id;
        enabled       = inputDevice.enabled;
        controls      = inputDevice.controls;
        enabledStates = inputDevice.enabledStates;
        settings      = inputDevice.settings;
    }
    
    /**
     * Processes the input actions of a device by looking for state changes in 
     * its interactive components.
     * 
     * @param targetDelta a constant value denoting the desired time (in 
     *                    seconds) it should take for one game tick to complete
     * @param trueDelta the actual time (in seconds) it took the current game
     *                  tick to complete
     */
    protected abstract void poll(double targetDelta, double trueDelta, Puppet puppet, Control control, Command command);
    
}