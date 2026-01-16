package org.xjge.core;

import org.xjge.graphics.Texture;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.GLShader;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeMap;
import java.util.regex.Pattern;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL32.*;
import org.xjge.graphics.Color;
import org.xjge.graphics.PostProcessShader;

/**
 * Created: Apr 28, 2021
 * <br><br>
 * Represents the game engine in its broadest sense and provides a point at 
 * which its functionality may be extended to better suit the individual 
 * requirements of the implementation.
 * <p>
 * More specifically this class provides the following features:
 * <ul>
 * <li>The ability to change how the screen will be 
 *     {@linkplain Window#setResolution(int, int) divided} during split screen mode.</li>
 * <li>Convenient access to the {@linkplain getDefaultGLProgram default shader 
 *     program} used internally by the engine.</li>
 * <li>The ability to provide the engine with supplemental {@linkplain 
 *     addCommand terminal commands} and 
 *     {@linkplain addGLProgram shader programs}.</li>
 * <li>Control over which {@link Camera} object each viewport will use to 
 *     display the scene from their perspective.</li>
 * <li>The ability to {@link UIManager#addWidget add} and {@link UIManager#removeWidget(int, java.lang.String) remove} UIManager widgets from viewports.</li>
 * </ul>
 * <p>
 * Before the engines features can be used {@link init init()} must be called 
 * followed by changing whichever settings the implementation needs to before 
 * exposing the game window with {@link start start()}.
 * 
 * @author J Hoffman
 * @since  2.0.0
 * 
 * @see InputSystem
 * @see Monitor
 * @see Logger
 * @see Terminal
 * @see Viewport
 * @see Window
 */
public final class XJGE {
    
    private static boolean showLightSources;
    
    static GLProgram depthProgram;
    static GLProgram blurProgram;
    static Map<String, GLProgram> glPrograms = new HashMap<>();
    
    
    //==== LEGACY FIELDS ABOVE =================================================
    
    private static boolean initialized;
    private static boolean debugModeEnabled;
    private static boolean started;
    private static boolean ticked;
    public static boolean enableBloom;
    
    private static float bloomThreshold = 1.0f;
    
    private static double deltaMetric = 0;
    
    private static int fps;
    private static int entityCount;
    private static int tickCount    = 0;
    final static int TICKS_PER_HOUR = 225000;
    
    public static final String VERSION = "4.0.0-beta4";
    private static String scenesPackage;
    private static String cpuModel;
    
    private static Texture engineIcons;
    
    private static TreeMap<String, TerminalCommand> engineCommands     = new TreeMap<>();
    private static final TreeMap<String, TerminalCommand> userCommands = new TreeMap<>();
    
    private static Color clearColor = new Color(119, 136, 255);
    private static Scene scene;
    
    private static final Observable observable = new Observable(XJGE.class);
    
    private static final Queue<Scene> sceneChangeRequests = new LinkedList<>();
    private static final Queue<Event> events = new PriorityQueue<>(Comparator.comparing(Event::getPriority));
    
