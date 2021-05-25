package dev.theskidster.xjge2.core;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.TreeMap;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWImage;
import static org.lwjgl.stb.STBImage.STBI_rgb_alpha;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author J Hoffman
 * Created: Apr 29, 2021
 */

public final class Window {
    
    //TODO: this is only temporarily public for testing purposes.
    public static final long HANDLE = glfwCreateWindow(640, 480, "XJGE 2 (v" + XJGE.VERSION + ")", NULL, NULL);
    
    private static int xPos;
    private static int yPos;
    private static int width;
    private static int height;
    
    private static boolean fullscreen;
    
    private static String title;
    static Monitor monitor;
    
    static void reconfigure() {
        if(fullscreen) {
            width  = monitor.getWidth();
            height = monitor.getHeight();
            
            glfwSetWindowMonitor(
                    HANDLE, monitor.handle,
                    xPos, yPos,
                    monitor.getWidth(), monitor.getHeight(),
                    monitor.getRefreshRate());
        } else {
            width  = (int) Math.round(monitor.getWidth() * 0.6f);
            height = (int) Math.round(monitor.getHeight() * 0.6f);
            
            glfwSetWindowMonitor(
                    HANDLE, NULL, 
                    xPos, yPos,
                    width, height,
                    monitor.getRefreshRate());
        }
        
        setPositionCentered();
        
        glfwSwapInterval(WinKit.getVSyncEnabled() ? 1 : 0);
    }
    
