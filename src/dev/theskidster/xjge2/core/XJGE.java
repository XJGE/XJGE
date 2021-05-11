package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import dev.theskidster.xjge2.shaderutils.BufferType;
import dev.theskidster.xjge2.shaderutils.GLProgram;
import dev.theskidster.xjge2.shaderutils.Shader;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author J Hoffman
 * Created: Apr 28, 2021
 */

public final class XJGE {
    
    private static int fbo;
    
    private static boolean initCalled;
    static boolean debugAllowed;
    
    public static final String VERSION = "0.0.0";
    private static String filepath     = "/dev/theskidster/xjge2/assets/";
    public static final Path PWD       = Path.of("").toAbsolutePath();
    
    static Map<String, GLProgram> glPrograms = new HashMap<>();
    
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
    
    public static void init(String filepath, boolean windowResizable, boolean debugAllowed) {
        if(!initCalled) {
            if(System.getProperty("java.version").compareTo("15.0.2") < 0) {
                Logger.logSevere("Unsupported Java version. Required 15.0.2, " + 
                                 "found: " + System.getProperty("java.version"), 
                                 null);
            }
            
            if(!glfwInit()) Logger.logSevere("Failed to initialize GLFW.", null);
            
            XJGE.debugAllowed = debugAllowed;
            
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
            
            /*
            TODO: 
            - implement split screen
            
            We can use the old splitscreen system, but remeber to recreate the 
            renderbuffer anytime the windows size is changed otherwise we'll end
            up with some goofy viewport cropping.
            */
            
            { //Initialize the default shaders.
                var shaderSourceFiles = new LinkedList<Shader>() {{
                    add(new Shader("defaultVertex.glsl", GL_VERTEX_SHADER));
                    add(new Shader("defaultFragment.glsl", GL_FRAGMENT_SHADER));
                }};
                
                GLProgram defaultProgram = new GLProgram(shaderSourceFiles, "default");
                
                defaultProgram.use();
                defaultProgram.addUniform(BufferType.INT,  "uType");
                defaultProgram.addUniform(BufferType.MAT4, "uModel");
                defaultProgram.addUniform(BufferType.MAT4, "uView");
                defaultProgram.addUniform(BufferType.MAT4, "uProjection");
                
                glPrograms.put("default", defaultProgram);
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
        glPrograms = Collections.unmodifiableMap(glPrograms);
        
        Window.show();
        Game.loop();
        
        Input.exportControls();
        GL.destroy();
        glfwTerminate();
    }
    
    public static void addGLProgram(String name, GLProgram glProgram) {
        if(!name.equals("default")) {
            glPrograms.put(name, glProgram);
        } else {
            Logger.setDomain("core");
            Logger.logWarning("Failed to add program \"" + name + "\". This " + 
                              " name is reserved for engine use- pick another.", 
                              null);
            Logger.setDomain(null);
        }
    }
    
    public static String getFilepath() {
        return filepath;
    }
    
}