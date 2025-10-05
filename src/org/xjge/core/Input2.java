package org.xjge.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class Input2 {

    private static final Map<UUID, Puppet2> puppets = new HashMap<>();
    private static final Map<Integer, InputDevice2> inputDevices = new HashMap<>();
    
    /**
     * Capture "snapshots" of the input state for each connected device
     */
    void pollInputDevices() {
        inputDevices.values().forEach(device -> device.poll());
    }
    
    void processPuppetActions(double targetDelta, double trueDelta) {
        puppets.values().forEach(puppet -> {
            
        });
    }
    
}