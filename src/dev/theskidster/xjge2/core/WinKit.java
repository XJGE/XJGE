package dev.theskidster.xjge2.core;

import java.util.TreeMap;
import org.lwjgl.PointerBuffer;
import static org.lwjgl.glfw.GLFW.glfwGetMonitors;

/**
 * @author J Hoffman
 * Created: Apr 29, 2021
 */

public final class WinKit {

    private static final TreeMap<Integer, Monitor> monitors = new TreeMap<>();
    
    public static final TreeMap<Integer, Monitor> getConnectedMonitors() {
        PointerBuffer monitorBuf = glfwGetMonitors();
        
        if(monitorBuf != null) {
            for(int i = 0; i < monitorBuf.limit(); i++) {
                monitors.put(i + 1, new Monitor(i + 1, monitorBuf.get(i)));
            }
        } else {
            Logger.setDomain("winkit");
            Logger.logSevere("Failed to find any available monitors.", null);
        }
        
        return monitors;
    }
    
}