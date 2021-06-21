package dev.theskidster.xjge2.core;

import java.util.TreeMap;
import org.lwjgl.PointerBuffer;
import static org.lwjgl.glfw.GLFW.glfwGetMonitors;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.openal.ALC11.*;
import static org.lwjgl.openal.ALUtil.getStringList;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author J Hoffman
 * Created: Apr 29, 2021
 */

public final class Hardware {
    
    private static boolean vSyncEnabled = true;
    
    private static final TreeMap<Integer, Speaker> speakers = new TreeMap<>();
    private static final TreeMap<Integer, Monitor> monitors = new TreeMap<>();
    
    static void freeSpeakers() {
        speakers.forEach((id, device) -> alcCloseDevice(device.handle));
    }
    
    static void removeMonitor(long handle) {
        monitors.values().removeIf(monitor -> monitor.handle == handle);
    }
    
    static Monitor getMonitor(long handle) {
        return monitors.values().stream()
                .filter(monitor -> monitor.handle == handle)
                .findFirst()
                .get();
    }
    
    static Monitor getAnyMonitor() {
        return monitors.values().stream().findAny().get();
    }
    
    public static final TreeMap<Integer, Speaker> findSpeakers() {
        speakers.forEach((id, device) -> alcCloseDevice(device.handle));
        speakers.clear();
        
        var deviceList = getStringList(NULL, ALC_ALL_DEVICES_SPECIFIER);
        
        if(deviceList != null) {
            for(int i = 0; i < deviceList.size(); i++) {
                if(!speakers.containsKey(i + 1)) {
                    speakers.put(i + 1, new Speaker(i + 1, deviceList.get(i)));
                }
            }
        } else {
            Logger.setDomain("core");
            Logger.logSevere("Failed to find any available speakers.", null);
        }
        
        return speakers;
    }
    
    public static final TreeMap<Integer, Monitor> findMonitors() {
        PointerBuffer monitorBuf = glfwGetMonitors();
        
        if(monitorBuf != null) {
            for(int i = 0; i < monitorBuf.limit(); i++) {
                if(!monitors.containsKey(i + 1)) {
                    monitors.put(i + 1, new Monitor(i + 1, monitorBuf.get(i)));
                }
            }
        } else {
            Logger.setDomain("core");
            Logger.logSevere("Failed to find any available monitors.", null);
        }
        
        return monitors;
    }
    
    public static int getNumMonitors() {
        return monitors.size();
    }
    
    public static int getNumSpeakers() {
        return speakers.size();
    }
    
    public static boolean getVSyncEnabled() {
        return vSyncEnabled;
    }
    
    public static void setVSyncEnabled(boolean vSyncEnabled) {
        Hardware.vSyncEnabled = vSyncEnabled;
        glfwSwapInterval(vSyncEnabled ? 1 : 0);
        
        Logger.setDomain("winkit");
        Logger.logInfo("Toggled VSync (" + vSyncEnabled + ")");
        Logger.setDomain(null);
    }
    
}