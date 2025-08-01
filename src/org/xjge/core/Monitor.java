package org.xjge.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import static org.lwjgl.glfw.GLFW.glfwGetMonitorName;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetVideoModes;
import org.lwjgl.glfw.GLFWVidMode;

/**
 * Created: Apr 29, 2021
 * <p>
 * Represents a visual display device such as a computer monitor or television.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Monitor {
    
    final int index;
    
    final long handle;
    
    public final String name;
    
    private GLFWVidMode videoMode;
    private final TreeMap<String, GLFWVidMode> videoModes = new TreeMap<>();
    
    /**
     * Provides the information parsed from a visual display device as an 
     * object.
     * 
     * @param index the unique number used to identify the device in other parts 
     *              of the engine
     * @param handle a unique value provided by GLFW used to identify the  device
     */
    Monitor(int index, long handle) {
        this.index  = index;
        this.handle = handle;
        name        = glfwGetMonitorName(handle);
        videoMode   = glfwGetVideoMode(handle);
        
        GLFWVidMode.Buffer modeBuf = glfwGetVideoModes(handle);
        
        if(modeBuf != null) {
            modeBuf.forEach(mode -> {
                switch(getAspectRatio(mode)) {
                    case "4:3", "16:9", "85:48", "683:384" -> {
                        if(mode.refreshRate() <= 60 && mode.refreshRate() >= 29) {
                            if(Window.get4KRestricted()) {
                                if(mode.width() <= 1920) videoModes.put(getInfo(mode), mode);
                            } else {
                                videoModes.put(getInfo(mode), mode);
                            }
                        }
                    }
                }
            });
            
            //We'll supply the default video mode of the monitor in the off 
            //chance it doesn't support one of our preferred video modes
            
            if(!videoModes.containsKey(getInfo())) {
                videoModes.put(getInfo(), videoMode);
            }
        } else {
            Logger.logWarning("Unable to find any additional video " +
                              "modes for monitor " + index + " \"" + name + "\"", 
                              null);
        }
    }
    
    /**
     * Finds the greatest common divisor given two numbers. Used to calculate 
     * the aspect ratios of the monitors various video modes.
     * 
     * @param width the width of the video mode
     * @param height the height of the video mode
     * 
     * @return the greatest common divisor
     */
    private int findDivisor(int width, int height) {
        return (height == 0) ? width : findDivisor(height, width % height);
    }
    
    /**
     * Finds the aspect ratio of a video mode.
     * 
     * @param mode the video mode to parse information from
     * 
     * @return an aspect ratio represented as a string
     */
    private String getAspectRatio(GLFWVidMode mode) {
        int gcd = findDivisor(mode.width(), mode.height());
        return mode.width() / gcd + ":" + mode.height() / gcd;
    }
    
    /**
     * Obtains the aspect ratio of the monitors current video mode.
     * 
     * @return an aspect ratio represented as a string
     */
    public String getAspectRatio() {
        return getAspectRatio(videoMode);
    }
    
    /**
     * Generates a string that provides information about a video mode.
     * 
     * @param mode the video mode to parse information from
     * 
     * @return a human-readable string detailing the video modes resolution, 
     *         aspect ratio, and refresh rate
     */
    private String getInfo(GLFWVidMode mode) {
        return mode.width() + "x" + mode.height() + " " + getAspectRatio(mode) +
               " " + mode.refreshRate() + "hz";
    }
    
    /**
     * Generates a string that provides information about the monitors current 
     * video mode.
     * 
     * @return a human-readable string detailing the video modes resolution, 
     *         aspect ratio, and refresh rate
     */
    public String getInfo() {
        return getInfo(videoMode);
    }
    
    /**
     * Obtains the width of the monitors current video mode.
     * 
     * @return the width of the video mode in pixels
     */
    public int getWidth() {
        return videoMode.width();
    }
    
    /**
     * Obtains the height of the monitors current video mode.
     * 
     * @return the height of the video mode in pixels
     */
    public int getHeight() {
        return videoMode.height();
    }
    
    /**
     * Obtains the refresh rate of the monitors current video mode.
     * 
     * @return the refresh rate of the video mode in hertz
     */
    public int getRefreshRate() {
        return videoMode.refreshRate();
    }
    
    /**
     * Provides an immutable collection of all the video modes available to 
     * this monitor that are supported by the engine.
     * 
     * @return a collection of GLFW video mode objects
     */
    public Map<String, GLFWVidMode> getVideoModes() {
        return Collections.unmodifiableNavigableMap(videoModes);
    }
    
    /**
     * Sets the video mode the monitor will use.
     * 
     * @param operation the method of traversal to use. Either explicitly as 
     *                  the ID number of the monitor or "prev/next" to move 
     *                  between the previous and next video modes available to 
     *                  the monitor respectively.
     */
    public void setVideoMode(String operation) {
        switch(operation) {
            case "next" -> {
                if(!videoModes.ceilingKey(videoModes.lastKey()).equals(getInfo())) {
                    videoMode = videoModes.higherEntry(getInfo()).getValue();
                } else {
                    videoMode = videoModes.firstEntry().getValue();
                }
            } 
            
            case "prev" -> {
                if(!videoModes.floorKey(videoModes.firstKey()).equals(getInfo())) {
                    videoMode = videoModes.lowerEntry(getInfo()).getValue();
                } else {
                    videoMode = videoModes.lastEntry().getValue();
                }
            }
            
            default -> {
                try {
                    int modeIndex = Integer.parseInt(operation);
                    
                    var tempKeys   = new ArrayList<String>();
                    var tempValues = new ArrayList<GLFWVidMode>(); 
                    
                    videoModes.forEach((info, mode) -> {
                        tempKeys.add(info);
                        tempValues.add(mode);
                    });
                    
                    if(tempKeys.get(modeIndex) != null && tempValues.get(modeIndex) != null) {
                        videoMode = tempValues.get(modeIndex);
                    } else {
                        Logger.logWarning("Failed to change the current video mode of monitor " + 
                                          index + " \"" + name + "\". Could not find a video mode " + 
                                          "object at index " + index, 
                                          null);
                    }
                } catch(NumberFormatException | IndexOutOfBoundsException e) {
                    Logger.logWarning("Failed to change the current video mode for monitor " + 
                                      index + " \"" + name + "\". Invalid index used", 
                                      e);
                }
            }
        }
        
        Window.reconfigure();
    }
    
}