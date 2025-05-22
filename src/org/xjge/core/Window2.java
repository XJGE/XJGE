package org.xjge.core;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created: Apr 29, 2021
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Window2 {
    
    private static boolean fullscreen;
    
    private static int positionX;
    private static int positionY;
    private static int width;
    private static int height;
    
    private static long handle = NULL;
    
    private static String title;
    
    private static final Viewport[] viewports = new Viewport[4];
    
    private Window2() {}
    
    private static void init() {
        if(handle != NULL) return;
        
        if(!glfwInit()) Logger.logError("Failed to initialize GLFW", null);
        
        handle = glfwCreateWindow(640, 480, "XJGE v" + XJGE.VERSION, NULL, NULL);
    }
    
    public static void show() {
        init();
        
        /**
         * Maybe add these?
         * XJGE.setUserSavedControls(viewportID, Collection);
         * XJGE.setMusicMasterVolume(1.0f);
         * XJGE.setSoundMasterVolume(1.0f);
         * XJGE.setVsyncEnabled(true);
         * XJGE.setGamma() //future feature?
         * XJGE.findMonitors(); //returns list of monitor objects
         * XJGE.findSpeakers(); //returns list of speaker objects
         * XJGE.findGamepads(); //returns list of gamepads
         * 
         * Definitely add these:
         * XJGE.setAssetsFilepath("");
         * XJGE.setScenesFilepath("");
         * XJGE.setDebugModeEnabled(true);
         * XJGE.setScene(Scene);
         * XJGE.addEvent(Event);
         * XJGE.restrict4K(true);
         * XJGE.retainFullscreen(false);
         * 
         * Window.show();
         * 
         * Window.setSize(0, 0);
         * Window.setPosition(0, 0);
         * Window.setTitle("");
         * Window.setIcon("");
         * Window.setFullscreen(false);
         * Window.setScreenSplitType(ScreenSplitType);
         * Window.setResolution(640, 480);
         * Window.setResizable(false);
         * Window.setMontior(Monitor);
         * Window.setInputMode(CURSOR_NORMAL);
         * Window.center();
         * 
         * Window.getWidth();
         * Window.getHeight();
         * Window.getPositionX();
         * Window.getPositionY();
         * Window.getTitle();
         * Window.getFullscreen();
         * Window.getScreenSplitType();
         * Window.getResolutionWidth();
         * Window.getResolutionHeight();
         * Window.getResizeable();
         * Window.getMonitor();
         * Window.getInputMode();
         * 
         * XJGE methods like setAssetsFilepath(), setScenesFilepath(), and 
         * setDebugModeEnabled() can no longer be called following Window.show()
         * 
         * Game -> XJGE (maintains loop, scene, and config settings)
         * XJGE -> Window (maintains window, viewport, and glfw callbacks)
         */
    }
    
}