    static {
        ProcessBuilder builder = new ProcessBuilder(
            "powershell.exe",
            "-NoProfile",
            "-Command",
            "(Get-CimInstance Win32_Processor).Name"
        );
        
        builder.redirectErrorStream(true);
        
        try {
            Process process = builder.start();
            
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                
                while((line = reader.readLine()) != null) {
                    line = line.trim();
                    if(!line.isEmpty()) cpuModel = line.trim();
                }
            } finally {
                process.destroy();
            }
        } catch(IOException exception) {
            Logger.logWarning("Failed to parse CPU model", exception);
            cpuModel = "Unknown";
        }
    }
    
    /**
     * Default constructor defined here to keep it out of the public APIs reach.
     */
    private XJGE() {}
    
    /**
     * 
     * @param debugModeEnabled if true, additional developer tools will be available for use
     * @param assetsFolderFilepath the absolute filepath to wherever the games assets are stored
     * @param scenesPackage the relative package path containing the game scene subclasses
     */
    public static void init(boolean debugModeEnabled, String assetsFolderFilepath, String scenesPackage) {
        if(initialized) {
            Logger.logInfo("XJGE has already been initialized");
            return;
        }
        
        XJGE.debugModeEnabled = debugModeEnabled;
        
        if(XJGE.debugModeEnabled) {
            AssetSourceExternal externalSource = null;
            
            try {
                externalSource = new AssetSourceExternal(Paths.get(assetsFolderFilepath));
                AssetManager.addSource(externalSource);
            } catch(IOException exception) {
                Logger.logError("Failed to add external asset source: \"" + assetsFolderFilepath + "\"", exception);
            }
            
            if(externalSource != null) {
                externalSource.addChangeListener(filepath -> {
                    Pattern tempFile = Pattern.compile(".*(_tmp\\d+|~\\$).*");
                    String filename  = filepath.getFileName().toString();
                    if(!tempFile.matcher(filename).matches()) AssetManager.queueReload(filename);
                });
            }
        } else {
            AssetManager.addSource(new AssetSourceInternal(XJGE.class.getClassLoader()));
        }
        
        /*
        NOTE: For engine testing the external asset source should NOT be set to org/xjge/assets otherwise the engine will mistake
        those files for engine assets.
        */
        AssetManager.addSource(new AssetSourceEngine(XJGE.class.getClassLoader()));
        
        observable.properties.put("XJGE_SCENE_CHANGED", scene);
        
        String javaVersion = System.getProperty("java.version");
        
        if(javaVersion.compareTo("22.0.2") < 0) {
            Logger.logError("Unsupported Java version used (found: " + javaVersion + " required: 22.0.2)", null);
        }
        
        if(!glfwInit()) Logger.logError("Failed to initialize GLFW", null);
        
        Window.create();
        
        //Initialize the default shader program that will be provided to the implementation
        {
            var shaderSourceFiles = new LinkedList<GLShader>() {{
                add(GLShader.load("xjge_shader_default_vertex.glsl", GL_VERTEX_SHADER));
                add(GLShader.load("xjge_shader_default_fragment.glsl", GL_FRAGMENT_SHADER));
            }};

            GLProgram defaultProgram = new GLProgram(shaderSourceFiles, "default");
            glPrograms.put("default", defaultProgram);
        }

        //Create shader program that will generate shadow map output
        {
            var shaderSourceFiles = new LinkedList<GLShader>() {{
                add(GLShader.load("xjge_shader_depth_vertex.glsl", GL_VERTEX_SHADER));
                add(GLShader.load("xjge_shader_depth_fragment.glsl", GL_FRAGMENT_SHADER));
            }};

            depthProgram = new GLProgram(shaderSourceFiles, "default");
        }

        //Create shader program for applying gaussian blur
        {
            var shaderSourceFiles = new LinkedList<GLShader>() {{
                add(GLShader.load("xjge_shader_blur_vertex.glsl", GL_VERTEX_SHADER));
                add(GLShader.load("xjge_shader_blur_fragment.glsl", GL_FRAGMENT_SHADER));
            }};

            blurProgram = new GLProgram(shaderSourceFiles, "default");
        }
        
        engineIcons = Texture.load("xjge_texture_icons.png");
        Light.setIconTexture(engineIcons);
        
        engineCommands = new TreeMap<>() {{
            put("help",                 new TCHelp());
            put("listCommands",         new TCListCommands());
            put("listMonitors",         new TCListMonitors());
            put("listSpeakers",         new TCListSpeakers());
            put("reloadAsset",          new TCReloadAsset());
            put("runGarbageCollection", new TCRunGarbageCollection());
            put("setFullscreen",        new TCSetFullscreen());
            put("setMonitor",           new TCSetMonitor());
            put("setScene",             new TCSetScene());
            put("setSplitScreenValue",  new TCSetSplitScreenValue());
            put("setSpeaker",           new TCSetSpeaker());
            put("setVSyncEnabled",      new TCSetVSyncEnabled());
            put("setVideoMode",         new TCSetVideoMode());
            put("terminate",            new TCTerminate());
        }};
        
        XJGE.scenesPackage = scenesPackage;
        
        AudioSystem.init();
        InputSystem.init();
        
        Logger.logSystemInfo();
        
        initialized = true;
    }
    
    /**
     * Exposes the window to the user and starts running the applications main 
     * loop.
     * <p>
     * NOTE: This should be called <i>after</i> setting the initial scene with 
     * {@link XJGE#setScene(Scene)} and supplying whatever additional 
     * {@link org.xjge.graphics.GLProgram shader programs} 
     * and {@linkplain addCommand terminal commands} the implementation 
     * requires.
     * 
     * @see Window
     */
    public static void start() {
        if(started) {
            Logger.logInfo("The game loop has already been started");
            return;
        }
        
        engineCommands.putAll(userCommands);
        engineCommands.values().forEach(command -> command.setCommands(engineCommands));
        
        started    = true;
        glPrograms = Collections.unmodifiableMap(glPrograms);
        
        Window.registerCallbacks(engineCommands, engineIcons);
        Window.show();
        
        int cycles = 0;
        final double TARGET_DELTA = 1 / 60.0;
        double prevTime = glfwGetTime();
        double currTime;
        double delta = 0;
        Matrix4f projMatrix = new Matrix4f();
        
        while(!Window.closeRequested()) {
            glfwPollEvents();
            
            currTime = glfwGetTime();
            delta    += currTime - prevTime;
            if(delta < TARGET_DELTA && Window.getVSyncEnabled()) delta = TARGET_DELTA;
            prevTime = currTime;
            ticked   = false;
            
            if(debugModeEnabled) AssetManager.processReloadRequests();
            
            while(delta >= TARGET_DELTA) {
                InputSystem.update(TARGET_DELTA, deltaMetric);
                Window.update(deltaMetric, fps, entityCount);
                
                if(tick(20)) {
                    deltaMetric = delta;
                    if(scene != null) {
                        //entityCount = scene.entities.size();
                    }
                }
                
                delta     -= TARGET_DELTA;
                ticked    = true;
                tickCount = (tickCount == TICKS_PER_HOUR) ? 0 : tickCount + 1;
                
                //Resolve scene change requests
                if(!sceneChangeRequests.isEmpty()) {
                    if(scene != null) scene.exit();
                    scene = sceneChangeRequests.poll();
                    Logger.logInfo("Current scene changed to \"" + scene.name + "\"");
                    observable.notifyObservers("SCENE_CHANGED", scene);
                }
                
                //Process any unresolved events otherwise update the scene normally
                if(!events.isEmpty()) {
                    Event event = events.peek();
                    if(!event.resolved) event.resolve();
                    else                events.poll();
                } else {
                    scene.processAddRequests();
                    scene.update(TARGET_DELTA, deltaMetric);
                    scene.updateLightSources();
                    scene.processRemoveRequests();
                }
                
                //Add new widget to a viewport asynchronously
                UIManager.processWidgetAddRequests();
                
                //Update viewport cameras and UIManager widgets
                for(Viewport viewport : Window.getViewports()) {
                    if(viewport.active && viewport.currCamera != null) {
                        viewport.currCamera.update(TARGET_DELTA, delta);
                        UIManager.updateWidgets(viewport.id, TARGET_DELTA, delta);
                        AudioSystem.captureViewportCameraData(viewport.id, viewport.currCamera);
                    }
                }
                
                UIManager.processWidgetRemoveRequests();
                AudioSystem.update();
                
                if(tick(60)) {
                    fps    = cycles;
                    cycles = 0;
                }
            }
            
            XJGE.getDefaultGLProgram().use();
            XJGE.getDefaultGLProgram().setUniform("uBloomThreshold", bloomThreshold);
            scene.setShadowUniforms();
            scene.setLightingUniforms();
            
            //Render scene from the perspective of each active viewport
            for(Viewport viewport : Window.getViewports()) {
                if(viewport.active) {
                    scene.renderShadowMap(viewport.currCamera.up, depthProgram);
                    
                    if(viewport.id == 0) {
                        glClearColor(0, 0, 0, 0);
                        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                    }
                    
                    glBindFramebuffer(GL_FRAMEBUFFER, Window.getFBOHandle());
                        glViewport(0, 0, viewport.width, viewport.height);
                        glClearColor(clearColor.getRed(), clearColor.getGreen(), clearColor.getBlue(), 0);
                        viewport.bindDrawBuffers(enableBloom);
                        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                        
                        viewport.resetCamera(glPrograms);
                        
                        viewport.render(glPrograms, "camera", projMatrix);
                        scene.renderSkybox(viewport.currCamera.viewMatrix);
                        scene.render(glPrograms, viewport.id, viewport.currCamera);
                        scene.renderLightSources(viewport.currCamera);
                    glBindFramebuffer(GL_FRAMEBUFFER, 0);
                    
                    projMatrix.setOrtho(viewport.width, 0, 0, viewport.height, 0, 1);
                    
                    if(enableBloom) {
                        blurProgram.use();
                        blurProgram.setUniform("uProjection", false, projMatrix);
                        viewport.applyBloom(blurProgram);
                    }
                    
                    glViewport(viewport.botLeft.x, viewport.botLeft.y, viewport.topRight.x, viewport.topRight.y);
                    
                    viewport.resetCamera(glPrograms);
                    
                    viewport.render(glPrograms, "texture", projMatrix);
                    viewport.render(glPrograms, "ui", projMatrix);
                }
            }
            
            Window.swapBuffers();
            
            if(!ticked) {
                try {
                    Thread.sleep(1);
                } catch(InterruptedException e) {
                    Logger.logError(e.getMessage(), e);
                }
            } else {
                cycles++;
            }
        }
        
        Window.freeCallbacks();
        AudioSystem.freeResources();
        InputSystem.freeDevices();
        GL.destroy();
        glfwTerminate();
    }
    
    /**
     * Adds a custom {@link GLProgram} to an immutable collection which can be 
     * accessed through a scenes {@linkplain Scene#render render()} method to 
     * draw the various objects and entities within it.
     * 
     * @param name the name that will be used to refer to the program
     * @param glProgram the object representing the compiled shader program
     */
    public static void addGLProgram(String name, GLProgram glProgram) {
        if(!name.equals("default")) {
            glPrograms.put(name, glProgram);
        } else {
            Logger.logWarning("Failed to add program \"" + name + "\". This " + 
                              " name is reserved for engine use, please choose another", 
                              null);
        }
    }
    
    /**
     * Adds a new user-defined command that can be accessed through the engines 
     * {@link Terminal} and used to debug the application at runtime.
     * 
     * @param name the name the terminal will use to refer to the command
     * @param command an object used to organize the commands internal logic
     */
    public static void addCommand(String name, TerminalCommand command) {
        if(engineCommands.containsKey(name)) {
            Logger.logWarning("Failed to add command \"" + name + "\". A command " + 
                              "by this name already exists as a part of the engines " +
                              "core features", 
                              null);
        } else {
            userCommands.put(name, command);
        }
    }
    
    /**
     * Adds an event to the game event queue. Events are processed in the order 
     * of their priority. As such, events are not guaranteed to be executed in 
     * the order from which calls to this method are made.
     * 
     * @param event the event to queue
     */
    public static final void addEvent(Event event) {
        events.add(event);
    }
    
    public static void addObserver(PropertyChangeListener observer) {
        observable.addObserver(observer);
    }
    
    public static void removeObserver(PropertyChangeListener observer) {
        observable.removeObserver(observer);
    }
    
    /**
     * Changes the color OpenGL will use to clear color buffers. Often used to 
     * set background or sky colors.
     * 
     * @param color the color empty space will be filled with
     */
    public static void setClearColor(Color color) {
        if(color == null) {
            Logger.logInfo("The value of the color object may not be null");
            return;
        }
        
        clearColor = color;
    }
    
    /**
     * Places a request to change the currently rendered scene.
     * 
     * @param scene the new scene to enter
     */
    public static void setScene(Scene scene) {
        if(scene == null) {
            Logger.logInfo("the value of the scene object may not be null");
            return;
        }
        
        sceneChangeRequests.add(scene);
    }
    
    public static boolean debugModeEnabled() {
        return debugModeEnabled;
    }
    
    /**
     * Returns true anytime the specified number of update iterations (or cycles) 
     * have been reached. Intended to be used in for game systems that don't 
     * require the precision of the {@link Timer} or {@link StopWatch} classes.
     * 
     * @param speed the number of cycles to wait until the next tick will occur
     * 
     * @return true every time the specified number of cycles has been reached
     */
    public static boolean tick(int speed) {
        return tickCount % speed == 0;
    }
    
    /**
     * Obtains the total number of cycles that the engines update loop has 
     * completed since the start of the application.
     * <p>
     * NOTE: The tick count will roll over to zero every hour, with one hour 
     * being equivalent to approximately 3,600,000 ticks. Gameplay systems that
     * require durations longer than this should consider instead utilizing the 
     * {@link Timer} class.
     * 
     * @return the number of cycles (or ticks) that have elapsed
     */
    public static int getTickCount() {
        return tickCount;
    }
    
    public static final String getCPUModel() {
        return cpuModel;
    }
    
    //==== LEGACY API BELOW ====================================================
    
    
    
    
    /**
     * Obtains the display name of the current Scene being rendered.
     * 
     * @return the name of the current scene
     */
    public static String getSceneName() {
        return scene.name;
    }
    
    /**
     * Inserts a light object into the current scenes 
     * {@linkplain Scene#lights lights array} at the specified index. This 
     * method is particularly useful in instances where lighting effects need 
     * to exhibit some level of dynamic behavior- such as an explosion in a 
     * dark tunnel emitting light for a brief period of time, etc.
     * 
     * @param index the index in the array to place the light object at
     * @param light the light object to add
     */
    public static void addLight(int index, Light light) {
        try {
            scene.lights[index] = light;
        } catch(IndexOutOfBoundsException e) {
            Logger.logWarning("Failed to add light at index " + index, e);
        }
    }
    
    /**
     * Specifies the value which will be used to indicate how bright the surface 
     * of objects must be before the bloom effect is applied to it. The lower 
     * the brightness threshold, the more abundant bloom will be.
     * 
     * @param value a number between 0 and 10 that the brightness of a surface
     *              will need to exceed
     */
    public static void setBloomThreshold(float value) {
        bloomThreshold = XJGE.clampValue(0f, 10f, value);
    }
    
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
     * @param resolution the internal resolution the engine will display the game 
     *                   at or <b>null</b> to copy the resolution of the window
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
    }
    
    /**
     * Restricts an input value from a user to one between the minimum and 
     * maximum ranges specified.
     * 
     * @param minValue the minimum permitted value
     * @param maxValue the maximum permitted value
     * @param userValue the value entered by the user
     * 
     * @return a value between desired minimum and maximum ranges
     */
    public static float clampValue(float minValue, float maxValue, float userValue) {
        return Math.max(minValue, Math.min(maxValue, userValue));
    }
    
    /**
     * Restricts an input value from a user to one between the minimum and 
     * maximum ranges specified.
     * 
     * @param minValue the minimum permitted value
     * @param maxValue the maximum permitted value
     * @param userValue the value entered by the user
     * 
     * @return a value between desired minimum and maximum ranges
     */
    public static int clampValue(int minValue, int maxValue, int userValue) {
        return Math.max(minValue, Math.min(maxValue, userValue));
    }
    
    /**
     * Performs a linear interpolation between two floating-point numbers and 
     * returns the result. This is often useful in instances where you need to 
     * gradually transition between two values such as player movement speed.
     * 
     * @param startValue the initial value to evaluate
     * @param targetValue the target value to reach
     * @param factor the floating-point interpolation factor (between 0 and 1)
     * 
     * @return the linearly-interpolated value between the starting and target 
     *         numbers
     */
    public static float lerp(float startValue, float targetValue, float factor) {
        return (1 - clampValue(0, 1, factor)) * startValue + clampValue(0, 1, factor) * targetValue;
    }
    
    /**
     * Applies post-processing effects to the desired viewport by changing 
     * which shader program its framebuffer object will use during rendering.
     * 
     * @param viewportID the ID number of the viewport to apply the filter to
     * @param postProcessShader an object containing the custom shader program 
     *                          to use or null to use the engines default shaders
     */
    public static final void usePostProcessShader(int viewportID, PostProcessShader postProcessShader) {
        //viewports[viewportID].postProcessShader = postProcessShader;
    }
    
    /**
     * Changes the filter type applied to the framebuffer texture of a viewport.
     * 
     * @param viewportID the ID number of the viewport who's framebuffer texture 
     *                   we want to change
     * @param useLinearFilter if true, the textures will be filtered without hard edges
     */
    public static final void changeFramebufferFilter(int viewportID, boolean useLinearFilter) {
        /*
        glBindTexture(GL_TEXTURE_2D, viewports[viewportID].viewTexHandle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, (useLinearFilter) ? GL_LINEAR : GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, (useLinearFilter) ? GL_LINEAR : GL_NEAREST);
        glBindTexture(GL_TEXTURE_2D, 0);
        */
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
     * Obtains a user-provided string that tells the engine where to search for 
     * implementations of the {@link Scene} superclass.
     * 
     * @return a filepath to the location containing the games scene subclasses
     */
    public static String getScenesPackage() {
        return scenesPackage;
    }
    
    /**
     * Provides convenient access to the engines default shader program.
     * 
     * @return a shader program that can be used to render most objects
     */
    public static GLProgram getDefaultGLProgram() {
        return glPrograms.get("default");
    }
    
}