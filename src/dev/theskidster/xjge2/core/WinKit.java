package dev.theskidster.xjge2.core;

import java.util.TreeMap;
import org.lwjgl.PointerBuffer;
import static org.lwjgl.glfw.GLFW.glfwGetMonitors;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;

/**
 * @author J Hoffman
 * Created: Apr 29, 2021
 */

public final class WinKit {

    private static boolean vSyncEnabled = true;
    
    private static final TreeMap<Integer, Monitor> monitors = new TreeMap<>();
    
    public static final TreeMap<Integer, Monitor> getConnectedMonitors() {
        monitors.clear();
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
    
    public static boolean getVSyncEnabled() {
        return vSyncEnabled;
    }
    
    public static void setVSyncEnabled(boolean vSyncEnabled) {
        WinKit.vSyncEnabled = vSyncEnabled;
        glfwSwapInterval(vSyncEnabled ? 1 : 0);
        
        Logger.setDomain("winkit");
        Logger.logInfo("Toggled VSync (" + vSyncEnabled + ")");
        Logger.setDomain(null);
    }
    
}