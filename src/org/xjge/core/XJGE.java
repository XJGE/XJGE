package org.xjge.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import org.xjge.core.Terminal.TCCLS;
import org.xjge.graphics.Texture;
import org.xjge.graphics.BufferType;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Shader;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.joml.Vector2i;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GLUtil.setupDebugMessageCallback;
import static org.xjge.core.Input.KEY_MOUSE_COMBO;
import static org.xjge.core.Window.HANDLE;

//Created: Apr 28, 2021

/**
 * Represents the game engine in its broadest sense and provides a point at 
 * which its functionality may be extended to better suit the individual 
 * requirements of the implementation.
 * <p>
 * More specifically this class provides the following features:
 * <ul>
 * <li>The ability to change how the screen will be 
 *     {@linkplain setScreenSplit divided} during split screen mode.</li>
 * <li>Convenient access to the {@linkplain getDefaultGLProgram default shader 
 *     program} used internally by the engine.</li>
 * <li>The ability to provide the engine with supplemental {@linkplain 
 *     addCommand terminal commands} and 
 *     {@linkplain addGLProgram shader programs}.</li>
 * <li>Control over which {@link Camera} object each viewport will use to 
 *     display the scene from their perspective.</li>
 * <li>The ability to {@linkplain addUIWidget add} and {@linkplain 
 *     removeUIWidget remove} UI widgets from viewports.</li>
 * </ul>
 * <p>
 * Before the engines features can be used {@link init init()} must be called 
 * followed by changing whichever settings the implementation needs to before 
 * exposing the game window with {@link start start()}.
 * 
 * @author J Hoffman
 * @since  2.0.0
 * 
 * @see Hardware
 * @see Input
 * @see Monitor
 * @see Logger
 * @see Terminal
 * @see Viewport
 * @see Window
 */
public final class XJGE {
    
    private static int fbo;
    private static int resolutionX;
    private static int resolutionY;
    
    static float noclipSpeedFactor = 1.0f;
    
    private static double cursorX;
    private static double cursorY;
    
    private static boolean initCalled;
    private static boolean restrict4KResolutions;
    private static boolean matchWindowResolution;
    private static boolean noclipEnabled;
    private static boolean terminalEnabled;
    private static boolean showLightSources;
    private static boolean firstMouse = true;
    
    public static final Path PWD       = Path.of("").toAbsolutePath();
    public static final String VERSION = "2.0.4";
    
    private static Split split = Split.NONE;
    
    private static String assetsFilepath = "/org/xjge/assets/";
    private static String scenesFilepath;
    
    private static Font engineFont;
    private static Texture engineIcons;
    private static Sound beep;
    private static Noclip freeCam;
    private static Terminal terminal;
    private static DebugInfo debugInfo;
    
    private static TreeMap<String, TerminalCommand> engineCommands     = new TreeMap<>();
    private static final TreeMap<String, TerminalCommand> userCommands = new TreeMap<>();
    
    static GLProgram depthProgram;
    static GLProgram blurProgram;
    static Map<String, GLProgram> glPrograms   = new HashMap<>();
    private static final Viewport[] viewports = new Viewport[4];
    
    /**
     * Default constructor provided here to keep it out of the implementations 
     * reach.
     */
    private XJGE() {}
    
