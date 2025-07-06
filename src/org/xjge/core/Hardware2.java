package org.xjge.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    
    private static String cpuModel;
    
    private static final NavigableMap<Integer, Monitor> monitors = new TreeMap<>();
    
    static {
        ProcessBuilder builder = new ProcessBuilder(
            "powershell.exe",
            "-NoProfile",
            "-Command",
            "(Get-CimInstance Win32_Processor).Name"
        );
        
        builder.redirectErrorStream(true);
        
        try {
            Process process = builder.start();
            
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                
                while((line = reader.readLine()) != null) {
                    line = line.trim();
                    if(!line.isEmpty()) cpuModel = line.trim();
                }
            } finally {
                process.destroy();
            }
        } catch(IOException exception) {
            Logger.logWarning("Failed to parse CPU model", exception);
            cpuModel = "Unknown";
        }
    }
    
    static String getCPUModel() {
        return cpuModel;
    }
    
    static int getNumMonitors() {
        return monitors.size();
    }
    
    static final Monitor getMonitor(long handle) {
        try {
            return monitors.values().stream().filter(monitor -> monitor.handle == handle).findFirst().get();
        } catch(Exception exception) {
            Logger.logWarning("Unable to find a monitor with the handle " + handle, exception);
            return null;
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