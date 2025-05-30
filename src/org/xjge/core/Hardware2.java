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
    
    public static final NavigableMap<Integer, Monitor> findMonitors() {
        PointerBuffer monitorBuffer = glfwGetMonitors();
        
        if(monitorBuffer == null || monitorBuffer.limit() == 0) {
            Logger.logWarning("Failed to find any available monitors", null);
            monitors.clear();
        } else {
            for(int i = 0; i < monitorBuffer.limit(); i++) {
                if(!monitors.containsKey(i + 1)) {
                    monitors.put(i + 1, new Monitor(i + 1, monitorBuffer.get(i)));
                }
            }
        }
        
        return Collections.unmodifiableNavigableMap(monitors);
    }
    
    public static final Monitor getPrimaryMonitor() {
        return findMonitors().get(1);
    }
    
}