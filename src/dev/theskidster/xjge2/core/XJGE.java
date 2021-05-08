package dev.theskidster.xjge2.core;

import java.nio.file.Path;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author J Hoffman
 * Created: Apr 28, 2021
 */

public final class XJGE {
    
    private static int fbo;
    private static int tickCount = 0;
    private static int fps;
    
    private static double deltaMetric = 0;
    
    private static boolean initCalled;
    private static boolean ticked;
    
    public static final String VERSION = "0.0.0";
    private static String filepath     = "/dev/theskidster/xjge2/assets/";
    
    public static final Path PWD = Path.of("").toAbsolutePath();
    
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
    
    public static void init(String filepath, boolean windowResizable) {
        if(!initCalled) {
            if(System.getProperty("java.version").compareTo("15.0.2") < 0) {
                Logger.logSevere("Unsupported Java version. Required 15.0.2, " + 
                                 "found: " + System.getProperty("java.version"), 
                                 null);
            }
            
            if(!glfwInit()) Logger.logSevere("Failed to initialize GLFW.", null);
            
            { //Initialize the window.
                glfwWindowHint(GLFW_RESIZABLE, windowResizable ? GLFW_TRUE : GLFW_FALSE);
                glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
                
                Window.monitor = WinKit.findMonitors().get(1);
                Window.reconfigure();
                Window.setIcon("img_null.png");
            }
            
            Input.importControls();
            Input.findInputDevices();
            
            glfwMakeContextCurrent(Window.HANDLE);
            GL.createCapabilities();
            
            int texHandle = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, texHandle);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, Window.getWidth(), Window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
            glBindTexture(GL_TEXTURE_2D, 0);
            
            fbo = glGenFramebuffers();
            glBindFramebuffer(GL_FRAMEBUFFER, fbo);
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texHandle, 0);
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            
            /*
            TODO: 
            - implement split screen
            
            We can use the old splitscreen system, but remeber to recreate the 
            renderbuffer anytime the windows size is changed otherwise we'll end
            up with some goofy viewport cropping.
            */
            
            { //Initialize the default shaders.
                
            }
            
            Logger.printSystemInfo();
            
            XJGE.filepath = filepath;
        } else {
            Logger.setDomain("core");
            Logger.logWarning("XJGE has already been initialized.", null);
            Logger.setDomain(null);
        }
        
        initCalled = true;
    }
    
    public static void start() {
        Window.show();
        
        int cycles = 0;
        final double TARGET_DELTA = 1 / 60.0;
        double prevTime = glfwGetTime();
        double currTime;
        double delta = 0;
        
        while(!glfwWindowShouldClose(Window.HANDLE)) {
            currTime = glfwGetTime();
            delta    += currTime - prevTime;
            
            if(delta < TARGET_DELTA && WinKit.getVSyncEnabled()) delta = TARGET_DELTA;
            
            prevTime = currTime;
            ticked   = false;
            
            while(delta >= TARGET_DELTA) {
                deltaMetric = delta;
                
                delta     -= TARGET_DELTA;
                ticked    = true;
                tickCount = (tickCount == Integer.MAX_VALUE) ? 0 : tickCount + 1;
                
                glfwPollEvents();
                
                
            }
            
            glfwPollEvents();
            Input.pollInput();
        }
        
        Input.exportControls();
        GL.destroy();
        glfwTerminate();
    }
    
    public static String getFilepath() {
        return filepath;
    }
    
}