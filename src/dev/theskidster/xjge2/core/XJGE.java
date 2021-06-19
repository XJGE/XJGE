package dev.theskidster.xjge2.core;

import static dev.theskidster.xjge2.core.Input.KEY_MOUSE_COMBO;
import dev.theskidster.xjge2.core.Terminal.TCCLS;
import static dev.theskidster.xjge2.core.Window.HANDLE;
import dev.theskidster.xjge2.graphics.Texture;
import dev.theskidster.xjge2.graphics.BufferType;
import dev.theskidster.xjge2.graphics.GLProgram;
import dev.theskidster.xjge2.graphics.Shader;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
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
    private static boolean showLightSources;
    private static boolean firstMouse = true;
    
    public static final Path PWD       = Path.of("").toAbsolutePath();
    public static final String VERSION = "0.0.0";
    
    private static Split split     = Split.NONE;
    private static String filepath = "/dev/theskidster/xjge2/assets/";
    
    private static Font engineFont;
    private static Texture engineIcons;
    private static FreeCam freeCam;
    private static Terminal terminal;
    private static DebugInfo debugInfo;
    
    private static TreeMap<String, TerminalCommand> engineCommands     = new TreeMap<>();
    private static final TreeMap<String, TerminalCommand> userCommands = new TreeMap<>();
    
    static Map<String, GLProgram> glPrograms   = new HashMap<>();
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
    
    private XJGE() {}
    
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
                
                Window.monitor = Hardware.findMonitors().get(1);
                Window.setIcon("img_null.png");
            }
            
            glfwMakeContextCurrent(Window.HANDLE);
            GL.createCapabilities();
            Window.reconfigure();
            
            Input.importControls();
            Input.findInputDevices();
            
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
                defaultProgram.addUniform(BufferType.INT,   "uType");
                defaultProgram.addUniform(BufferType.INT,   "uNumLights");
                defaultProgram.addUniform(BufferType.FLOAT, "uOpacity");
                defaultProgram.addUniform(BufferType.VEC2,  "uTexCoords");
                defaultProgram.addUniform(BufferType.VEC3,  "uColor");
                defaultProgram.addUniform(BufferType.MAT3,  "uNormal");
                defaultProgram.addUniform(BufferType.MAT4,  "uModel");
                defaultProgram.addUniform(BufferType.MAT4,  "uView");
                defaultProgram.addUniform(BufferType.MAT4,  "uProjection");
                defaultProgram.addUniform(BufferType.MAT4,  "uBoneTransforms");
                
                for(int i = 0; i < Scene.MAX_LIGHTS; i++) {
                    defaultProgram.addUniform(BufferType.FLOAT, "uLights[" + i + "].brightness");
                    defaultProgram.addUniform(BufferType.FLOAT, "uLights[" + i + "].contrast");
                    defaultProgram.addUniform(BufferType.VEC3,  "uLights[" + i + "].position");
                    defaultProgram.addUniform(BufferType.VEC3,  "uLights[" + i + "].ambient");
                    defaultProgram.addUniform(BufferType.VEC3,  "uLights[" + i + "].diffuse");
                }
                
                glPrograms.put("default", defaultProgram);
            }
            
            engineFont  = new Font();
            engineIcons = new Texture("spr_engineicons.png");
            
            Scene.setIconTexture(engineIcons);
            Logger.printSystemInfo();
            
            XJGE.filepath     = filepath;
            Widget.engineFont = new Font(engineFont);
            
            //TODO: add more commands
            engineCommands = new TreeMap<>() {{
                put("cls",             new TCCLS());
                put("help",            new TCHelp());
                put("setFullscreen",   new TCSetFullscreen());
                put("setMonitor",      new TCSetMonitor());
                put("setScreenSplit",  new TCScreenSplit());
                put("setVSync",        new TCSetVSync());
                put("setVideoMode",    new TCSetVideoMode());
                put("showCommands",    new TCShowCommands());
                put("terminate",       new TCTerminate());
            }};
            
            glfwSetKeyCallback(Window.HANDLE, (window, key, scancode, action, mods) -> {
                if(key == GLFW_KEY_F1 && action == GLFW_PRESS) {
                    if(debugEnabled && mods == GLFW_MOD_SHIFT) {
                        XJGE.terminalEnabled = !terminalEnabled;
                        
                        if(terminalEnabled) Input.setDeviceEnabled(KEY_MOUSE_COMBO, false);
                        else                Input.revertEnabledState(KEY_MOUSE_COMBO);
                    } else {
                        debugInfo.show = !debugInfo.show;
                    }
                    
                    if(debugInfo.show) debugInfo.updatePosition();
                }
                
                if(debugEnabled && key == GLFW_KEY_F2 && action == GLFW_PRESS) {
                    if(!terminalEnabled) {
                        XJGE.freeCamEnabled = !freeCamEnabled;
                        
                        if(freeCamEnabled) {
                            glfwSetInputMode(Window.HANDLE, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                            viewports[0].prevCamera = viewports[0].currCamera;
                            viewports[0].currCamera = freeCam;
                        } else {
                            glfwSetInputMode(Window.HANDLE, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                            viewports[0].currCamera = viewports[0].prevCamera;
                        }
                    } else {
                        Logger.setDomain("core");
                        Logger.logInfo("Freecam access denied, command terminal " + 
                                       "is currently in use. Close the command " + 
                                       "terminal and try again.");
                        Logger.setDomain(null);
                    }
                }
                
                if(debugEnabled && key == GLFW_KEY_F3 && action == GLFW_PRESS) {
                    showLightSources = !showLightSources;
                }

                if(freeCamEnabled && !terminalEnabled) {
                    if(key == GLFW_KEY_W) freeCam.pressed[0] = (action != GLFW_RELEASE);
                    if(key == GLFW_KEY_A) freeCam.pressed[1] = (action != GLFW_RELEASE);
                    if(key == GLFW_KEY_S) freeCam.pressed[2] = (action != GLFW_RELEASE);
                    if(key == GLFW_KEY_D) freeCam.pressed[3] = (action != GLFW_RELEASE);

                    freeCam.setSpeedBoostEnabled(mods == GLFW_MOD_SHIFT);
                }

                if(XJGE.terminalEnabled) {
                    terminal.processKeyInput(key, action, mods);
                } else {
                    //TODO: pass key input to ui widget
                }
            });

            glfwSetCursorPosCallback(HANDLE, (window, xpos, ypos) -> {
                if(freeCamEnabled && !terminalEnabled) {
                    if(firstMouse) {
                        freeCam.prevX = xpos;
                        freeCam.prevY = ypos;
                        firstMouse    = false;
                    }
                    
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
        engineCommands.putAll(userCommands);
        engineCommands.values().forEach(command -> command.setCommands(engineCommands));
        
        glPrograms = Collections.unmodifiableMap(glPrograms);
        freeCam    = new FreeCam();
        terminal   = new Terminal(engineCommands, engineFont);
        debugInfo  = new DebugInfo(engineFont, engineIcons);
        
        Window.show();
        setScreenSplit(getScreenSplit());
        
        glfwSetWindowSizeCallback(HANDLE, (window, w, h) -> {
            Window.updateDimensions(w, h);

            if(matchWindowResolution && Window.getWidth() != 0 && Window.getHeight() != 0) {
                resolutionX = w;
                resolutionY = h;

                createRenderbuffer();
            }

            setScreenSplit(getScreenSplit());
            debugInfo.updatePosition();
        });
        
        Game.loop(fbo, viewports, terminal, debugInfo);
        
        engineFont.freeTexture();
        engineIcons.freeTexture();
        terminal.freeBuffers();
        debugInfo.freeBuffers();
        
        Input.exportControls();
        GL.destroy();
        glfwTerminate();
    }
    
    private static void createRenderbuffer() {
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
    
    public static void addCommand(String name, TerminalCommand command) {
        if(engineCommands.containsKey(name)) {
            Logger.setDomain("core");
            Logger.logWarning("Failed to add command \"" + name + "\". A command " + 
                              "by this name already exists as a part of the engines " +
                              "core features.", 
                              null);
            Logger.setDomain(null);
        } else {
            userCommands.put(name, command);
        }
    }
    
    public static void addUIWidget(int viewportID, String name, Widget widget) {
        switch(viewportID) {
            case 0, 1, 2, 3 -> viewports[viewportID].addUIWidget(name, widget);
        }
    }
    
    public static void removeUIWidget(int viewportID, String name) {
        switch(viewportID) {
            case 0, 1, 2, 3 -> viewports[viewportID].removeUIWidget(name);
        }
    }
    
    public static int getResolutionX() {
        return resolutionX;
    }
    
    public static int getResolutionY() {
        return resolutionY;
    }
    
    static boolean getTerminalEnabled() {
        return terminalEnabled;
    }
    
    static boolean getLightSourcesVisible() {
        return showLightSources;
    }
    
    public static Split getScreenSplit() {
        return split;
    }
    
    public static String getFilepath() {
        return filepath;
    }
    
    public static GLProgram getDefaultGLProgram() {
        return glPrograms.get("default");
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