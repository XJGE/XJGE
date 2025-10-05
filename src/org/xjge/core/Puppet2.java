package org.xjge.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public final class Puppet2 {

    final UUID uuid = UUID.randomUUID();
    private InputDevice2 device;
    
    public final Map<Control, PuppetAction> actions = new HashMap<>();
    
    void process(double targetDelta, double trueDelta) {
        if(device != null && device.enabled && !actions.isEmpty()) {
            actions.forEach((control, action) -> action.perform(device, control, targetDelta, trueDelta));
        }
    }
    
}