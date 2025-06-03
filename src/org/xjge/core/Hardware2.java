package org.xjge.core;

import java.util.Collections;
import java.util.NavigableMap;
import java.util.TreeMap;
import org.lwjgl.PointerBuffer;
import static org.lwjgl.glfw.GLFW.glfwGetMonitors;

/**
 * Created: Apr 29, 2021
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Hardware2 {
    
    private static final NavigableMap<Integer, Monitor> monitors = new TreeMap<>();
    
    static Monitor getMonitor(long handle) {
        if(monitors.isEmpty()) {
            Logger.logWarning("Unable to find a monitor with the handle " + handle, null);
            return null;
        } else {
            return monitors.values().stream().filter(monitor -> monitor.handle == handle).findFirst().get();
        }
    }
    
    static final Monitor getPrimaryMonitor() {
        return findMonitors().get(0);
    }
    
    public static final NavigableMap<Integer, Monitor> findMonitors() {
        monitors.clear();
        PointerBuffer monitorBuffer = glfwGetMonitors();
        
        if(monitorBuffer == null || monitorBuffer.limit() == 0) {
            Logger.logWarning("Failed to find any available monitors", null);
        } else {
            for(int i = 0; i < monitorBuffer.limit(); i++) {
                monitors.put(i, new Monitor(i, monitorBuffer.get(i)));
            }
        }
        
        return Collections.unmodifiableNavigableMap(monitors);
    }
    
}