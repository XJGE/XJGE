package dev.theskidster.xjge2.core;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * @author J Hoffman
 * Created: May 3, 2021
 */

abstract class InputDevice {

    final int id;
    
    float sensitivity = 0.15f;
    
    boolean enabled = true;
    
    protected String name;
    
    Stack<Boolean> enabledStates  = new Stack<>();
    Stack<Puppet> puppets         = new Stack<>();
    Queue<Puppet> puppetSetEvents = new LinkedList<>();
    
    InputDevice(int id) {
        this.id = id;
    }
    
    InputDevice(InputDevice inputDevice) {
        id              = inputDevice.id;
        sensitivity     = inputDevice.sensitivity;
        enabled         = inputDevice.enabled;
        enabledStates   = inputDevice.enabledStates;
        puppets         = inputDevice.puppets;
        puppetSetEvents = inputDevice.puppetSetEvents;
    }
    
    protected abstract void poll();
    
    protected abstract void importControlConfig(String filename);
    
    protected abstract void exportControlConfig();
    
    protected void resolvePuppetSetRequest() {
        if(puppetSetEvents.size() > 0) {
            puppets.push(puppetSetEvents.poll());
        }
    }
    
}