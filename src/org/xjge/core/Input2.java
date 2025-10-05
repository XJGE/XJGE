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

    private static final Map<UUID, Controllable> controllables = new HashMap<>();
    private static final Map<Integer, InputDevice2> inputDevices = new HashMap<>();
    
    void update(double targetDelta, double trueDelta) {
        //Cache the current control state of input devices for this frame
        inputDevices.values().forEach(device -> device.poll());
        
        //Process the user-defined actions of controllable objects
        controllables.values().forEach(controllable -> controllable.process(targetDelta, trueDelta));
    }
    
}