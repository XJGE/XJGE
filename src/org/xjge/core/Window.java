package org.xjge.core;

import org.xjge.graphics.Texture;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Collections;
import java.util.NavigableMap;
import java.util.TreeMap;
import org.lwjgl.PointerBuffer;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMonitorCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowIconifyCallback;
import org.lwjgl.glfw.GLFWWindowPosCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.STBI_rgb_alpha;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.xjge.core.SplitScreenType.*;

/**
 * Created: Apr 29, 2021
 * 
 * <p>Provides a single point of access through which the applications window 
 * can be adjusted during runtime.</p>
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Window {
    
    private static boolean fullscreen;
    private static boolean resizable;
    private static boolean minimized;
    private static boolean restrict4K   = true;
    private static boolean vSyncEnabled = true;
    
    public static final int DEFAULT_WIDTH  = 1280;
    public static final int DEFAULT_HEIGHT = 720;
    
    private static int cursorInputMode = GLFW_CURSOR_NORMAL;
    private static int minWidth  = 640;
    private static int minHeight = 480;
    private static int width     = DEFAULT_WIDTH;
    private static int height    = DEFAULT_HEIGHT;
    private static int positionX;
    private static int positionY;
    private static int fboHandle;
    
    private static long handle = NULL;
    
    private static Terminal terminal;
    private static EngineMetrics metrics;
    private static Noclip noclip;
    
    private static Monitor monitor;
    private static String title = "XJGE v" + XJGE.VERSION;
    private static final Keyboard keyboard = new Keyboard();
    private static final Mouse mouse       = new Mouse();
    private static final Resolution resolution = new Resolution(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    private static final Observable observable = new Observable(Window.class);
    
    //These fields were added to prevent issues with GC invalidating callbacks
    private static GLFWErrorCallback         glfwErrorReference;
    private static GLFWMonitorCallback       glfwMonitorReference;
    private static GLFWWindowIconifyCallback glfwWindowIconifyReference;
    private static GLFWWindowSizeCallback    glfwWindowSizeReference;
    private static GLFWWindowPosCallback     glfwWindowPositionReference;
    private static GLFWCursorPosCallback     glfwCursorPositionReference;
    private static GLFWMouseButtonCallback   glfwMouseButtonReference;
    private static GLFWScrollCallback        glfwScrollReference;
    private static GLFWKeyCallback           glfwKeyReference;
    
    private static final Viewport[] viewports = new Viewport[4];
    
    private static SplitScreenType splitType = SplitScreenType.NONE;
    
    private static final NavigableMap<Integer, Monitor> monitors = new TreeMap<>();
    
    /**
     * Default constructor defined here to keep it out of the public APIs reach.
     */
    private Window() {}
    
    /**
     * Internally creates a renderbuffer for offscreen or multisample rendering.
     * Called automatically during window creation and resolution changes.
     */
    private static void createRenderbuffer() {
        glBindFramebuffer(GL_FRAMEBUFFER, fboHandle);
        int rboHandle = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rboHandle);
        
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, resolution.width, resolution.height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboHandle);
        
        ErrorUtils.checkGLError();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    
    /**
     * Adapts the window to the current monitors video mode. Called automatically
     * after applying major state changes.
     */
    static void reconfigure() {
        if(fullscreen) {
            width  = monitor.getWidth();
            height = monitor.getHeight();
            
            observable.notifyObservers("WINDOW_WIDTH_CHANGED",  width);
            observable.notifyObservers("WINDOW_HEIGHT_CHANGED", height);
            
            glfwSetWindowMonitor(handle, monitor.handle, positionX, positionY, 
                                 monitor.getWidth(), monitor.getHeight(), monitor.getRefreshRate());
        } else {
            glfwSetWindowMonitor(handle, NULL, positionX, positionY, 
                                 width, height, monitor.getRefreshRate());
        }
        
        center(monitor);
        glfwSwapInterval(vSyncEnabled ? 1 : 0);
    }
    
    /**
     * Initializes the applications window, framebuffer, and viewports.
     * 
     * @param debugModeEnabled if true, additional developer tools will be available for use
     */
    static void create() {
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_FOCUS_ON_SHOW, GLFW_TRUE);
        
        //Register observable properties for public API use
        observable.properties.put("WINDOW_FULLSCREEN_CHANGED",        fullscreen);
        observable.properties.put("WINDOW_RESIZABLE_CHANGED",         resizable);
        observable.properties.put("WINDOW_MINIMIZE_VALUE_CHANGED",    minimized);
        observable.properties.put("WINDOW_MINIMUM_WIDTH_CHANGED",     minWidth);
        observable.properties.put("WINDOW_MINIMUM_HEIGHT_CHANGED",    minHeight);
        observable.properties.put("WINDOW_WIDTH_CHANGED",             width);
        observable.properties.put("WINDOW_HEIGHT_CHANGED",            height);
        observable.properties.put("WINDOW_POSITION_X_CHANGED",        positionX);
        observable.properties.put("WINDOW_POSITION_Y_CHANGED",        positionY);
        observable.properties.put("WINDOW_RESOLUTION_WIDTH_CHANGED",  resolution.width);
        observable.properties.put("WINDOW_RESOLUTION_HEIGHT_CHANGED", resolution.height);
        observable.properties.put("WINDOW_TITLE_CHANGED",             title);
        observable.properties.put("WINDOW_MONITOR_CHANGED",           monitor);
        observable.properties.put("WINDOW_SPLITSCREEN_TYPE_CHANGED",  splitType);
        
        monitor = findMonitors().get(0); //Obtain primary monitor
        handle  = glfwCreateWindow(width, height, title, NULL, NULL);
        
        glfwSetWindowSizeLimits(handle, minWidth, minHeight, GLFW_DONT_CARE, GLFW_DONT_CARE);
        glfwMakeContextCurrent(handle);
        GL.createCapabilities();
        reconfigure();
        
        for(int i = 0; i < viewports.length; i++) viewports[i] = new Viewport(i, resolution);
        
        fboHandle = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboHandle);
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
        setSplitScreenType(splitType);
    }
    
    /**
     * Registers the windows callback functions. These fire anytime an event is
     * captured from the OS.
     * 
     * @param terminal the command-line terminal used to make state changes during runtime
     * @param metrics a series of collapsible menus containing debug information
     */
    static void registerCallbacks(TreeMap<String, TerminalCommand> engineCommands, Texture engineIcons) {
        noclip   = new Noclip();
        terminal = new Terminal(engineCommands);
        metrics  = new EngineMetrics(engineIcons);
        
        glfwErrorReference = GLFWErrorCallback.create((error, description) -> {
            Logger.logWarning("GLFW Error: (" + error + ") " + GLFWErrorCallback.getDescription(description), null);
        });
        
        glfwMonitorReference = GLFWMonitorCallback.create((monitorHandle, event) -> {
            if(event == GLFW_DISCONNECTED) {
                var eventMonitor = getMonitor(monitorHandle);
                
                if(eventMonitor != null) {
                    Logger.logInfo("The monitor \"" + eventMonitor.name + "\" at index " + 
                                   eventMonitor.index + " has been disconnected");
                }
            }
            
            findMonitors();
            
            if(event == GLFW_CONNECTED) {
                var eventMonitor = getMonitor(monitorHandle);
                
                if(eventMonitor != null) {
                    Logger.logInfo("New monitor \"" + eventMonitor.name + "\" connected at index " + eventMonitor.index);
                }
            }
        });
        
        glfwWindowIconifyReference = GLFWWindowIconifyCallback.create((window, iconified) -> {
            minimized = iconified;
            observable.notifyObservers("WINDOW_MINIMIZE_VALUE_CHANGED", minimized);
        });
        
        glfwWindowSizeReference = GLFWWindowSizeCallback.create((window, newWidth, newHeight) -> {
            if(XJGE.debugModeEnabled()) terminal.relocate(newWidth, newHeight);
            width  = newWidth;
            height = newHeight;
            observable.notifyObservers("WINDOW_WIDTH_CHANGED",  width);
            observable.notifyObservers("WINDOW_HEIGHT_CHANGED", height);
        });
        
        glfwWindowPositionReference = GLFWWindowPosCallback.create((window, newPositionX, newPositionY) -> {
            positionX = newPositionX;
            positionY = newPositionY;
            observable.notifyObservers("WINDOW_POSITION_X_CHANGED", positionX);
            observable.notifyObservers("WINDOW_POSITION_Y_CHANGED", positionY);
        });
        
        glfwCursorPositionReference = GLFWCursorPosCallback.create((window, cursorPositionX, cursorPositionY) -> {
            if(!terminal.show && !metrics.show && !noclip.enabled) {
                float scaleX = (float) resolution.width / width;
                float scaleY = (float) resolution.height / height;

                mouse.cursorPositionX = cursorPositionX * scaleX;
                mouse.cursorPositionY = resolution.height - Math.abs((cursorPositionY * scaleY));

                UIManager.processMouseInput(mouse);
            } else {
                mouse.cursorPositionX = cursorPositionX;
                mouse.cursorPositionY = height - cursorPositionY;
                
                if(terminal.show) terminal.scrollBar.processMouseInput(mouse);
                if(metrics.show) metrics.processMouseInput(mouse);
            }
            
            if(!mouse.previousClickValue) {
                noclip.prevX = cursorPositionX;
                noclip.prevY = cursorPositionY;
            }
            
            if(noclip.enabled && !terminal.show && mouse.rightHeld) {
                noclip.setDirection(cursorPositionX, cursorPositionY);
            }
        });
        
        glfwMouseButtonReference = GLFWMouseButtonCallback.create((window, button, action, mods) -> {
            mouse.currentClickValue = action == GLFW_PRESS;
            mouse.button            = button;
            
            switch(button) {
                case GLFW_MOUSE_BUTTON_LEFT   -> mouse.leftHeld = (action == GLFW_PRESS);
                case GLFW_MOUSE_BUTTON_MIDDLE -> mouse.middleHeld = (action == GLFW_PRESS);
                case GLFW_MOUSE_BUTTON_RIGHT  -> {
                    mouse.rightHeld = (action == GLFW_PRESS);
                    
                    if(!terminal.show && noclip.enabled) {
                        glfwSetInputMode(handle, GLFW_CURSOR, (mouse.rightHeld) ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
                    }
                }
            }
            
            if(!terminal.show && !metrics.show) {
                UIManager.processMouseInput(mouse);
            } else {
                if(terminal.show) terminal.scrollBar.processMouseInput(mouse);
                if(metrics.show) metrics.processMouseInput(mouse);
            }
        });
        
        glfwScrollReference = GLFWScrollCallback.create((window, scrollSpeedX, scrollSpeedY) -> {
            mouse.scrollSpeedX = scrollSpeedX;
            mouse.scrollSpeedY = scrollSpeedY;
            
            if(!terminal.show && !metrics.show && !noclip.enabled) {
                UIManager.processMouseInput(mouse);
                InputDeviceMouse.setScrollSpeedValues(scrollSpeedX, scrollSpeedY);
            } else {
                if(terminal.show) terminal.scrollBar.processMouseInput(mouse);
                if(metrics.show) metrics.processMouseInput(mouse);
                //TODO: include feature that allows the public API to add their own debug widgets?
                if(noclip.enabled) noclip.speed = XJGE.clampValue(0.01f, 1f, noclip.speed + ((float) mouse.scrollSpeedY * 0.01f));
            }
            
            mouse.scrollSpeedX = 0;
            mouse.scrollSpeedY = 0;
        });
        
        glfwKeyReference = GLFWKeyCallback.create((window, key, scancode, action, mods) -> {
            if(XJGE.debugModeEnabled() && action == GLFW_PRESS && mods == GLFW_MOD_SHIFT) {
                switch(key) {
                    case GLFW_KEY_F1 -> terminal.show = !terminal.show;
                    case GLFW_KEY_F2 -> metrics.show = !metrics.show;
                    case GLFW_KEY_F3 -> {
                        noclip.enabled = !noclip.enabled;
                        
                        if(noclip.enabled) {
                            viewports[0].prevCamera = viewports[0].currCamera;
                            viewports[0].currCamera = noclip;
                        } else {
                            viewports[0].currCamera = viewports[0].prevCamera;
                            glfwSetInputMode(handle, GLFW_CURSOR, cursorInputMode);
                        }
                    }
                }
            }
            
            if(terminal.show) {
                terminal.processKeyboardInput(key, action, mods, keyboard.getKeyCharacter(key, mods));
            } else if(noclip.enabled) {
                if(key == GLFW_KEY_W) noclip.pressed[0] = (action != GLFW_RELEASE);
                if(key == GLFW_KEY_A) noclip.pressed[1] = (action != GLFW_RELEASE);
                if(key == GLFW_KEY_S) noclip.pressed[2] = (action != GLFW_RELEASE);
                if(key == GLFW_KEY_D) noclip.pressed[3] = (action != GLFW_RELEASE);
            } else {
                UIManager.processKeyboardInput(key, action, mods, keyboard.getKeyCharacter(key, mods));
            }
            
            mouse.mods = mods;
        });
        
        glfwSetErrorCallback(glfwErrorReference);
        glfwSetMonitorCallback(glfwMonitorReference);
        glfwSetWindowSizeCallback(handle, glfwWindowSizeReference);
        glfwSetWindowPosCallback(handle, glfwWindowPositionReference);
        glfwSetCursorPosCallback(handle, glfwCursorPositionReference);
        glfwSetMouseButtonCallback(handle, glfwMouseButtonReference);
        glfwSetScrollCallback(handle, glfwScrollReference);
        glfwSetKeyCallback(handle, glfwKeyReference);
    }
    
    /**
     * Makes the window visible to the user.
     */
    static void show() {
        glfwShowWindow(handle);
    }
    
    static void update(double deltaMetric, int fps, int entityCount) {
        if(terminal.show) terminal.update();
        if(metrics.show) metrics.update(deltaMetric, fps, entityCount, noclip);
        
        mouse.previousClickValue = mouse.currentClickValue;
    }
    
    /**
     * Swaps the front and back framebuffers, displaying the most recent rendered frame.
     */
    static void swapBuffers() {
        if(terminal.show || metrics.show) {
            glViewport(0, 0, width, height);
            UIManager.updateProjectionMatrix(width, height, 0, Integer.MAX_VALUE);
            
            if(terminal.show) terminal.render();
            if(metrics.show) metrics.render();
        }
        
        glfwSwapBuffers(handle);
    }
    
    /**
     * Releases the references to callbacks maintained by GLFW. This is called 
     * automatically by the engine after the window has closed.
     */
    static void freeCallbacks() {
        glfwErrorReference.free();
        glfwMonitorReference.free();
        glfwWindowIconifyReference.free();
        glfwWindowSizeReference.free();
        glfwWindowPositionReference.free();
        glfwCursorPositionReference.free();
        glfwMouseButtonReference.free();
        glfwScrollReference.free();
        glfwKeyReference.free();
    }
    
    /**
     * Used to determine if the window has requested to be closed. The 
     * application will cease execution if this returns true.
     * 
     * @return if true, the window will be closed
     */
    static boolean closeRequested() {
        return glfwWindowShouldClose(handle);
    }
    
    static double getCursorPositionX() {
        return mouse.cursorPositionX;
    }
    
    static double getCursorPositionY() {
        return mouse.cursorPositionY;
    }
    
    static int getKeyInputValue(int key) {
        return glfwGetKey(handle, key);
    }
    
    static int getMouseButtonInputValue(int button) {
        return glfwGetMouseButton(handle, button);
    }
    
    /**
     * Obtains the OpenGL framebuffer object (FBO) handle used for rendering.
     * 
     * @return a number used to identify the frambuffer object
     */
    static int getFBOHandle() {
        return fboHandle;
    }
    
    static final Monitor getMonitor(long handle) {
        try {
            return monitors.values().stream().filter(mon -> mon.handle == handle).findFirst().get();
        } catch(Exception exception) {
            return null;
        }
    }
    
    /**
     * Used to supply a reference of the mouse object to the Input class for use
     * by InputDeviceMouse.
     * 
     * @return 
     */
    static Mouse getMouse() {
        return mouse;
    }
    
    /**
     * Obtains the viewports managed by the window.
     * 
     * @return an array of {@linkplain Viewport} objects
     */
    static Viewport[] getViewports() {
        return viewports;
    }
    
    /**
     * Brings the window to the forefront and requests input focus.
     */
    public static void focus() {
        glfwFocusWindow(handle);
    }
    
    /**
     * Centers the window position on the specified monitor.
     * 
     * @param monitor the monitor on which the window will be centered
     */
    public static void center(Monitor monitor) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer xPosBuf = stack.mallocInt(1);
            IntBuffer yPosBuf = stack.mallocInt(1);
            
            glfwGetMonitorPos(monitor.handle, xPosBuf, yPosBuf);
            
            positionX = Math.round((monitor.getWidth() - width) / 2) + xPosBuf.get();
            positionY = Math.round((monitor.getHeight() - height) / 2) + yPosBuf.get();
            
            glfwSetWindowPos(handle, positionX, positionY);
            
            observable.notifyObservers("WINDOW_POSITION_X_CHANGED", Window.positionX);
            observable.notifyObservers("WINDOW_POSITION_Y_CHANGED", Window.positionY);
        }
    }
    
    /**
     * Closes the applications window and gracefully ceases execution.
     */
    public static void close() {
        glfwSetWindowShouldClose(handle, true);
    }
    
    /**
     * Registers an observer that will be notified whenever a window property 
     * changes. These properties include the following:
     * <table border="0" cellpadding="4" cellspacing="0"><caption></caption>
     * <thead><tr><th align="left">Name</th><th align="left">Type</th><th align="left">Description</th></tr></thead>
     * <tbody>
     * <tr><td>WINDOW_FULLSCREEN_CHANGED</td><td>boolean</td><td>Triggered when the window enters or exits fullscreen mode</td></tr>
     * <tr><td>WINDOW_RESIZABLE_CHANGED</td><td>boolean</td><td>Indicates whether the window's resizable state has changed</td></tr>
     * <tr><td>WINDOW_MINIMUM_WIDTH_CHANGED</td><td>int</td><td>The minimum allowed width of the window has been updated</td></tr>
     * <tr><td>WINDOW_MINIMUM_HEIGHT_CHANGED</td><td>int</td><td>The minimum allowed height of the window has been updated</td></tr>
     * <tr><td>WINDOW_WIDTH_CHANGED</td><td>int</td><td>The current window width in pixels has changed</td></tr>
     * <tr><td>WINDOW_HEIGHT_CHANGED</td><td>int</td><td>The current window height in pixels has changed</td></tr>
     * <tr><td>WINDOW_POSITION_X_CHANGED</td><td>int</td><td>The X coordinate of the window’s position has changed</td></tr>
     * <tr><td>WINDOW_POSITION_Y_CHANGED</td><td>int</td><td>The Y coordinate of the window’s position has changed</td></tr>
     * <tr><td>WINDOW_RESOLUTION_WIDTH_CHANGED</td><td>int</td><td>The internal rendering resolution width has been modified</td></tr>
     * <tr><td>WINDOW_RESOLUTION_HEIGHT_CHANGED</td><td>int</td><td>The internal rendering resolution height has been modified</td></tr>
     * <tr><td>WINDOW_TITLE_CHANGED</td><td>String</td><td>The title of the window has been changed</td></tr>
     * <tr><td>WINDOW_MONITOR_CHANGED</td><td>Monitor</td><td>The monitor has been assigned to a different window</td></tr>
     * <tr><td>WINDOW_SPLITSCREEN_TYPE_CHANGED</td><td>SplitScreenType</td><td>The active split-screen layout or configuration has changed</td></tr>
     * </tbody></table>
     *
     * @param observer the {@linkplain PropertyChangeListener observer} to be 
     *                 notified of state changes made to the window
     */
    public static void addObserver(PropertyChangeListener observer) {
        observable.addObserver(observer);
    }
    
    /**
     * Removes a previously registered observer.
     * 
     * @param observer the {@linkplain PropertyChangeListener observer} to remove
     */
    public static void removeObserver(PropertyChangeListener observer) {
        observable.removeObserver(observer);
    }
    
    /**
     * Sets whether the window should be fullscreen or not.
     * 
     * @param fullscreen true for fullscreen or false for windowed mode
     */
    public static void setFullscreen(boolean fullscreen) {
        Window.fullscreen = fullscreen;
        reconfigure();
        observable.notifyObservers("WINDOW_FULLSCREEN_CHANGED", Window.fullscreen);
    }
    
    /**
     * Sets whether or not the window can be resized by the user.
     * 
     * @param resizable if true, the window can be resized by the user
     */
    public static void setResizable(boolean resizable) {
        Window.resizable = resizable;
        glfwSetWindowAttrib(handle, GLFW_RESIZABLE, Window.resizable ? GLFW_TRUE : GLFW_FALSE);
        observable.notifyObservers("WINDOW_RESIZABLE_CHANGED", Window.resizable);
    }
    
    public static void setMinimized(boolean minimized) {
        if(minimized) {
            glfwIconifyWindow(handle);
        } else {
            glfwRestoreWindow(handle);
        }
    }
    
    /**
     * Sets whether or not 4K resolutions will be restricted by the window. 
     * <br><br>
     * Using 4K resolutions can generate large framebuffer textures which can 
     * slow the applications framerate on systems without dedicated graphics 
     * hardware.
     * 
     * @param restrict4K if true, the window will restrict the use of 4K resolutions
     */
    public static void set4KRestricted(boolean restrict4K) {
        Window.restrict4K = restrict4K;
    }
    
    /**
     * Determines whether the application will take advantage of vertical sync 
     * (or VSync) while rendering frames. VSync is enabled by default on startup.
     * 
     * @param vSyncEnabled if true, vertical sync will be enabled
     */
    public static void setVSyncEnabled(boolean vSyncEnabled) {
        Window.vSyncEnabled = vSyncEnabled;
        glfwSwapInterval(vSyncEnabled ? 1 : 0);
    }
    
    /**
     * Sets the minimum size constraints for the window. If the window is
     * {@linkplain #setResizable(boolean) resizable}, users will not be able to
     * resize it to dimensions smaller than those specified by this method.
     * <br><br>
     * By default, the minimum size is set to 640x480 pixels until explicitly
     * overridden.
     * 
     * @param minWidth the minimum width of the window (in pixels)
     * @param minHeight the minimum height of the window (in pixels)
     */
    public static void setMinimumSize(int minWidth, int minHeight) {
        if(minWidth <= 0 || minHeight <= 0) {
            Logger.logInfo("Invalid minimum window size used (" + minWidth + ", " +
                           minHeight + ") values passed must be greater than zero");
            return;
        }
        
        Window.minWidth  = minWidth;
        Window.minHeight = minHeight;
        
        glfwSetWindowSizeLimits(handle, Window.minWidth, Window.minHeight, GLFW_DONT_CARE, GLFW_DONT_CARE);
        
        observable.notifyObservers("WINDOW_MINIMUM_WIDTH_CHANGED",  Window.minWidth);
        observable.notifyObservers("WINDOW_MINIMUM_HEIGHT_CHANGED", Window.minHeight);
    }
    
    /**
     * Sets the size of the window.
     * 
     * @param width the width of the window (in pixels)
     * @param height the height of the window (in pixels)
     */
    public static void setSize(int width, int height) {
        if(width <= 0 || height <= 0) {
            Logger.logInfo("Invalid window size used (" + width + ", " + height + 
                           ") values passed must be greater than zero");
            return;
        }
        
        Window.width  = width;
        Window.height = height;
        
        glfwSetWindowSize(handle, width, height);
        
        observable.notifyObservers("WINDOW_WIDTH_CHANGED",  Window.width);
        observable.notifyObservers("WINDOW_HEIGHT_CHANGED", Window.height);
    }
    
    /**
     * Sets the position of the window relative to it's top-left corner.
     * 
     * @param positionX the windows position along the x-axis (in pixels)
     * @param positionY the windows position along the y-axis (in pixels)
     */
    public static void setPosition(int positionX, int positionY) {
        Window.positionX = positionX;
        Window.positionY = positionY;
        
        glfwSetWindowPos(handle, positionX, positionY);
        
        observable.notifyObservers("WINDOW_POSITION_X_CHANGED", positionX);
        observable.notifyObservers("WINDOW_POSITION_Y_CHANGED", positionY);
    }
    
    /**
     * Sets the internal resolution (in pixels) of the engines framebuffer. 
     * Often this is used to fix the stretching caused by changes in the 
     * monitors aspect ratio or windows size at runtime, but can also be used to
     * achieve a retro/pixelated look.
     * 
     * @param width the resolution width of the window (in pixels)
     * @param height the resolution height of the window (in pixels)
     */
    public static void setResolution(int width, int height) {
        if(width <= 0 || height <= 0) {
            Logger.logInfo("Invalid window resolution used (" + width + ", " + 
                           height + ") values passed must be greater than zero");
            return;
        }
        
        Window.resolution.width  = width;
        Window.resolution.height = height;
        
        createRenderbuffer();
        setSplitScreenType(splitType);
        
        observable.notifyObservers("WINDOW_RESOLUTION_WIDTH_CHANGED",  resolution.width);
        observable.notifyObservers("WINDOW_RESOLUTION_HEIGHT_CHANGED", resolution.height);
    }
    
    /**
     * Sets the behavior of an input mode used by the window, such as cursor 
     * visibility or sticky keys.
     * 
     * @param mode the input option to manipulate. One of 
     *              {@link org.lwjgl.glfw.GLFW#GLFW_CURSOR GLFW_CURSOR}, 
     *              {@link org.lwjgl.glfw.GLFW#GLFW_STICKY_KEYS GLFW_STICKY_KEYS}, 
     *              {@link org.lwjgl.glfw.GLFW#GLFW_STICKY_MOUSE_BUTTONS GLFW_STICKY_MOUSE_BUTTONS}, 
     *              {@link org.lwjgl.glfw.GLFW#GLFW_LOCK_KEY_MODS GLFW_LOCK_KEY_MODS}, or
     *              {@link org.lwjgl.glfw.GLFW#GLFW_RAW_MOUSE_MOTION GLFW_RAW_MOUSE_MOTION}.
     * @param value the new value to set the specified input option to 
     */
    public static void setInputMode(int mode, int value) {
        if(mode == GLFW_CURSOR) cursorInputMode = value;
        glfwSetInputMode(handle, mode, value);
    }
    
    /**
     * Changes the text that will appear on the windows title bar and elsewhere.
     * 
     * @param title a string used to identify the window
     */
    public static void setTitle(String title) {
        if(title == null) {
            Logger.logInfo("Window title cannot be null");
            return;
        }
        
        Window.title = title;
        glfwSetWindowTitle(handle, title);
        observable.notifyObservers("WINDOW_TITLE_CHANGED", Window.title);
    }
    
    /**
     * Sets the icon image of the window. Images used for window icons should be
     * no smaller than 32x32 pixels and no larger than 64x64.
     * 
     * @param filename the name of the file to load (with extension)
     */
    public static void setIcon(String filepath, String filename) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            InputStream file = Window.class.getResourceAsStream(filepath + filename);
            byte[] iconData  = file.readAllBytes();
            
            IntBuffer widthBuf   = stack.mallocInt(1);
            IntBuffer heightBuf  = stack.mallocInt(1);
            IntBuffer channelBuf = stack.mallocInt(1);
            
            ByteBuffer icon = stbi_load_from_memory(
                    stack.malloc(iconData.length).put(iconData).flip(),
                    widthBuf,
                    heightBuf,
                    channelBuf,
                    STBI_rgb_alpha);
            
            glfwSetWindowIcon(handle, GLFWImage.malloc(1, stack)
                    .width(widthBuf.get())
                    .height(heightBuf.get())
                    .pixels(icon));
            
            stbi_image_free(icon);
            
        } catch(Exception exception) {
            Logger.logWarning("Failed to change the window icon using \"" + filename +
                              "\" check the filename, path, or extension", exception);
        }
    }
    
    /**
     * Changes the current split type the engine will use to divide the screen 
     * during split screen play. More specifically this will effect the layout 
     * of the viewports within the application window during runtime.
     * 
     * @param splitType a value that determines how the screen will be divided. One of: 
     * <table><caption></caption><tr>
     * <td>{@link SplitScreenType#NONE NONE}</td><td>{@link SplitScreenType#HORIZONTAL HORIZONTAL}</td>
     * <td>{@link SplitScreenType#VERTICAL VERTICAL}</td>
     * <td>{@link SplitScreenType#TRISECT TRISECT}</td><td>{@link SplitScreenType#QUARTER QUARTER}</td>
     * </tr></table>
     */
    public static void setSplitScreenType(SplitScreenType splitType) {
        Window.splitType = splitType;
        
        for(Viewport viewport : viewports) {
            switch(splitType) {
                case NONE -> {
                    viewport.active = (viewport.id == 0);
                    viewport.setBounds(resolution.width, resolution.height, 
                                       0, 0, 
                                       width, height);
                }
                
                case HORIZONTAL -> {
                    viewport.active = (viewport.id == 0 || viewport.id == 1);
                    switch(viewport.id) {
                        case 0 -> viewport.setBounds(
                                    resolution.width, resolution.height / 2,
                                    0, height / 2, 
                                    width, height / 2);
                            
                        case 1 -> viewport.setBounds(
                                    resolution.width, resolution.height / 2,
                                    0, 0, 
                                    width, height / 2);
                        
                        default -> viewport.setBounds(resolution.width, resolution.height, 0, 0, 0, 0);
                    }
                }
                
                case VERTICAL -> {
                    viewport.active = (viewport.id == 0 || viewport.id == 1);
                    switch(viewport.id) {
                        case 0 -> viewport.setBounds(
                                    resolution.width / 2, resolution.height,
                                    0, 0, 
                                    width / 2, height);
                            
                        case 1 -> viewport.setBounds(
                                    resolution.width / 2, resolution.height,
                                    width / 2, 0, 
                                    width / 2, height);
                        
                        default -> viewport.setBounds(resolution.width, resolution.height, 0, 0, 0, 0);
                    }
                }
                
                case TRISECT -> {
                    viewport.active = (viewport.id != 3);
                    switch(viewport.id) {
                        case 0 -> viewport.setBounds(
                                    resolution.width / 2, resolution.height / 2,
                                    0, height / 2, 
                                    width / 2, height / 2);
                            
                        case 1 -> viewport.setBounds(
                                    resolution.width / 2, resolution.height / 2,
                                    width / 2, height / 2, 
                                    width / 2, height / 2);
                            
                        case 2 -> viewport.setBounds(
                                    resolution.width / 2, resolution.height / 2,
                                    width / 4, 0, 
                                    width / 2, height / 2);
                        
                        default -> viewport.setBounds(resolution.width, resolution.height, 0, 0, 0, 0);
                    }
                }
                
                case QUARTER -> {
                    viewport.active = true;
                    switch(viewport.id) {
                        case 0 -> viewport.setBounds(
                                    resolution.width / 2, resolution.height / 2,
                                    0, height / 2, 
                                    width / 2, height / 2);
                            
                        case 1 -> viewport.setBounds(
                                    resolution.width / 2, resolution.height / 2,
                                    width / 2, height / 2, 
                                    width / 2, height / 2);
                            
                        case 2 -> viewport.setBounds(
                                    resolution.width / 2, resolution.height / 2,
                                    0, 0, 
                                    width / 2, height / 2);
                            
                        case 3 -> viewport.setBounds(
                                    resolution.width / 2, resolution.height / 2,
                                    width / 2, 0, 
                                    width / 2, height / 2);
                    }
                }
            }
        }
        
        observable.notifyObservers("WINDOW_SPLITSCREEN_TYPE_CHANGED", Window.splitType);
    }
    
    /**
     * Sets the current camera object a viewport will use.
     * 
     * @param viewportID the ID number of the viewport whos camera we want to set
     * @param camera the camera object being assigned to the viewport
     */
    public static final void setViewportCamera(int viewportID, Camera camera) {
        if(camera == null) {
            Logger.logInfo("Failed to set viewport camera. Null is not " + 
                           "accepted as a value of this function");
        } else if(viewportID < GLFW_JOYSTICK_1 || viewportID > GLFW_JOYSTICK_4) {
            Logger.logInfo("Failed to set viewport camera. No viewport "+ 
                           "by the ID of " + viewportID + " exists");
        } else {
            if(viewports[viewportID].currCamera.equals(noclip)) {
                //Added in case noclip is enabled when this was called.
                viewports[viewportID].prevCamera = camera;
            } else {
                viewports[viewportID].currCamera = camera;
            }
        }
    }
    
    /**
     * Moves the window to another monitor. The window will reconfigure itself 
     * using the target monitors settings after being moved.
     * 
     * @param monitor the target {@linkplain Monitor} to use
     * @return if true, the method call was successful in changing the windows monitor
     */
    public static boolean setMonitor(Monitor monitor) {
        if(monitor == null) {
            Logger.logWarning("Failed to change window monitor. Parameter value cannot be null", null);
            return false;
        }
        
        Window.monitor = monitor;
        reconfigure();
        observable.notifyObservers("WINDOW_MONITOR_CHANGED", Window.monitor);
        
        return true;
    }
    
    /**
     * Obtains the value used to indicate if the window is in fullscreen mode.
     * 
     * @return if true, the window will fill the entire screen
     */
    public static boolean getFullscreen() {
        return fullscreen;
    }
    
    public static boolean getResizable() {
        return resizable;
    }
    
    public static boolean getMinimized() {
        return minimized;
    }
    
    public static boolean get4KRestricted() {
        return restrict4K;
    }
    
    /**
     * Obtains a value indicating whether or not vertical sync is enabled.
     * 
     * @return true if vertical sync is currently enabled
     */
    public static boolean getVSyncEnabled() {
        return vSyncEnabled;
    }
    
    /**
     * Obtains the current width of the windows content area.
     * 
     * @return the width of the window (in pixels)
     */
    public static int getWidth() {
        return width;
    }
    
    /**
     * Obtains the current height of the windows content area.
     * 
     * @return the height of the window (in pixels)
     */
    public static int getHeight() {
        return height;
    }
    
    /**
     * Obtains the position of the window, in screen coordinates, of the 
     * upper-left corner of the content area of the window.
     * 
     * @return the position of the windows content area along the x-axis
     */
    public static int getPositionX() {
        return positionX;
    }
    
    /**
     * Obtains the position of the window, in screen coordinates, of the 
     * upper-left corner of the content area of the window.
     * 
     * @return the position of the windows content area along the y-axis
     */
    public static int getPositionY() {
        return positionY;
    }
    
    /**
     * Obtains the width of the resolution used by the window.
     * 
     * @return the width (in pixels) of the engines internal framebuffer texture
     */
    public static int getResolutionWidth() {
        return resolution.width;
    }
    
    /**
     * Obtains the height of the resolution used by the window.
     * 
     * @return the height (in pixels) of the engines internal framebuffer texture
     */
    public static int getResolutionHeight() {
        return resolution.height;
    }
    
    /**
     * Obtains the current value of the specified input option used by the game 
     * window.
     * 
     * @param mode the input option to manipulate. One of 
     *             {@link org.lwjgl.glfw.GLFW#GLFW_CURSOR GLFW_CURSOR}, 
     *             {@link org.lwjgl.glfw.GLFW#GLFW_STICKY_KEYS GLFW_STICKY_KEYS}, 
     *             {@link org.lwjgl.glfw.GLFW#GLFW_STICKY_MOUSE_BUTTONS GLFW_STICKY_MOUSE_BUTTONS}, 
     *             {@link org.lwjgl.glfw.GLFW#GLFW_LOCK_KEY_MODS GLFW_LOCK_KEY_MODS}, or
     *             {@link org.lwjgl.glfw.GLFW#GLFW_RAW_MOUSE_MOTION GLFW_RAW_MOUSE_MOTION}.
     * @return the current state of the queried mode
     */
    public static int getInputMode(int mode) {
        return glfwGetInputMode(handle, mode);
    }
    
    public static int getNumMonitors() {
        return monitors.size();
    }
    
    /**
     * Obtains the current {@linkplain Monitor} the window is using.
     * 
     * @return an object representing a single display device
     */
    public static final Monitor getMonitor() {
        return monitor;
    }
    
    public static final Monitor getMonitor(int index) {
        return monitors.get(index);
    }
    
    public static final NavigableMap<Integer, Monitor> findMonitors() {
        monitors.clear();
        PointerBuffer monitorBuffer = glfwGetMonitors();
        
        if(monitorBuffer == null || monitorBuffer.limit() == 0) {
            Logger.logWarning("Failed to find any available monitors", null);
        } else {
            for(int i = 0; i < monitorBuffer.limit(); i++) {
                monitors.put(i, new Monitor(i, monitorBuffer.get(i)));
            }
            
            if(monitor != null && getMonitor(monitor.handle) == null) {
                Monitor fallback = monitors.firstEntry().getValue();
                
                Logger.logWarning("The previous monitor in use is now unavailable. Attempting to move the window " + 
                                  "to monitor \"" + fallback.name + "\" at index " + fallback.index, null);
                
                if(fallback != monitor) {
                    setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
                    setFullscreen(false);
                    setMonitor(fallback);
                }
            }
        }
        
        return Collections.unmodifiableNavigableMap(monitors);
    }
    
    /**
     * Obtains the title used to identify the window. This will be displayed to 
     * the user from the windows title bar among other places.
     * 
     * @return a string used to identify the window
     */
    public static final String getTitle() {
        return title;
    }
    
    /**
     * Obtains the current split type used to divide the windows content area.
     * 
     * @return a value indicating how the screen is being divided
     */
    public static SplitScreenType getSplitScreenType() {
        return splitType;
    }
    
}