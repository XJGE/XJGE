package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import dev.theskidster.xjge2.shaderutils.BufferType;
import dev.theskidster.xjge2.shaderutils.GLProgram;
import dev.theskidster.xjge2.shaderutils.Shader;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author J Hoffman
 * Created: Apr 28, 2021
 */

public final class XJGE {
    
    private static int fbo;
    private static int fps;
    private static int tickCount = 0;
    
    private static double deltaMetric = 0;
    
    private static boolean initCalled;
    private static boolean ticked;
    static boolean debugAllowed;
    
    public static final String VERSION = "0.0.0";
    private static String filepath     = "/dev/theskidster/xjge2/assets/";
    public static final Path PWD       = Path.of("").toAbsolutePath();
    
    private static Color clearColor = Color.BLACK;
    private static Scene scene;
    private static Camera camera;
    
    static Map<String, GLProgram> glPrograms = new HashMap<>();
    
    private static final Queue<Event> events = new PriorityQueue<>(Comparator.comparing(Event::getPriority));
    
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
        
        camera = new FreeCam(); //this will be removed eventually- just here for testing purposes.
        
        Window.show();
        
        int cycles = 0;
        final double TARGET_DELTA = 1 / 60.0;
        double prevTime = glfwGetTime();
        double currTime;
        double delta = 0;
        
        while(!glfwWindowShouldClose(Window.HANDLE)) {
            glfwPollEvents();
            
            currTime = glfwGetTime();
            delta    += currTime - prevTime;
            if(delta < TARGET_DELTA && WinKit.getVSyncEnabled()) delta = TARGET_DELTA;
            prevTime = currTime;
            ticked   = false;
            
            while(delta >= TARGET_DELTA) {
                Input.pollInput();
                
                deltaMetric = delta;
                
                delta     -= TARGET_DELTA;
                ticked    = true;
                tickCount = (tickCount == Integer.MAX_VALUE) ? 0 : tickCount + 1;
                
                processEvents(TARGET_DELTA);
                
                if(tick(60)) {
                    fps    = cycles;
                    cycles = 0;
                }
            }
            
            glClearColor(clearColor.r, clearColor.g, clearColor.b, 0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            camera.render(glPrograms);
            scene.render(glPrograms, camera);
            glfwSwapBuffers(Window.HANDLE);
            
            if(!ticked) {
                try {
                    Thread.sleep(1);
                } catch(InterruptedException e) {
                    Logger.logSevere(e.getMessage(), e);
                }
            } else {
                cycles++;
            }
        }
        
        Input.exportControls();
        GL.destroy();
        glfwTerminate();
    }
    
    private static void processEvents(double targetDelta) {
        if(events.size() > 0) {
            Event event = events.peek();
            
            if(!event.resolved) event.resolve();
            else                events.poll();
        } else {
            camera.update(); //TODO: temp
            scene.update(targetDelta);
        }
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
    
    public static final void addEntity(String name, Entity entity) {
        scene.entities.put(name, entity);
    }
    
    public static boolean tick(int cycles) {
        return tickCount % cycles == 0;
    }
    
    public static String getFilepath() {
        return filepath;
    }
    
    public static void setScene(Scene scene) {
        Logger.setDomain("core");
        Logger.logInfo("Current scene changed to \"" + scene.name + "\"");
        Logger.newLine();
        Logger.setDomain(null);
        
        if(XJGE.scene != null) scene.exit();
        XJGE.scene = scene;
    }
    
    public static void setClearColor(Color color) {
        clearColor = color;
    }
    
}