    /**
     * Initializes the engines assets, compiles the default shader programs, 
     * and searches for connected peripheral devices. This method must be called 
     * once before the engine can be used.
     * <p>
     * NOTE: If a resolution is provided the value of {@code windowResizable} 
     * will be ignored. Additionally, the {@code scenesFilepath} should use 
     * periods for separators instead of slashes akin to how it appears in your 
     * IDEs package explorer. A call to this method might look something like 
     * the following:
     * <blockquote><pre>
     * init(<i>"/dev/theskidster/game/assets/"</i>, <i>"dev.theskidster.game.scenes."</i>, <b>null</b>, ...);
     * </pre></blockquote>
     * 
     * @param assetsFilepath the relative filepath to the folder that contains 
     *                       all of the games assets
     * @param scenesFilepath the relative filepath to the package that contains 
     *                       all of the games scene subclasses
     * @param resolution the internal resolution the engine will display the 
     *                   game at or <b>null</b> to copy the resolution of the 
     *                   window
     * @param createOpenGLLog if true, the engine will create a text file 
     *                        containing messages sent by the OpenGL API
     * @param debugEnabled if true, the engine will provide debugging 
     *                     utilities at runtime
     * @param restrict4K if true, the engine will not permit the use of video 
     *                   modes whos resolution is greater than 1920x1080
     * @param retainFullscreen if true, the game window will retain its mode 
     *                         (fullscreen or windowed) between runtime sessions
     * @param windowResizable if true, the user will be allowed to freely alter 
     *                        the size of the window at runtime
     */
    public static void init(String assetsFilepath, String scenesFilepath, Vector2i resolution, boolean createOpenGLLog, 
                              boolean debugEnabled, boolean restrict4K, boolean retainFullscreen, boolean windowResizable) {
        if(!initCalled) {
            if(System.getProperty("java.version").compareTo("15.0.2") < 0) {
                Logger.logSevere("Unsupported Java version. Required 15.0.2, " + 
                                 "found: " + System.getProperty("java.version"), 
                                 null);
            }
            
            if(!glfwInit()) Logger.logSevere("Failed to initialize GLFW.", null);
            
            boolean fullscreen = false;
            restrict4KResolutions = restrict4K;
            
            //Load settings from engine configuration file.
            try {
                InputStream stream = new FileInputStream(PWD.toString() + "/engine.cfg");
                XMLStreamReader xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
                
                while(xmlReader.hasNext()) {
                    switch(xmlReader.next()) {
                        case XMLStreamConstants.START_ELEMENT -> {
                            if(xmlReader.getName().getLocalPart().equals("config")) {
                                final float soundMaster  = Float.parseFloat(xmlReader.getAttributeValue(null, "soundMaster"));
                                final float musicMaster  = Float.parseFloat(xmlReader.getAttributeValue(null, "musicMaster"));
                                final boolean vSync      = Boolean.parseBoolean(xmlReader.getAttributeValue(null, "vSync"));
                                
                                Audio.setMasterVolumePreferences(soundMaster, musicMaster);
                                Hardware.setVSyncPreference(vSync);
                                
                                if(retainFullscreen) {
                                    fullscreen = Boolean.parseBoolean(xmlReader.getAttributeValue(null, "fullscreen"));
                                }
                            }
                        }
                        
                        case XMLStreamConstants.END_ELEMENT -> {
                            if(xmlReader.getName().getLocalPart().equals("config")) {
                                xmlReader.close();
                            }
                        }
                    }
                }
            } catch(FileNotFoundException | NumberFormatException | XMLStreamException e) {
                Logger.setDomain("core");
                Logger.logWarning("Failed to parse engine configuration " + 
                                  "file, using default configuration.", 
                                  e);
                Logger.setDomain(null);
            }
            
            { //Initialize 3D audio utilities.
                Audio.speaker = Hardware.findSpeakers().get(1);
                Audio.speaker.setContextCurrent();
            }
            
            { //Initialize the game window.
                glfwWindowHint(GLFW_RESIZABLE, (windowResizable && resolution == null) ? GLFW_TRUE : GLFW_FALSE);
                glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
                
                Window.monitor = Hardware.findMonitors().get(1);
                Window.setFullscreenPreference(fullscreen);
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
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, viewports[0].viewTexHandle, 0);
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, viewports[1].viewTexHandle, 0);
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT2, GL_TEXTURE_2D, viewports[2].viewTexHandle, 0);
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT3, GL_TEXTURE_2D, viewports[3].viewTexHandle, 0);
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT4, GL_TEXTURE_2D, viewports[0].bloomTexHandle, 0);
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT5, GL_TEXTURE_2D, viewports[1].bloomTexHandle, 0);
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT6, GL_TEXTURE_2D, viewports[2].bloomTexHandle, 0);
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT7, GL_TEXTURE_2D, viewports[3].bloomTexHandle, 0);
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            
            createRenderbuffer();
            
            ErrorUtils.checkFBStatus(GL_FRAMEBUFFER);
            
            //Initialize the default shader program that will be provided to the implementation.
            {
                var shaderSourceFiles = new LinkedList<Shader>() {{
                    add(new Shader("defaultVertex.glsl", GL_VERTEX_SHADER));
                    add(new Shader("defaultFragment.glsl", GL_FRAGMENT_SHADER));
                }};
                
                GLProgram defaultProgram = new GLProgram(shaderSourceFiles, "default");
                
                defaultProgram.use();
                defaultProgram.addUniform(BufferType.INT,   "uType");
                defaultProgram.addUniform(BufferType.INT,   "uPCFValue");
                defaultProgram.addUniform(BufferType.INT,   "uShine");
                defaultProgram.addUniform(BufferType.INT,   "uNumLights");
                defaultProgram.addUniform(BufferType.INT,   "uTexture");
                defaultProgram.addUniform(BufferType.INT,   "uDepthTexture");
                defaultProgram.addUniform(BufferType.INT,   "uSkyTexture");
                defaultProgram.addUniform(BufferType.INT,   "uBloomTexture");
                defaultProgram.addUniform(BufferType.INT,   "uShadowMapActive");
                defaultProgram.addUniform(BufferType.INT,   "uBloomOverride");
                defaultProgram.addUniform(BufferType.FLOAT, "uOpacity");
                defaultProgram.addUniform(BufferType.FLOAT, "uMinShadowBias");
                defaultProgram.addUniform(BufferType.FLOAT, "uMaxShadowBias");
                defaultProgram.addUniform(BufferType.FLOAT, "uBloomThreshold");
                defaultProgram.addUniform(BufferType.VEC2,  "uTexCoords");
                defaultProgram.addUniform(BufferType.VEC3,  "uColor");
                defaultProgram.addUniform(BufferType.VEC3,  "uCamPos");
                defaultProgram.addUniform(BufferType.MAT3,  "uNormal");
                defaultProgram.addUniform(BufferType.MAT4,  "uModel");
                defaultProgram.addUniform(BufferType.MAT4,  "uView");
                defaultProgram.addUniform(BufferType.MAT4,  "uProjection");
                defaultProgram.addUniform(BufferType.MAT4,  "uBoneTransforms");
                defaultProgram.addUniform(BufferType.MAT4,  "uLightSpace");
                
                for(int i = 0; i < Scene.MAX_LIGHTS; i++) {
                    defaultProgram.addUniform(BufferType.FLOAT, "uLights[" + i + "].brightness");
                    defaultProgram.addUniform(BufferType.FLOAT, "uLights[" + i + "].contrast");
                    defaultProgram.addUniform(BufferType.FLOAT, "uLights[" + i + "].distance");
                    defaultProgram.addUniform(BufferType.VEC3,  "uLights[" + i + "].position");
                    defaultProgram.addUniform(BufferType.VEC3,  "uLights[" + i + "].ambient");
                    defaultProgram.addUniform(BufferType.VEC3,  "uLights[" + i + "].diffuse");
                    defaultProgram.addUniform(BufferType.VEC3,  "uLights[" + i + "].specular");
                }
                
                glPrograms.put("default", defaultProgram);
            }
            
            //Create shader program that will generate shadow map output.
            {
                var shaderSourceFiles = new LinkedList<Shader>() {{
                    add(new Shader("depthVertex.glsl", GL_VERTEX_SHADER));
                    add(new Shader("depthFragment.glsl", GL_FRAGMENT_SHADER));
                }};
                
                depthProgram = new GLProgram(shaderSourceFiles, "default");
                
                depthProgram.use();
                depthProgram.addUniform(BufferType.INT,  "uTexture");
                depthProgram.addUniform(BufferType.MAT4, "uModel");
                depthProgram.addUniform(BufferType.MAT4, "uLightSpace");
            }
            
            //Create shader program for applying gaussian blur.
            {
                var shaderSourceFiles = new LinkedList<Shader>() {{
                    add(new Shader("blurVertex.glsl", GL_VERTEX_SHADER));
                    add(new Shader("blurFragment.glsl", GL_FRAGMENT_SHADER));
                }};
                
                blurProgram = new GLProgram(shaderSourceFiles, "default");
                
                blurProgram.use();
                blurProgram.addUniform(BufferType.INT,   "uBloomTexture");
                blurProgram.addUniform(BufferType.INT,   "uHorizontal");
                blurProgram.addUniform(BufferType.FLOAT, "uWeight");
                blurProgram.addUniform(BufferType.MAT4,  "uProjection");
            }
            
            engineFont  = new Font();
            engineIcons = new Texture("spr_engineicons.png");
            beep        = new Sound("sfx_beep.ogg");
            
            Light.setIconTexture(engineIcons);
            Logger.printSystemInfo();
            
            XJGE.assetsFilepath = assetsFilepath;
            XJGE.scenesFilepath = scenesFilepath;
            Widget.defaultFont  = new Font(engineFont);
            
            engineCommands = new TreeMap<>() {{
                put("cls",                  new TCCLS());
                put("help",                 new TCHelp());
                put("setFullscreen",        new TCSetFullscreen());
                put("setMonitor",           new TCSetMonitor());
                put("setMusicMasterVolume", new TCSetMusicMasterVolume());
                put("setScene",             new TCSetScene());
                put("setScreenSplit",       new TCSetScreenSplit());
                put("setSoundMasterVolume", new TCSetSoundMasterVolume());
                put("setSpeaker",           new TCSetSpeaker());
                put("setVSync",             new TCSetVSync());
                put("setVideoMode",         new TCSetVideoMode());
                put("showCommands",         new TCShowCommands());
                put("terminate",            new TCTerminate());
            }};
            
            if(createOpenGLLog) {
                try {
                    String date = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
                    
                    File file = new File("opengl log " + date + ".txt");
                    int duplicate = 0;

                    while(file.exists()) {
                        duplicate++;
                        file = new File("opengl log " + date + " (" + duplicate + ").txt");
                    }
                    
                    PrintStream stream = new PrintStream(file);
                    setupDebugMessageCallback(stream);
                } catch(FileNotFoundException e) {
                    Logger.logWarning("Failed to create OpenGL log file.", e);
                }
            }
            
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
                        XJGE.noclipEnabled = !noclipEnabled;
                        
                        if(noclipEnabled) {
                            Input.setDeviceEnabled(KEY_MOUSE_COMBO, false);
                            glfwSetInputMode(Window.HANDLE, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                            viewports[0].prevCamera = viewports[0].currCamera;
                            viewports[0].currCamera = freeCam;
                        } else {
                            Input.revertEnabledState(KEY_MOUSE_COMBO);
                            glfwSetInputMode(Window.HANDLE, GLFW_CURSOR, Window.cursorMode);
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
                
                if(debugEnabled && key == GLFW_KEY_F4 && action == GLFW_PRESS) {
                    Audio.playSound(beep, null, false);
                }

                if(noclipEnabled && !terminalEnabled) {
                    if(key == GLFW_KEY_W) freeCam.pressed[0] = (action != GLFW_RELEASE);
                    if(key == GLFW_KEY_A) freeCam.pressed[1] = (action != GLFW_RELEASE);
                    if(key == GLFW_KEY_S) freeCam.pressed[2] = (action != GLFW_RELEASE);
                    if(key == GLFW_KEY_D) freeCam.pressed[3] = (action != GLFW_RELEASE);

                    freeCam.setSpeedBoostEnabled(mods == GLFW_MOD_SHIFT);
                }

                if(XJGE.terminalEnabled) terminal.processKeyInput(key, action, mods);
                else                     viewports[0].processKeyInput(key, action, mods);
            });

            glfwSetCursorPosCallback(HANDLE, (window, xpos, ypos) -> {
                float scaleX = (float) resolutionX / Window.getWidth();
                float scaleY = (float) resolutionY / Window.getHeight();
                
                cursorX = (double) (xpos * scaleX);
                cursorY = (double) Math.abs((ypos * scaleY) - resolutionY);
                
                if(noclipEnabled && !terminalEnabled) {
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
    
    /**
     * Simplified variant of the main {@link init} method that will initialize 
     * the engine using its default configuration. This method must be called 
     * before the engine can be used.
     * 
     * @param assetsFilepath the relative filepath to the folder that contains 
     *                       all of the games assets
     * @param scenesFilepath the relative filepath to the package that contains 
     *                       all of the games scene subclasses
     * @param resolution     the internal resolution the engine will display 
     *                       the game at or <b>null</b> to copy the resolution 
     *                       of the window
     */
    public static void init(String assetsFilepath, String scenesFilepath, Vector2i resolution) {
        init(assetsFilepath, scenesFilepath, resolution, false, true, true, false, false);
    }
    
    /**
     * Exposes the window to the user and starts running the applications main 
     * loop. 
     * <p>
     * NOTE: This should be called <i>after</i> setting the initial scene with 
     * {@link Game#setScene(Scene)} and supplying whatever additional 
     * {@link org.xjge.graphics.GLProgram shader programs} 
     * and {@linkplain addCommand terminal commands} the implementation 
     * requires.
     * 
     * @see Game
     * @see Window
     */
    public static void start() {
        engineCommands.putAll(userCommands);
        engineCommands.values().forEach(command -> command.setCommands(engineCommands));
        
        glPrograms = Collections.unmodifiableMap(glPrograms);
        freeCam    = new Noclip();
        terminal   = new Terminal(engineCommands, engineFont);
        debugInfo  = new DebugInfo(engineFont, engineIcons);
        
        Window.show();
        updateRenderbufferDimensions();
        setScreenSplit(getScreenSplit());
        
        glfwSetWindowSizeCallback(HANDLE, (window, w, h) -> {
            Window.updateDimensions(w, h);

            updateRenderbufferDimensions();

            setScreenSplit(getScreenSplit());
            debugInfo.updatePosition();
        });
        
        Game.loop(fbo, viewports, terminal, debugInfo, depthProgram, blurProgram);
        
        engineFont.freeTexture();
        engineIcons.freeTexture();
        beep.freeSound();
        terminal.freeBuffers();
        debugInfo.freeBuffers();
        
        //Export engine configuration.
        try {
            FileWriter file = new FileWriter("engine.cfg");
            
            try(PrintWriter output = new PrintWriter(file)) {
                output.append("<config soundMaster=\"")
                      .append(Audio.getSoundMasterVolume() + "\" ")
                      .append("musicMaster=\"")
                      .append(Audio.getMusicMasterVolume() + "\" ")
                      .append("vSync=\"")
                      .append(Hardware.getVSyncEnabled() + "\" ")
                      .append("fullscreen=\"")
                      .append(Window.getFullscreen() + "\">")
                      .append("</config>");
            }
        } catch(IOException e) {
            Logger.setDomain("core");
            Logger.logWarning("Failed to export engine configuration.", e);
            Logger.setDomain(null);
        }
        
        Hardware.freeSpeakers();
        Input.exportControls();
        GL.destroy();
        glfwTerminate();
    }
    
    /**
     * Generates a new renderbuffer object and attaches it to the engines 
     * framebuffer. 
     */
    private static void createRenderbuffer() {
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        int rbo = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rbo);
        
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, resolutionX, resolutionY);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rbo);
        
        ErrorUtils.checkGLError();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    
    /**
     * Sets the width/height dimensions of the renderbuffer in accordance to 
     * the engines internal resolution (in pixels).
     */
    private static void updateRenderbufferDimensions() {
        if(matchWindowResolution && Window.getWidth() != 0 && Window.getHeight() != 0) {
            resolutionX = Window.getWidth();
            resolutionY = Window.getHeight();

            createRenderbuffer();
        }
    }
    
    /**
     * Obtains the current status of the viewport used to indicate its 
     * visibility.
     * 
     * @param viewportID the ID number of the viewport to query
     * 
     * @return true if the viewport of the specified ID number is currently 
     *         visible
     */
    static boolean getViewportActive(int viewportID) {
        return viewports[viewportID].active;
    }
    
    /**
     * Restricts an input value from a user to one between the minimum and 
     * maximum ranges specified.
     * 
     * @param minValue  the minimum permitted value
     * @param maxValue  the maximum permitted value
     * @param userValue the value entered by the user
     * 
     * @return a value between desired minimum and maximum ranges
     */
    public static float clampValue(float minValue, float maxValue, float userValue) {
        float result = 0;
        
        if(userValue > maxValue) {
            result = maxValue;
        } else if(userValue < minValue) {
            result = minValue;
        } else {
            result = userValue;
        }
        
        return result;
    }
    
    /**
     * Adds a custom {@link GLProgram} to an immutable collection which can be 
     * accessed through a scenes {@linkplain Scene#render render()} method to 
     * draw the various objects and entities within it.
     * 
     * @param name      the name that will be used to refer to the program
     * @param glProgram the object representing the compiled shader program
     */
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
    
    /**
     * Adds a new user-defined command that can be accessed through the engines 
     * {@link Terminal} and used to debug the application at runtime.
     * 
     * @param name    the name the terminal will use to refer to the command
     * @param command an object used to organize the commands internal logic
     */
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
    
    /**
     * Adds a new {@link Widget} to the specified viewport. Widgets will be 
     * rendered in the order of their z-positions with lower numbers denoting a 
     * higher priority. For example, a component with a z-position of 0 will be 
     * rendered in front of a component with a z-position of 1.
     * 
     * @param viewportID the ID number of the viewport to add the widget to
     * @param name       the name that will be used to identify and remove the 
     *                   widget later
     * @param widget     the widget object to add
     * 
     * @see Viewport
     */
    public static final void addUIWidget(int viewportID, String name, Widget widget) {
        switch(viewportID) {
            case 0, 1, 2, 3 -> viewports[viewportID].addUIWidget(name, widget);
        }
    }
    
    /**
     * Removes a widget from the specified viewports user interface.
     * 
     * @param viewportID the ID number of the viewport to remove the widget 
     *                   from
     * @param name       the name of the widget to remove
     * 
     * @see Viewport
     * @see Widget
     */
    public static final void removeUIWidget(int viewportID, String name) {
        switch(viewportID) {
            case 0, 1, 2, 3 -> viewports[viewportID].removeUIWidget(name);
        }
    }
    
    /**
     * Removes every widget currently attached to the specified viewport.
     * 
     * @param viewportID the ID number of the viewport that will clear its UI 
     *                   of all widgets
     */
    public static final void clearWidgets(int viewportID) {
        viewports[viewportID].clearWidgets();
    }
    
    /**
     * Obtains the width of the engines internal resolution (if one exists).
     * 
     * @return the width of the framebuffer texture being rendered
     */
    public static int getResolutionX() {
        return resolutionX;
    }
    
    /**
     * Obtains the height of the engines internal resolution (if one exists).
     * 
     * @return the height of the framebuffer texture being rendered
     */
    public static int getResolutionY() {
        return resolutionY;
    }
    
    /**
     * Obtains the current position of the mouse cursor within the game windows 
     * content area.
     * <p>
     * NOTE: If the engine was supplied with a resolution during 
     * {@linkplain #init initialization} the value returned by this method will
     * be converted to correspond to the coordinate system of the resolution.
     * 
     * @return the current x-coordinate of the mouse cursor
     */
    public static final double getCursorPosX() {
        return cursorX;
    }
    
    /**
     * Obtains the current position of the mouse cursor within the game windows 
     * content area.
     * <p>
     * NOTE: If the engine was supplied with a resolution during 
     * {@linkplain #init initialization} the value returned by this method will
     * be converted to correspond to the coordinate system of the resolution.
     * 
     * @return the current y-coordinate of the mouse cursor
     */
    public static final double getCursorPosY() {
        return cursorY;
    }
    
    /**
     * Obtains the value that will be used to further cull a monitors supported 
     * video modes.
     * 
     * @return true if 4K resolutions are not permitted for use by the engine
     */
    static boolean get4KRestricted() {
        return restrict4KResolutions;
    }
    
    /**
     * Obtains the value that indicates whether or not the command terminal is 
     * currently open.
     * 
     * @return true if the debug mode is enabled and the terminal is open
     */
    static boolean getTerminalEnabled() {
        return terminalEnabled;
    }
    
    /**
     * Obtains a value that determines if the positions of sources of light 
     * should be exposed.
     * 
     * @return if true, icons indicating the color and locations of the light 
     *         sources will be rendered
     */
    static boolean getLightSourcesVisible() {
        return showLightSources;
    }
    
    /**
     * Obtains the current split value used to divide the screen.
     * 
     * @return a value indicating how the screen is being divided
     */
    public static Split getScreenSplit() {
        return split;
    }
    
    /**
     * Obtains a user-provided string denoting the location of the sounds, 
     * images, .glsl files, and whatever other assets are used by the 
     * implementation.
     * 
     * @return a filepath to the location containing the games assets
     */
    public static String getAssetsFilepath() {
        return assetsFilepath;
    }
    
    /**
     * Obtains a user-provided string that tells the engine where to search for 
     * implementations of the {@link Scene} superclass.
     * 
     * @return a filepath to the location containing the games scene subclasses
     */
    public static String getScenesFilepath() {
        return scenesFilepath;
    }
    
    /**
     * Provides convenient access to the engines default shader program.
     * 
     * @return a shader program that can be used to render most objects
     */
    public static GLProgram getDefaultGLProgram() {
        return glPrograms.get("default");
    }
    
    /**
     * Sets the current split value the engine will use to divide the screen 
     * during split screen mode.
     * 
     * @param split a value that determines how the screen will be divided. One 
     *              of: 
     * <table><caption></caption><tr>
     * <td>{@link Split#NONE NONE}</td><td>{@link Split#HORIZONTAL HORIZONTAL}</td>
     * <td>{@link Split#VERTICAL VERTICAL}</td></tr><tr>
     * <td>{@link Split#TRISECT TRISECT}</td><td>{@link Split#QUARTER QUARTER}</td>
     * </table>
     */
    public static final void setScreenSplit(Split split) {
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
                        
                        default -> viewport.setBounds(resolutionX, resolutionY, 0, 0, 0, 0);
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
                        
                        default -> viewport.setBounds(resolutionX, resolutionY, 0, 0, 0, 0);
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
                        
                        default -> viewport.setBounds(resolutionX, resolutionY, 0, 0, 0, 0);
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
    
    /**
     * Sets the current camera object a viewport will use.
     * 
     * @param viewportID the ID number of the viewport whos camera we want to 
     *                   set
     * @param camera     the camera object being assigned to the viewport
     */
    public static final void setViewportCamera(int viewportID, Camera camera) {
        Logger.setDomain("core");
        
        if(camera == null) {
            Logger.logInfo("Failed to set viewport camera. Null is not " + 
                           "accepted as a value of this function.");
        } else if(viewportID < GLFW_JOYSTICK_1 || viewportID > GLFW_JOYSTICK_4) {
            Logger.logInfo("Failed to set viewport camera. No viewport "+ 
                           "by the ID of " + viewportID + " exists.");
        } else {
            if(viewports[viewportID].currCamera == freeCam) {
                //Added in case noclip is enabled when this was called.
                viewports[viewportID].prevCamera = camera;
            } else {
                viewports[viewportID].currCamera = camera;
            }
        }
        
        Logger.setDomain(null);
    }
    
    /**
     * Sets the speed at which the camera will move while noclip mode is 
     * enabled. This includes base speed and boost speed.
     * <p>
     * More specifically, the factor number provided here will influence the 
     * base speed of the camera. That is, lower values will make the camera 
     * move slower, whereas higher values will make it move faster. By default
     * the cameras speed factor is 1.
     * 
     * @param factor the value that will effect the cameras movement speed
     */
    public static void setNoclipSpeedFactor(float factor) {
        noclipSpeedFactor = clampValue(0, Float.MAX_VALUE, factor);
    }
    
}