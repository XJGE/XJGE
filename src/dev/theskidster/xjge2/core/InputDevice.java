package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.gameutils.Puppet;
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
    
    boolean enabled = true;
    
    protected String name;
    
    Stack<Boolean> enabledStates  = new Stack<>();
    Stack<Puppet> puppets         = new Stack<>();
    Queue<Puppet> puppetSetEvents = new LinkedList<>();
    
    final HashMap<Control, Integer> config;
    
    InputDevice(int id, float sensitivity, HashMap<Control, Integer> config) {
        this.id          = id;
        this.sensitivity = sensitivity;
        this.config      = config;
    }
    
    InputDevice(InputDevice inputDevice) {
        id              = inputDevice.id;
        sensitivity     = inputDevice.sensitivity;
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