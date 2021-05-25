package dev.theskidster.xjge2.core;

import static dev.theskidster.xjge2.core.Window.HANDLE;
import dev.theskidster.xjge2.shaderutils.BufferType;
import dev.theskidster.xjge2.shaderutils.GLProgram;
import dev.theskidster.xjge2.shaderutils.Shader;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.joml.Vector2i;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author J Hoffman
 * Created: Apr 28, 2021
 */

public final class XJGE {
    
    private static int fbo;
    private static int resolutionX;
    private static int resolutionY;
    
    private static boolean initCalled;
    private static boolean matchWindowResolution;
    private static boolean freeCamEnabled;
    private static boolean terminalEnabled;
    private static boolean firstMouse = true;
    
    public static final Path PWD       = Path.of("").toAbsolutePath();
    public static final String VERSION = "0.0.0";
    
    private static Split split;
    private static String filepath = "/dev/theskidster/xjge2/assets/";
    private static FreeCam freeCam;
    private static Terminal terminal;
    
    static Map<String, GLProgram> glPrograms  = new HashMap<>();
    private static final Viewport[] viewports = new Viewport[4];
    
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
    
    public static void init(String filepath, boolean debugEnabled, Vector2i resolution, boolean windowResizable) {        
        if(!initCalled) {
            if(System.getProperty("java.version").compareTo("15.0.2") < 0) {
                Logger.logSevere("Unsupported Java version. Required 15.0.2, " + 
                                 "found: " + System.getProperty("java.version"), 
                                 null);
            }
            
            if(!glfwInit()) Logger.logSevere("Failed to initialize GLFW.", null);
            
            { //Initialize the window.
                //TODO: mention in documentation that the windowResize flag will be overruled if
                //an internal resolution is supplied.
                glfwWindowHint(GLFW_RESIZABLE, (windowResizable && resolution == null) ? GLFW_TRUE : GLFW_FALSE);
                glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
                
                Window.monitor = WinKit.findMonitors().get(1);
                Window.setIcon("img_null.png");
            }
            
            Input.importControls();
            Input.findInputDevices();
            
            glfwMakeContextCurrent(Window.HANDLE);
            GL.createCapabilities();
            Window.reconfigure();
            
            if(resolution == null) {
                matchWindowResolution = true;
                resolutionX = Window.getWidth();
                resolutionY = Window.getHeight();
            } else {
                resolutionX = resolution.x;
                resolutionY = resolution.y;
            }
            
            for(int i = 0; i < viewports.length; i++) viewports[i] = new Viewport(i);
            
            fbo = glGenFramebuffers();
            glBindFramebuffer(GL_FRAMEBUFFER, fbo);
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, viewports[0].texHandle, 0);
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, viewports[1].texHandle, 0);
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT2, GL_TEXTURE_2D, viewports[2].texHandle, 0);
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT3, GL_TEXTURE_2D, viewports[3].texHandle, 0);
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            
            createRenderbuffer();
            
            ErrorUtils.checkFBStatus(GL_FRAMEBUFFER);
            
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
            
            glfwSetKeyCallback(Window.HANDLE, (window, key, scancode, action, mods) -> {
                if(debugEnabled && key == GLFW_KEY_F2 && action == GLFW_PRESS) {
                    XJGE.freeCamEnabled = !freeCamEnabled;
                    
                    Logger.setDomain("core");
                    
                    if(freeCamEnabled) {
                        glfwSetInputMode(Window.HANDLE, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                        Logger.logInfo("Freecam enabled.");
                        viewports[0].prevCamera = viewports[0].currCamera;
                        viewports[0].currCamera = freeCam;
                    } else {
                        glfwSetInputMode(Window.HANDLE, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                        Logger.logInfo("Freecam disabled.");
                        viewports[0].currCamera = viewports[0].prevCamera;
                    }

                    Logger.setDomain(null);
                }

                if(freeCamEnabled) {
                    if(key == GLFW_KEY_W) freeCam.pressed[0] = (action != GLFW_RELEASE);
                    if(key == GLFW_KEY_A) freeCam.pressed[1] = (action != GLFW_RELEASE);
                    if(key == GLFW_KEY_S) freeCam.pressed[2] = (action != GLFW_RELEASE);
                    if(key == GLFW_KEY_D) freeCam.pressed[3] = (action != GLFW_RELEASE);

                    freeCam.setSpeedBoostEnabled(mods == GLFW_MOD_SHIFT);
                }

                if(action == GLFW_PRESS) {
                    switch(key) {
                        case GLFW_KEY_ESCAPE -> {
                            Window.close(); //TODO: temp
                        }

                        case GLFW_KEY_F1 -> {
                            Window.setFullscreen(!Window.getFullscreen());
                            //WinKit.setVSyncEnabled(!WinKit.getVSyncEnabled());
                        }
                        
                        case GLFW_KEY_1 -> setScreenSplit(Split.NONE);
                        case GLFW_KEY_2 -> setScreenSplit(Split.HORIZONTAL);
                        case GLFW_KEY_3 -> setScreenSplit(Split.VERTICAL);
                        case GLFW_KEY_4 -> setScreenSplit(Split.TRISECT);
                        case GLFW_KEY_5 -> setScreenSplit(Split.QUARTER);
                    }
                }
            });

            glfwSetCursorPosCallback(HANDLE, (window, xpos, ypos) -> {
                if(freeCamEnabled) {
                    if(firstMouse) {
                        freeCam.prevX = xpos;
                        freeCam.prevY = ypos;
                        firstMouse = false;
                    }
                    
                    //TODO: fix bug where direction changes when fullscreen is toggled.
                    freeCam.setDirection(xpos, ypos);
                } else {
                    firstMouse = true;
                }
            });
        } else {
            Logger.setDomain("core");
            Logger.logWarning("XJGE has already been initialized.", null);
            Logger.setDomain(null);
        }
        
        initCalled = true;
    }
    
    public static void start() {
        glPrograms = Collections.unmodifiableMap(glPrograms);
        
        //TODO: delete the viewport ui test code here when finished.
        //viewports[0].addUIWidget("test", new TestWidget());
        
        freeCam = new FreeCam();
        
        Window.show(matchWindowResolution);
        setScreenSplit(Split.NONE);
        Game.loop(fbo, viewports);
        
        Input.exportControls();
        GL.destroy();
        glfwTerminate();
    }
    
    static void createRenderbuffer() {
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        int rbo = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rbo);
        
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, resolutionX, resolutionY);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rbo);
        
        ErrorUtils.checkGLError();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    
    static void transferViewportState() {
        for(int i = 0; i < viewports.length; i++) {
            viewports[i] = new Viewport(viewports[i]);
        }
        
        setScreenSplit(split);
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
    
    static int getResolutionX() {
        return resolutionX;
    }
    
    static int getResolutionY() {
        return resolutionY;
    }
    
    static Split getScreenSplit() {
        return split;
    }
    
    public static String getFilepath() {
        return filepath;
    }
    
    public static GLProgram getDefaultGLProgram() {
        return glPrograms.get("default");
    }
    
    static void setResolution(int x, int y) {
        resolutionX = x;
        resolutionY = y;
    }
    
    public static void setScreenSplit(Split split) {
        XJGE.split = split;
        
        for(Viewport viewport : viewports) {
            switch(split) {
                case NONE -> {
                    viewport.active = (viewport.id == 0);
                    viewport.setBounds(resolutionX, resolutionY, 
                                       0, 0, 
                                       Window.getWidth(), Window.getHeight());
                }
                
                case HORIZONTAL -> {
                    viewport.active = (viewport.id == 0 || viewport.id == 1);
                    switch(viewport.id) {
                        case 0 -> viewport.setBounds(
                                    resolutionX, resolutionY / 2,
                                    0, Window.getHeight() / 2, 
                                    Window.getWidth(), Window.getHeight() / 2);
                            
                        case 1 -> viewport.setBounds(
                                    resolutionX, resolutionY / 2,
                                    0, 0, 
                                    Window.getWidth(), Window.getHeight() / 2);
                    }
                }
                
                case VERTICAL -> {
                    viewport.active = (viewport.id == 0 || viewport.id == 1);
                    switch(viewport.id) {
                        case 0 -> viewport.setBounds(
                                    resolutionX / 2, resolutionY,
                                    0, 0, 
                                    Window.getWidth() / 2, Window.getHeight());
                            
                        case 1 -> viewport.setBounds(
                                    resolutionX / 2, resolutionY,
                                    Window.getWidth() / 2, 0, 
                                    Window.getWidth() / 2, Window.getHeight());
                    }
                }
                
                case TRISECT -> {
                    viewport.active = (viewport.id != 3);
                    switch(viewport.id) {
                        case 0 -> viewport.setBounds(
                                    resolutionX / 2, resolutionY / 2,
                                    0, Window.getHeight() / 2, 
                                    Window.getWidth() / 2, Window.getHeight() / 2);
                            
                        case 1 -> viewport.setBounds(
                                    resolutionX / 2, resolutionY / 2,
                                    Window.getWidth() / 2, Window.getHeight() / 2, 
                                    Window.getWidth() / 2, Window.getHeight() / 2);
                            
                        case 2 -> viewport.setBounds(
                                    resolutionX / 2, resolutionY / 2,
                                    Window.getWidth() / 4, 0, 
                                    Window.getWidth() / 2, Window.getHeight() / 2);
                    }
                }
                
                case QUARTER -> {
                    viewport.active = true;
                    switch(viewport.id) {
                        case 0 -> viewport.setBounds(
                                    resolutionX / 2, resolutionY / 2,
                                    0, Window.getHeight() / 2, 
                                    Window.getWidth() / 2, Window.getHeight() / 2);
                            
                        case 1 -> viewport.setBounds(
                                    resolutionX / 2, resolutionY / 2,
                                    Window.getWidth() / 2, Window.getHeight() / 2, 
                                    Window.getWidth() / 2, Window.getHeight() / 2);
                            
                        case 2 -> viewport.setBounds(
                                    resolutionX / 2, resolutionY / 2,
                                    0, 0, 
                                    Window.getWidth() / 2, Window.getHeight() / 2);
                            
                        case 3 -> viewport.setBounds(
                                    resolutionX / 2, resolutionY / 2,
                                    Window.getWidth() / 2, 0, 
                                    Window.getWidth() / 2, Window.getHeight() / 2);
                    }
                }
            }
        }
    }
    
}