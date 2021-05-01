package dev.theskidster.xjge2.core;

import java.util.ArrayList;
import java.util.TreeMap;
import static org.lwjgl.glfw.GLFW.glfwGetMonitorName;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetVideoModes;
import org.lwjgl.glfw.GLFWVidMode;

/**
 * @author J Hoffman
 * Created: Apr 29, 2021
 */

public final class Monitor {
    
    private final int id;
    
    public final long handle;
    
    public final String name;
    
    private GLFWVidMode videoMode;
    private final TreeMap<String, GLFWVidMode> videoModes = new TreeMap<>();
    
    Monitor(int id, long handle) {
        this.id     = id;
        this.handle = handle;
        name        = glfwGetMonitorName(handle);
        videoMode   = glfwGetVideoMode(handle);
        
        GLFWVidMode.Buffer modeBuf = glfwGetVideoModes(handle);
        
        if(modeBuf != null) {
            modeBuf.forEach(mode -> {
                switch(getAspectRatio(mode)) {
                    case "4:3", "16:9", "85:48", "683:384" -> {
                        if(mode.refreshRate() <= 60 && mode.refreshRate() >= 29) {
                            videoModes.put(getInfo(mode), mode);
                        }
                    }
                }
            });
            
            //We'll supply the default video mode of the monitor in the off 
            //chance it doesn't support one of our preferred video modes.
            
            if(!videoModes.containsKey(getInfo())) {
                videoModes.put(getInfo(), videoMode);
            }
        } else {
            Logger.setDomain("winkit");
            Logger.logWarning("Unable to find any additional video " +
                              "modes for monitor: \"" + name + "\"", 
                              null);
            Logger.setDomain(null);
        }
    }
    
    private int findDivisor(int width, int height) {
        return (height == 0) ? width : findDivisor(height, width % height);
    }
    
    private String getAspectRatio(GLFWVidMode mode) {
        int gcd = findDivisor(mode.width(), mode.height());
        return mode.width() / gcd + ":" + mode.height() / gcd;
    }
    
    public String getAspectRatio() {
        return getAspectRatio(videoMode);
    }
    
    private String getInfo(GLFWVidMode mode) {
        return mode.width() + "x" + mode.height() + " " + getAspectRatio(mode) +
               " " + mode.refreshRate() + "hz";
    }
    
    public String getInfo() {
        return getInfo(videoMode);
    }
    
    public int getWidth() {
        return videoMode.width();
    }
    
    public int getHeight() {
        return videoMode.height();
    }
    
    public int getRefreshRate() {
        return videoMode.refreshRate();
    }
    
    public TreeMap<String, GLFWVidMode> getVideoModes() {
        return videoModes;
    }
    
    public void setVideoMode(String operation) {
        switch(operation) {
            case "next" -> {
                if(!videoModes.ceilingKey(videoModes.lastKey()).equals(getInfo())) {
                    videoMode = videoModes.higherEntry(getInfo()).getValue();
                } else {
                    videoMode = videoModes.firstEntry().getValue();
                }
                
                Logger.setDomain("winkit");
                Logger.logInfo("Changed video mode of monitor \"" + name +
                               "\" (ID:" + id + ") to (" + getInfo() + ").");
                Logger.setDomain(null);
            } 
            
            case "prev" -> {
                if(!videoModes.floorKey(videoModes.firstKey()).equals(getInfo())) {
                    videoMode = videoModes.lowerEntry(getInfo()).getValue();
                } else {
                    videoMode = videoModes.lastEntry().getValue();
                }
                
                Logger.setDomain("winkit");
                Logger.logInfo("Changed video mode of monitor \"" + name +
                               "\" (ID:" + id + ") to (" + getInfo() + ").");
                Logger.setDomain(null);
            }
            
            default -> {
                try {
                    int index = Integer.parseInt(operation);
                    
                    var tempKeys   = new ArrayList<String>();
                    var tempValues = new ArrayList<GLFWVidMode>(); 
                    
                    videoModes.forEach((info, mode) -> {
                        tempKeys.add(info);
                        tempValues.add(mode);
                    });
                    
                    if(tempKeys.get(index) != null && tempValues.get(index) != null) {
                        videoMode = tempValues.get(index);
                        
                        Logger.setDomain("winkit");
                        Logger.logInfo("Changed video mode of monitor \"" + name +
                                       "\" (ID:" + id + ") to (" + getInfo() + ").");
                        Logger.setDomain(null);
                    } else {
                        Logger.setDomain("winkit");
                        Logger.logWarning("Failed to change video mode of monitor \"" + name + "\" (ID:" + 
                                          id + "). Could not find video mode at index " + index + ".", 
                                           null);
                        Logger.setDomain(null);
                    }
                } catch(NumberFormatException | IndexOutOfBoundsException e) {
                    Logger.logWarning("Failed to set video mode for monitor \"" + name + "", e);
                }
            }
        }
        
        //TODO: update window and viewports. Refer to legacy project.
    }
    
}