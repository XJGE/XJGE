package dev.theskidster.xjge2.core;

import static org.lwjgl.glfw.GLFW.glfwInit;

/**
 * @author J Hoffman
 * Created: Apr 28, 2021
 */

public final class XJGE {
    
    private static boolean initCalled;
    
    public static final String VERSION = "0.0.0";
    private static String filepath     = "/dev/theskidster/xjge2/assets/";
    
    /*
    XJGE.init(String filepath);
    ...setup shaders...
    XJGE.start(Map<String, GLProgram> programs, Scene initialScene);

    init() should be called once at the beginning of the application to 
    initialize the engines various libraries and set the filepath it will 
    use locate whatever resources it needs such as game assets and other 
    files.

    it's assumed the implementing application will compile it's shaders 
    between the init() and start() methods, a collection of these compiled 
    programs will be supplied to the engine via the "programs" parameter in
    the start method. Additonally, there's another parameter that will be 
    used to set the initial scene the engine will enter upon startup.
    */
    
    public static void init(String filepath) {
        if(!initCalled) {
            if(System.getProperty("java.version").compareTo("15.0.2") < 0) {
                Logger.logSevere("Unsupported Java version. Required 15.0.2, " + 
                                 "found: " + System.getProperty("java.version"), 
                                 null);
            }
            
            if(!glfwInit()) Logger.logSevere("Failed to initialize GLFW.", null);
            
            WinKit.getConnectedMonitors();
            
            Window.setDimensions(0, 0);
            Window.setPositionCentered();
            Window.setIcon("img_null.png");
            Window.setMonitor(WinKit.getConnectedMonitors().get(1));
            
            XJGE.filepath = filepath;
        } else {
            Logger.setDomain("core");
            Logger.logWarning("XJGE has already been initialized.", null);
            Logger.setDomain(null);
        }
        
        initCalled = true;
    }
    
    public static void start() {
        
    }
    
    public static String getFilepath() {
        return filepath;
    }
    
}