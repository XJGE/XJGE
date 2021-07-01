package dev.theskidster.xjge2.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * @author J Hoffman
 * Created: May 3, 2021
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
    
    InputDevice(int id, float sensitivity, float deadzone, HashMap<Control, Integer> config) {
        this.id          = id;
        this.sensitivity = sensitivity;
        this.deadzone    = deadzone;
        this.config      = config;
    }
    
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
    
    protected abstract void poll();
    
    protected void resolvePuppetSetRequest() {
        if(puppetSetEvents.size() > 0) {
            puppets.push(puppetSetEvents.poll());
        }
    }
    
}