package dev.theskidster.xjge2.core;

import java.nio.IntBuffer;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author J Hoffman
 * Created: Apr 29, 2021
 */

public final class Window {
    
    /*
    The windows default settings are as follows;
        - Centered on the screen.
        - 60% the size of the monitor its on.
        - Not resizable.
        - Will have "XJGE 2 (v1.0.0)" as its title.
        - Will have the nullchicken as its icon.
        - Will be located on the "primary" monitor.
    
    the Window class is available statically.
        
    //WINDOW GETTERS (static)
        Window.getTitle();              String
        Window.getMonitor();            Monitor
        Window.getWidth();              int
        Window.getHeight();             int
        Window.getPositionX();          int
        Window.getPositionY();          int
        Window.getResizable();          boolean
        Window.getFullscreenEnabled();  boolean

    //WINDOW SETTERS (static)
        Window.setTitle(String title);
        Window.setMonitor(Monitor monitor);
        Window.setDimensions(int width, int height);
        Window.setWidth(int width);
        Window.setHeight(int height);
        Window.setPosition(int xPos, int yPos);
        Window.setPositionCentered();
        Window.setPositionX(int xPos);
        Window.setPositionY(int yPos);
        Window.setResizable(boolean resizable);
        Window.setFullScreenEnabled(boolean fullscreenEnabled);
        Window.setIcon(String filename);
            
    //WINDOW MISC
        Window.handle
        Window.show() //package access only.
    */
    
    public final long handle = glfwCreateWindow(640, 480, "XJGE 2 (v" + XJGE.VERSION + ")", NULL, NULL);
    
    private static int width;
    private static int height;
    private static int xPos;
    private static int yPos;
    
    private static boolean resizable;
    private static boolean fullscreen;
    
    private static String title;
    private static Monitor monitor;
    
    static void show() {
        
    }
    
    public static void setDimensions(int width, int height) {
        Window.width = width;
        Window.height = height;
    }
    
    public static int getWidth() {
        return width;
    }
    
    public static void setWidth(int width) {
        Window.width = width;
    }
    
    public static int getHeight() {
        return height;
    }
    
    public static void setHeight(int height) {
        Window.height = height;
    }
    
    public static void setPosition(int xPos, int yPos) {
        Window.xPos = xPos;
        Window.yPos = yPos;
    }
    
    public static void setPositionCentered() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer xPosBuf = stack.mallocInt(1);
            IntBuffer yPosBuf = stack.mallocInt(1);
            
            glfwGetMonitorPos(monitor.handle, xPosBuf, yPosBuf);
            
            //int centerX = Math.round(monitor.);
            
            //setPosition()
        }
    }
    
    public static int getPositionX() {
        return xPos;
    }
    
    public static void setPositionX(int xPos) {
        Window.xPos = xPos;
    }
    
    public static int getPositionY() {
        return yPos;
    }
    
    public static void setPositionY(int yPos) {
        Window.yPos = yPos;
    }
    
    public static boolean getResizable() {
        return resizable;
    }
    
    public static void setResizable(boolean resizable) {
        Window.resizable = resizable;
    }
    
    public static boolean getFullscreen() {
        return fullscreen;
    }
    
    public static void setFullscreen(boolean fullscreen) {
        Window.fullscreen = fullscreen;
    }
    
    public static String getTitle() {
        return title;
    }
            
    public static void setTitle(String title) {
        Window.title = title;
    }
    
    public static Monitor getMonitor() {
        return monitor;
    }
    
    public static void setMonitor(Monitor monitor) {
        Window.monitor = monitor;
    }
    
    public static void setIcon(String filename) {
        //TODO: the filepath is set to the XJGE jar assets the first time this
        //is called by the engine. subsequent calls will use the new filepath
        //set by the user. Refer to ShaderCore
    }
    
}