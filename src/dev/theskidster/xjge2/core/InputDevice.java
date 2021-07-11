package dev.theskidster.xjge2.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * @author J Hoffman
 * Created: May 3, 2021
 */

/**
 * Represents a peripheral device that can capture input actions from a user.
 */
abstract class InputDevice {

    final int id;
    
    float sensitivity;
    float deadzone;
    
    boolean enabled = true;
    
    protected String name;
    
    Stack<Boolean> enabledStates  = new Stack<>();
    Stack<Puppet> puppets         = new Stack<>();
    Queue<Puppet> puppetSetEvents = new LinkedList<>();
    
    final HashMap<Control, Integer> config;
    
    /**
     * Creates a new input device object and applies the users settings to its control configuration.
     * 
     * @param id          the unique number used to identify the device in other parts of the engine
     * @param sensitivity a value used by gameplay systems to adjust the responsiveness of input actions based off the users preference
     * @param deadzone    a value used to indicate how much pressure must be applied to an analog stick before its input is recognized
     * @param config      a collection of various {@link Control} mappings
     */
    InputDevice(int id, float sensitivity, float deadzone, HashMap<Control, Integer> config) {
        this.id          = id;
        this.sensitivity = sensitivity;
        this.deadzone    = deadzone;
        this.config      = config;
    }
    
    /**
     * Creates a new input device object from an existing one.
     * 
     * @param inputDevice the input device to copy
     */
    InputDevice(InputDevice inputDevice) {
        id              = inputDevice.id;
        sensitivity     = inputDevice.sensitivity;
        deadzone        = inputDevice.deadzone;
        enabled         = inputDevice.enabled;
        enabledStates   = inputDevice.enabledStates;
        puppets         = inputDevice.puppets;
        puppetSetEvents = inputDevice.puppetSetEvents;
        config          = inputDevice.config;
    }
    
    /**
     * Processes the input actions of a device by looking for state changes in its interactive components. 
     */
    protected abstract void poll();
    
    /**
     * Resolves a request to update the current {@link Puppet} object an input device is using.
     */
    protected void resolvePuppetSetRequest() {
        if(puppetSetEvents.size() > 0) {
            puppets.push(puppetSetEvents.poll());
        }
    }
    
}