package org.xjge.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public final class Controllable {
    
    private int deviceID = Input2.NO_DEVICE;
    
    final UUID uuid = UUID.randomUUID();
    public final String name;
    
    public final Map<Control, ControllableAction> actions = new HashMap<>();
    
    public Controllable(String name) {
        this.name = name;
    }
    
    public void setInputDevice(int deviceID) {
        if(deviceID == Input2.NO_DEVICE) {
            this.deviceID = deviceID;
        } else {
            if(Input2.getDeviceConnectionStatus(deviceID)) {
                this.deviceID = deviceID;
                Input2.addControllable(this);
            } else {
                Logger.logWarning("Failed to set the input device of controllable object \"" + name + 
                                  "\". Could not locate an input device at index " + deviceID, null);
            }
        }
    }
    
    public int getInputDeviceID() {
        return deviceID;
    }
    
}