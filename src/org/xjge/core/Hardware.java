package org.xjge.core;

import java.util.Collections;
import java.util.NavigableMap;
import java.util.TreeMap;
import org.lwjgl.PointerBuffer;
import static org.lwjgl.glfw.GLFW.glfwGetMonitors;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.openal.ALC11.*;
import static org.lwjgl.openal.ALUtil.getStringList;
import static org.lwjgl.system.MemoryUtil.NULL;

//Created: Apr 29, 2021

/**
 * Provides utilities to manage the state of various connected peripheral 
 * devices not directly related to user input.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Hardware {
    
    private static boolean vSyncEnabled = true;
    
    private static final TreeMap<Integer, Speaker> speakers = new TreeMap<>();
    private static final TreeMap<Integer, Monitor> monitors = new TreeMap<>();
    
    /**
     * Releases every connected audio device from the engines influence.
     */
    static void freeSpeakers() {
        speakers.forEach((id, device) -> alcCloseDevice(device.handle));
    }
    
    /**
     * Removes the specified display device from a collection of currently 
     * connected peripheral devices.
     * 
     * @param handle a unique value provided by GLFW used to identify the 
     *               device
     */
    static void removeMonitor(long handle) {
        monitors.values().removeIf(monitor -> monitor.handle == handle);
    }
    
    /**
     * Obtains a display device from a collection of currently connected 
     * peripheral devices.
     * 
     * @param handle a unique value provided by GLFW used to identify the 
     *               device
     * 
     * @return the monitor object whos handle corresponds to the one supplied
     */
    static Monitor getMonitor(long handle) {
        return monitors.values().stream()
                .filter(monitor -> monitor.handle == handle)
                .findFirst()
                .get();
    }
    
    /**
     * Obtains any available monitor currently connected and subject to the 
     * engines influence.
     * 
     * @return a monitor object that's available to the engine for immediate 
     *         use 
     */
    static Monitor getAnyMonitor() {
        return monitors.values().stream().findAny().get();
    }
    
    /**
     * Sets the value of vsync using data from the engine configuration file.
     * 
     * @param enabled if true, vsync will be enabled when the engine is 
     *                initialized
     */
    static void setVSyncPreference(boolean enabled) {
        vSyncEnabled = enabled;
    }
    
    /**
     * Finds every available audio device currently connected to the system and 
     * returns them in a collection.
     * 
     * @return an immutable collection of available audio devices
     */
    public static final NavigableMap<Integer, Speaker> findSpeakers() {
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
            Logger.setDomain("hardware");
            Logger.logWarning("Failed to find any available speakers", null);
            Logger.setDomain(null);
        }
        
        return Collections.unmodifiableNavigableMap(speakers);
    }
    
    /**
     * Finds every available display device currently connected to the system 
     * and returns them in a collection.
     * 
     * @return an immutable collection of available display devices
     */
    public static final NavigableMap<Integer, Monitor> findMonitors() {
        PointerBuffer monitorBuf = glfwGetMonitors();
        
        if(monitorBuf != null) {
            for(int i = 0; i < monitorBuf.limit(); i++) {
                if(!monitors.containsKey(i + 1)) {
                    monitors.put(i + 1, new Monitor(i + 1, monitorBuf.get(i)));
                }
            }
        } else {
            Logger.setDomain("hardware");
            Logger.logWarning("Failed to find any available monitors", null);
            Logger.setDomain(null);
        }
        
        return Collections.unmodifiableNavigableMap(monitors);
    }
    
    /**
     * Provides the amount of currently connected display devices.
     * 
     * @return the number of available display devices
     */
    public static int getNumMonitors() {
        return monitors.size();
    }
    
    /**
     * Provides the amount of currently connected audio devices.
     * 
     * @return the number of available audio devices
     */
    public static int getNumSpeakers() {
        return speakers.size();
    }
    
    /**
     * Obtains a value indicating whether or not vertical sync is enabled.
     * 
     * @return true if vertical sync is currently enabled
     */
    public static boolean getVSyncEnabled() {
        return vSyncEnabled;
    }
    
    /**
     * Obtains an object which represents the current monitor the window is 
     * using. This method is identical in function to 
     * {@link Window#getMonitor()}.
     * 
     * @return the current monitor object of the window
     */
    public static Monitor getCurrentMonitor() {
        return Window.monitor;
    }
    
    /**
     * Obtains the current {@link Speaker} the engine is using for audio output.
     * 
     * @return an object that represents a peripheral audio device
     */
    public static Speaker getCurrentSpeaker() {
        return Audio.speaker;
    }
    
    /**
     * Determines whether the application will take advantage of vertical sync 
     * (or VSync) while rendering frames. VSync is enabled by default on 
     * startup.
     * 
     * @param vSyncEnabled if true, vertical sync will be enabled
     */
    public static void setVSyncEnabled(boolean vSyncEnabled) {
        Hardware.vSyncEnabled = vSyncEnabled;
        glfwSwapInterval(vSyncEnabled ? 1 : 0);
    }
    
    /**
     * Sets the current audio device the engine will use. 
     * <p>
     * NOTE: Using this method will update the collection of audio devices 
     * available to the engine and change the current OpenAL context. This 
     * action will require all sounds and music previously loaded into memory 
     * to be reloaded which may cause brief but noticeable stutter while the 
     * audio data is being transferred.
     * 
     * @param operation the method of traversal to use. Either explicitly as 
     *                  the ID number of the device or "prev/next" to move 
     *                  between the previous and next devices in the collection 
     *                  respectively.
     */
    public static void setSpeaker(String operation) {
        Audio.saveContextConfig();
        
        findSpeakers();
        
        Logger.setDomain("hardware");
        try {
            if(!speakers.isEmpty()) {
                switch(operation) {
                    case "next" -> {
                        if(!speakers.ceilingKey(speakers.lastKey()).equals(Audio.speaker.id)) {
                            Audio.speaker = speakers.higherEntry(Audio.speaker.id).getValue();
                        } else {
                            Audio.speaker = speakers.firstEntry().getValue();
                        }
                        Audio.speaker.setContextCurrent();
                        Logger.logInfo(
                                "Set current audio device to " + Audio.speaker.id + " \"" +
                                Audio.speaker.name.substring(15) + "\"");
                    }

                    case "prev" -> {
                        if(!speakers.floorKey(speakers.firstKey()).equals(Audio.speaker.id)) {
                            Audio.speaker = speakers.lowerEntry(Audio.speaker.id).getValue();
                        } else {
                            Audio.speaker = speakers.lastEntry().getValue();
                        }
                        Audio.speaker.setContextCurrent();
                        Logger.logInfo(
                                "Set current audio device to " + Audio.speaker.id + " \"" +
                                Audio.speaker.name.substring(15) + "\"");
                    }

                    default -> {
                        try {
                            int index = Integer.parseInt(operation);

                            if(speakers.containsKey(index)) {
                                Audio.speaker = speakers.get(index);
                                Audio.speaker.setContextCurrent();
                                Logger.logInfo(
                                        "Set current audio device to " + Audio.speaker.id + " \"" +
                                                Audio.speaker.name.substring(15) + "\"");
                            } else {
                                Logger.logWarning(
                                        "Failed to set audio device. Could not find device at index " + index + ".", null);
                            }
                        } catch(NumberFormatException e) {
                            Logger.logWarning("Failed to set audio device. Invalid index value used", null);
                        }
                    }
                }
            }
        } catch(NullPointerException e) {
            Logger.logWarning("Failed to set audio device. No such device is available", e);
        }
        Logger.setDomain(null);
    }
    
}