    static void show() {
        glfwSetInputMode(HANDLE, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        glfwShowWindow(HANDLE);
        glfwFocusWindow(HANDLE);
        
        glfwSetMonitorCallback((monHandle, event) -> {
            Logger.setDomain("winkit");
            
            switch(event) {
                case GLFW_CONNECTED -> {
                    WinKit.findMonitors();
                    Monitor conMon = WinKit.getMonitor(monHandle);
                    Logger.logInfo("Monitor " + conMon.id + " \"" + conMon.name + "\" has been connected.");
                }
                
                /*
                I think there's a driver issue here with some monitors which
                causes the window to disappear if it attempts to enter 
                fullscreen again after a monitor is disconnected.
                */
                case GLFW_DISCONNECTED -> {
                    if(monitor.handle == monHandle) {
                        Logger.logWarning("The windows current monitor (ID:" + monitor.id + ", \"" + 
                                          monitor.name + "\") has been disconnected. Attempting " + 
                                          "to move the window to the next available monitor...", 
                                          null);
                        
                        WinKit.removeMonitor(monHandle);
                        monitor = WinKit.getAnyMonitor();
                        enableFullscreen(false);
                        
                        Logger.logInfo("Moved the window to monitor " + monitor.id + " \"" + monitor.name + "\"");
                    } else {
                        Monitor disconMon = WinKit.getMonitor(monHandle);
                        
                        Logger.logInfo("Monitor " + disconMon.id + " \"" + disconMon.name + "\" has been disconnected.");
                        WinKit.removeMonitor(monHandle);
                    }
                }
            }
            
            Logger.setDomain(null);
        });
    }
    
    public static int getPositionX() {
        return xPos;
    }
    
    public static int getPositionY() {
        return yPos;
    }
    
    public static int getWidth() {
        return width;
    }
    
    public static int getHeight() {
        return height;
    }
    
    public static boolean getFullscreen() {
        return fullscreen;
    }
    
    public static String getTitle() {
        return title;
    }
    
    public static Monitor getMonitor() {
        return monitor;
    }
    
    public static void setPosition(int xPos, int yPos) {
        Window.xPos = xPos;
        Window.yPos = yPos;
        
        glfwSetWindowPos(HANDLE, xPos, yPos);
    }
    
    public static void setPositionCentered() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer xPosBuf = stack.mallocInt(1);
            IntBuffer yPosBuf = stack.mallocInt(1);
            
            glfwGetMonitorPos(monitor.handle, xPosBuf, yPosBuf);
            
            xPos = Math.round((monitor.getWidth() - width) / 2) + xPosBuf.get();
            yPos = Math.round((monitor.getHeight() - height) / 2) + yPosBuf.get();
        
            glfwSetWindowPos(HANDLE, xPos, yPos);
        }
    }
    
    public static void setDimensions(int width, int height) {
        Window.width  = width;
        Window.height = height;
        
        glfwSetWindowSize(HANDLE, width, height);
    }
    
    public static void setFullscreen(boolean fullscreen) {
        Logger.setDomain("winkit");
        Logger.logInfo("Toggled fullscreen (" + fullscreen + ")");
        Logger.setDomain(null);
        
        enableFullscreen(fullscreen);
    }
    
    static void enableFullscreen(boolean fullscreen) {
        Window.fullscreen = fullscreen;
        reconfigure();
    }
            
    public static void setTitle(String title) {
        Window.title = title;
        glfwSetWindowTitle(HANDLE, title);
    }
    
    public static void setMonitor(Monitor monitor) {
        enableFullscreen(!fullscreen);

        Window.monitor = monitor;
        reconfigure();

        Logger.setDomain("winkit");
        Logger.logInfo("Moved the window to monitor " + monitor.id + " \"" + monitor.name + "\"");
        Logger.setDomain(null);

        enableFullscreen(!fullscreen);
    }
    
    public static void setMonitor(String operation) {
        enableFullscreen(!fullscreen);
        Logger.setDomain("winkit");
        
        try {
            Thread.sleep(100);
        } catch(InterruptedException e) {
            Logger.logSevere(e.getMessage(), e);
        }
        
        TreeMap<Integer, Monitor> monitors = WinKit.findMonitors();
        Monitor newMonitor = null;
        
        if(monitors.size() > 0) {
            switch(operation) {
                case "next" -> {
                    newMonitor = (!monitors.ceilingKey(monitors.lastKey()).equals(monitor.id))
                               ? monitors.higherEntry(monitor.id).getValue()
                               : monitors.firstEntry().getValue();
                }
                
                case "prev" -> {
                    newMonitor = (!monitors.floorKey(monitors.firstKey()).equals(monitor.id))
                               ? monitors.lowerEntry(monitor.id).getValue()
                               : monitors.lastEntry().getValue();
                }
                
                default -> {
                    try {
                        int index = Integer.parseInt(operation);
                        
                        if(monitors.containsKey(index)) {
                            newMonitor = monitors.get(index);
                        } else {
                            Logger.logWarning("Failed to change the windows current monitor. Could " + 
                                              "not find a monitor object at index " + index + ".", 
                                              null);
                        }
                    } catch(NumberFormatException e) {
                        Logger.logWarning("Failed to change the windows current " + 
                                          "monitor. Invalid index used.", 
                                          e);
                    }
                }
            }
            
            if(newMonitor != null) {
                setMonitor(newMonitor);
                //TODO: reset viewports
            }
        } else {
            Logger.logWarning("Failed to change windows current monitor." + 
                              " No monitors are currently connected.", 
                              null);
        }
        
        Logger.setDomain(null);
        enableFullscreen(!fullscreen);
    }
    
    public static void setIcon(String filename) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            InputStream file = Window.class.getResourceAsStream(XJGE.getFilepath() + filename);
            byte[] data      = file.readAllBytes();
            
            IntBuffer widthBuf   = stack.mallocInt(1);
            IntBuffer heightBuf  = stack.mallocInt(1);
            IntBuffer channelBuf = stack.mallocInt(1);
            
            ByteBuffer icon = stbi_load_from_memory(
                    stack.malloc(data.length).put(data).flip(),
                    widthBuf,
                    heightBuf,
                    channelBuf,
                    STBI_rgb_alpha);
            
            glfwSetWindowIcon(HANDLE, GLFWImage.mallocStack(1, stack)
                    .width(widthBuf.get())
                    .height(heightBuf.get())
                    .pixels(icon));
            
            stbi_image_free(icon);
            
        } catch(Exception e) {
            Logger.setDomain("winkit");
            Logger.logWarning("Failed to set window icon using \"" + filename + 
                              "\". Check the files name, path, or extension.", 
                              e);
            Logger.setDomain(null);
        }
    }
    
    public static void close() {
        glfwSetWindowShouldClose(HANDLE, true);
    }
    
}