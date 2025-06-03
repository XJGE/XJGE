package org.xjge.core;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.NavigableMap;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWImage;
import static org.lwjgl.stb.STBImage.STBI_rgb_alpha;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created: Apr 29, 2021
 * <br><br>
 * Provides a point of access which can be used to alter the properties of the 
 * game window at runtime.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Window {
    
    static final long HANDLE = glfwCreateWindow(640, 480, "XJGE v" + XJGE.VERSION, NULL, NULL);
    
    private static int xPos;
    private static int yPos;
    private static int width;
    private static int height;
    static int cursorMode = GLFW_CURSOR_NORMAL;
    
    private static boolean fullscreen;
    static boolean visible;
    
    private static String title;
    static Monitor monitor;
    
    /**
     * Adapts the window configuration to the video mode of the current 
     * monitor.
     */
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
        
        glfwSwapInterval(Hardware.getVSyncEnabled() ? 1 : 0);
    }
    
    /**
     * Uncovers the window and requests focus from the operating system.
     */
    static void show() {
        visible = true;
        
        glfwSetInputMode(HANDLE, GLFW_CURSOR, cursorMode);
        glfwShowWindow(HANDLE);
        glfwFocusWindow(HANDLE);
        
        glfwSetMonitorCallback((monHandle, event) -> {
            switch(event) {
                case GLFW_CONNECTED -> {
                    Hardware.findMonitors();
                    Monitor conMon = Hardware.getMonitor(monHandle);
                    Logger.logInfo("Monitor " + conMon.index + " \"" + conMon.name + "\" has been connected");
                }
                
                /*
                I think there's a driver issue here with some monitors which
                causes the window to disappear if it attempts to enter 
                fullscreen again after a monitor is disconnected.
                */
                case GLFW_DISCONNECTED -> {
                    if(monitor.handle == monHandle) {
                        Logger.logWarning("The current monitor (ID: " + monitor.index + ", \"" + 
                                          monitor.name + "\") has been disconnected. Attempting " + 
                                          "to move the window to the next available monitor...", 
                                          null);
                        
                        Hardware.removeMonitor(monHandle);
                        monitor = Hardware.getAnyMonitor();
                        setFullscreen(false);
                        
                        Logger.logInfo("Moved the window to monitor " + monitor.index + " \"" + monitor.name + "\"");
                    } else {
                        Monitor disconMon = Hardware.getMonitor(monHandle);
                        
                        Logger.logInfo("Monitor " + disconMon.index + " \"" + disconMon.name + "\" has been disconnected");
                        Hardware.removeMonitor(monHandle);
                    }
                }
            }
        });
        
        glfwSetWindowPosCallback(HANDLE, (window, x, y) -> {
            xPos = x;
            yPos = y;
        });
    }
    
    /**
     * Obtains the position of the window, in screen coordinates, of the 
     * upper-left corner of the content area of the window.
     * 
     * @return the position of the windows content area along the x-axis
     */
    public static int getPositionX() {
        return xPos;
    }
    
    /**
     * Obtains the position of the window, in screen coordinates, of the 
     * upper-left corner of the content area of the window.
     * 
     * @return the position of the windows content area along the y-axis
     */
    public static int getPositionY() {
        return yPos;
    }
    
    /**
     * Obtains the current width of the windows content area.
     * 
     * @return the width of the window in pixels
     */
    public static int getWidth() {
        return width;
    }
    
    /**
     * Obtains the current height of the windows content area.
     * 
     * @return the height of the window in pixels
     */
    public static int getHeight() {
        return height;
    }
    
    /**
     * Obtains the value indicating whether or not the window is currently in 
     * fullscreen mode.
     * 
     * @return if true, the window will cover the entire screen including 
     *         taskbars
     */
    public static boolean getFullscreen() {
        return fullscreen;
    }
    
    /**
     * Obtains the title used to identify the window. This is the same title 
     * that will be displayed to the user from the windows frame.
     * 
     * @return the title of the window as a string
     */
    public static String getTitle() {
        return title;
    }
    
    /**
     * Obtains the {@link Monitor} which the window is currently using.
     * 
     * @return the current monitor object of the window
     */
    public static Monitor getMonitor() {
        return monitor;
    }
    
    /**
     * Obtains the current value of the specified input option used by the game 
     * window.
     * 
     * @param mode the input option to manipulate. One of 
     *             {@link org.lwjgl.glfw.GLFW#GLFW_CURSOR GLFW_CURSOR}, 
     *             {@link org.lwjgl.glfw.GLFW#GLFW_STICKY_KEYS GLFW_STICKY_KEYS}, 
     *             {@link org.lwjgl.glfw.GLFW#GLFW_STICKY_MOUSE_BUTTONS GLFW_STICKY_MOUSE_BUTTONS}, 
     *             {@link org.lwjgl.glfw.GLFW#GLFW_LOCK_KEY_MODS GLFW_LOCK_KEY_MODS}, or
     *             {@link org.lwjgl.glfw.GLFW#GLFW_RAW_MOUSE_MOTION GLFW_RAW_MOUSE_MOTION}.
     * 
     * @return the current state of the queried mode
     */
    public int getInputMode(int mode) {
        return glfwGetInputMode(HANDLE, mode);
    }
    
    /**
     * Sets the position, in screen coordinates, of the upper-left corner of 
     * the content area of the window.
     * 
     * @param xPos the x-coordinate of the upper-left corner of the content area
     * @param yPos the y-coordinate of the upper-left corner of the content area
     */
    public static void setPosition(int xPos, int yPos) {
        Window.xPos = xPos;
        Window.yPos = yPos;
        
        glfwSetWindowPos(HANDLE, xPos, yPos);
    }
    
    /**
     * Attempts to place the window in the center of the monitor.
     */
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
    
    /**
     * Updates the values of the windows width and height fields without 
     * alerting GLFW.
     * 
     * @param width the new width of the window in pixels
     * @param height the new height of the window in pixels
     */
    static void updateDimensions(int width, int height) {
        Window.width  = width;
        Window.height = height;
    }
    
    /**
     * Sets the value of the windows fullscreen field using data from the 
     * engines configuration file without alerting GLFW.
     * 
     * @param fullscreen if true, the window will cover the entire screen 
     *                   including taskbars
     */
    static void setFullscreenPreference(boolean fullscreen) {
        Window.fullscreen = fullscreen;
    }
    
    /**
     * Sets the size of the windows content area in pixels.
     * 
     * @param width the desired width of the window
     * @param height the desired height of the window
     */
    public static void setDimensions(int width, int height) {
        updateDimensions(width, height);
        glfwSetWindowSize(HANDLE, width, height);
    }
    
    /**
     * Changes the game window between fullscreen and windowed modes and 
     * reconfigures it to better fit the monitor.
     * 
     * @param fullscreen if true, the window will cover the entire screen 
     *                   including taskbars
     */
    public static void setFullscreen(boolean fullscreen) {
        Window.fullscreen = fullscreen;
        reconfigure();
    }
    
    /**
     * Changes the title used to identify the window. This is the same title 
     * that will be displayed to the user from the windows frame.
     * 
     * @param title the new title the window will use
     */
    public static void setTitle(String title) {
        Window.title = title;
        glfwSetWindowTitle(HANDLE, title);
    }
    
    /**
     * Changes the current monitor the window will use. Alternate version of 
     * {@link setMonitor(String)}.
     * 
     * @param monitor the monitor that the window will be switched to
     */
    public static void setMonitor(Monitor monitor) {
        setFullscreen(!fullscreen);

        Window.monitor = monitor;
        reconfigure();

        setFullscreen(!fullscreen);
    }
    
    /**
     * Changes the current monitor the window will use. Often this will cause 
     * the window to relocate.
     * 
     * @param operation the method of traversal to use. Either explicitly as 
     *                  the ID number of the device or "prev/next" to move 
     *                  between the previous and next devices in the collection 
     *                  respectively.
     */
    public static void setMonitor(String operation) {
        setFullscreen(!fullscreen);
        
        try {
            Thread.sleep(100);
        } catch(InterruptedException e) {
            Logger.logError(e.getMessage(), e);
        }
        
        NavigableMap<Integer, Monitor> monitors = Hardware.findMonitors();
        Monitor newMonitor = null;
        
        if(!monitors.isEmpty()) {
            switch(operation) {
                case "next" -> {
                    newMonitor = (!monitors.ceilingKey(monitors.lastKey()).equals(monitor.index))
                               ? monitors.higherEntry(monitor.index).getValue()
                               : monitors.firstEntry().getValue();
                }
                
                case "prev" -> {
                    newMonitor = (!monitors.floorKey(monitors.firstKey()).equals(monitor.index))
                               ? monitors.lowerEntry(monitor.index).getValue()
                               : monitors.lastEntry().getValue();
                }
                
                default -> {
                    try {
                        int index = Integer.parseInt(operation);
                        
                        if(monitors.containsKey(index)) {
                            newMonitor = monitors.get(index);
                        } else {
                            Logger.logWarning("Failed to change the windows current monitor. Could " + 
                                              "not find a monitor object at index " + index, 
                                              null);
                        }
                    } catch(NumberFormatException e) {
                        Logger.logWarning("Failed to change the windows current " + 
                                          "monitor. Invalid index used", 
                                          e);
                    }
                }
            }
            
            if(newMonitor != null) setMonitor(newMonitor);
        } else {
            Logger.logWarning("Failed to change windows current monitor." + 
                              " No monitors are currently connected", 
                              null);
        }
        
        setFullscreen(!fullscreen);
    }
    
    /**
     * Sets the icon image of the window. Images should be at least 32x32 
     * pixels large, but no larger than 64x64.
     * 
     * @param filename the name of the file to load. Expects the file extension 
     *                 to be included.
     */
    public static void setIcon(String filename) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            InputStream file = Window.class.getResourceAsStream(XJGE.getAssetsFilepath() + filename);
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
            Logger.logWarning("Failed to set window icon using \"" + filename + 
                              "\". Check the filename, path, or extension", 
                              e);
        }
    }
    
    /**
     * Sets the value of an input option used by the game window.
     * 
     * @param mode the input option to manipulate. One of 
     *              {@link org.lwjgl.glfw.GLFW#GLFW_CURSOR GLFW_CURSOR}, 
     *              {@link org.lwjgl.glfw.GLFW#GLFW_STICKY_KEYS GLFW_STICKY_KEYS}, 
     *              {@link org.lwjgl.glfw.GLFW#GLFW_STICKY_MOUSE_BUTTONS GLFW_STICKY_MOUSE_BUTTONS}, 
     *              {@link org.lwjgl.glfw.GLFW#GLFW_LOCK_KEY_MODS GLFW_LOCK_KEY_MODS}, or
     *              {@link org.lwjgl.glfw.GLFW#GLFW_RAW_MOUSE_MOTION GLFW_RAW_MOUSE_MOTION}.
     * @param value the new value to set the specified input option to 
     */
    public static void setInputMode(int mode, int value) {
        if(mode == GLFW_CURSOR) cursorMode = value;
        glfwSetInputMode(HANDLE, mode, value);
    }
    
    /**
     * Gracefully ceases execution and closes the window.
     */
    public static void close() {
        glfwSetWindowShouldClose(HANDLE, true);
    }
    
}