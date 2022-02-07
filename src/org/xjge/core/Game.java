package org.xjge.core;

import org.xjge.graphics.Color;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import org.joml.Matrix4f;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;
import static org.xjge.core.XJGE.glPrograms;
import org.xjge.graphics.GLProgram;

//Created: May 11, 2021

/**
 * Provides utilities for managing high-level game logic.
 * <p>
 * More specifically the game class can be used to change the current scene 
 * being rendered or the flow of execution through its access to the engines 
 * central event queue. In addition to these features the Game class also 
 * provides some convenience methods useful for general gameplay operations.
 * <p>
 * These gameplay methods include:
 * <ul>
 * <li>{@linkplain #addEntity(String, Entity)}</li>
 * <li>{@linkplain #addEvent(Event)}</li>
 * <li>{@linkplain #addLight(int, Light)}</li>
 * <li>{@linkplain #setClearColor(Color)}</li>
 * <li>{@linkplain #setScene(Scene)}</li>
 * <li>{@linkplain #tick(int)}</li>
 * <li>{@linkplain #setBloomThreshold(float)}</li>
 * </ul>
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Game {

    private static int fps;
    private static int tickCount = 0;
    final static int TICKS_PER_HOUR = 3599999;
    
    private static float bloomThreshold = 1.0f;
    
    private static double deltaMetric = 0;
    
    private static boolean ticked;
    public static boolean enableBloom;
    
    private static Color clearColor = Color.create(92, 148, 252);
    private static Scene scene;
    
    private static final Queue<Event> events = new PriorityQueue<>(Comparator.comparing(Event::getPriority));
    
    /**
     * Central game loop that decouples game time progression from processor 
     * speed and framerate.
     * 
     * @param fbo          the handle of the framebuffer object used to render 
     *                     viewports
     * @param viewports    an array of the viewports to render during split 
     *                     screen
     * @param terminal     a command terminal that can be used to interact with 
     *                     the engine
     * @param debugInfo    an interface detailing the current state of the 
     *                     engine
     * @param depthProgram the shader program provided by the engine that will 
     *                     be used to generate the shadow map texture
     * @param blurProgram  the shader program used to apply a Gaussian blur to
     *                     the bloom framebuffer texture
     */
    static void loop(int fbo, Viewport[] viewports, Terminal terminal, DebugInfo debugInfo, GLProgram depthProgram, GLProgram blurProgram) {
        int cycles = 0;
        final double TARGET_DELTA = 1 / 60.0;
        double prevTime = glfwGetTime();
        double currTime;
        double delta = 0;
        Matrix4f projMatrix = new Matrix4f();
        
        while(!glfwWindowShouldClose(Window.HANDLE)) {
            glfwPollEvents();
            
            currTime = glfwGetTime();
            delta    += currTime - prevTime;
            if(delta < TARGET_DELTA && Hardware.getVSyncEnabled()) delta = TARGET_DELTA;
            prevTime = currTime;
            ticked   = false;
            
            while(delta >= TARGET_DELTA) {
                Input.pollInput();
                if(XJGE.getTerminalEnabled()) terminal.update();
                
                deltaMetric = delta;
                
                delta     -= TARGET_DELTA;
                ticked    = true;
                tickCount = (tickCount == TICKS_PER_HOUR) ? 0 : tickCount + 1;
                
                //Process any unresolved events otherwise update the scene normally.
                if(events.size() > 0) {
                    Event event = events.peek();
                    
                    if(!event.resolved) event.resolve();
                    else                events.poll();
                } else {
                    scene.update(TARGET_DELTA, deltaMetric);
                    scene.updateLightSources();
                    scene.processRemoveRequests();
                }
                
                //Update viewport camera objects.
                for(Viewport viewport : viewports) {
                    if(viewport.active && viewport.currCamera != null) {
                        viewport.currCamera.update();
                        viewport.ui.forEach((name, component) -> component.update());

                        Audio.setViewportCamData(viewport.id, viewport.currCamera.position, viewport.currCamera.direction);
                    }
                }
                
                Audio.updateSoundSourcePositions();
                Audio.queueMusicBodySection();
                
                if(tick(60)) {
                    fps    = cycles;
                    cycles = 0;
                }
            }
            
            XJGE.getDefaultGLProgram().use();
            XJGE.getDefaultGLProgram().setUniform("uBloomThreshold", bloomThreshold);
            scene.setShadowUniforms();
            scene.setLightingUniforms();
            
            //Render scene from the perspective of each active viewport.
            for(Viewport viewport : viewports) {
                if(viewport.active) {
                    scene.renderShadowMap(viewport.currCamera.up, depthProgram);
                    
                    if(viewport.id == 0) {
                        glClearColor(0, 0, 0, 0);
                        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                    }
                    
                    glBindFramebuffer(GL_FRAMEBUFFER, fbo);
                        glViewport(0, 0, viewport.width, viewport.height);
                        glClearColor(clearColor.r, clearColor.g, clearColor.b, 0);
                        viewport.bindDrawBuffers(Game.enableBloom);
                        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                        
                        viewport.resetCamera(glPrograms);
                        
                        viewport.render(glPrograms, "camera");
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
                    
                    glPrograms.get("default").use();
                    glPrograms.get("default").setUniform("uProjection", false, projMatrix);
                    
                    viewport.render(glPrograms, "texture");
                    viewport.render(glPrograms, "ui");
                }
            }
            
            if(XJGE.getTerminalEnabled() || debugInfo.show) {
                glPrograms.get("default").use();
                glViewport(0, 0, Window.getWidth(), Window.getHeight());
                projMatrix.setOrtho(0,  Window.getWidth(), 0, Window.getHeight(), 0, Integer.MAX_VALUE);
                glPrograms.get("default").setUniform("uProjection", false, projMatrix);
                
                if(XJGE.getTerminalEnabled()) terminal.render();
                if(debugInfo.show) debugInfo.render();
            }
            
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
    }
    
    /**
     * Obtains engine runtime information.
     * 
     * @return the number of consecutive frames that have been rendered since 
     *         the last game tick
     */
    static int getFPS() {
        return fps;
    }
    
    /**
     * Obtains engine runtime information.
     * <p>
     * NOTE: The current delta value returned by this method is truncated and 
     * intended for display/debug purposes only. A more accurate version can be 
     * found in the {@linkplain Scene#update(double, double) update method} of
     * the current scene under the {@code trueDelta} argument.
     * 
     * @return the time (in seconds) it took the engine to complete an update 
     *         cycle
     */
    static float getDelta() {
        return (float) deltaMetric;
    }
    
    /**
     * Obtains engine runtime information.
     * 
     * @return true of the game has completed a pass of its update cycle
     */
    static boolean getTicked() {
        return ticked;
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
    
    /**
     * Changes the color OpenGL will use to clear color buffers. Often used to 
     * set background or sky colors.
     * 
     * @param color the color empty space will be filled with
     */
    public static void setClearColor(Color color) {
        clearColor = color;
    }
    
    /**
     * Exits the current scene and enters the one specified.
     * 
     * @param scene the new scene to enter
     */
    public static void setScene(Scene scene) {
        Logger.setDomain("core");
        Logger.logInfo("Current scene changed to \"" + scene.name + "\"");
        Logger.newLine();
        Logger.setDomain(null);
        
        if(Game.scene != null) scene.exit();
        Game.scene = scene;
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
     * Returns true anytime the specified number of update iterations 
     * (or cycles) have been reached. Intended to be used in for game systems 
     * that don't require the precision of the {@link Timer} or 
     * {@link StopWatch} classes.
     * 
     * @param speed the number of cycles to wait until the next tick will 
     *              occur
     * 
     * @return true every time the specified number of cycles has been reached
     */
    public static boolean tick(int speed) {
        return tickCount % speed == 0;
    }
    
    /**
     * Adds an entity to the current scene. Generally speaking you'll only want 
     * to use this for instances where a brute-force solution is required, 
     * otherwise entity objects should be managed directly from within the 
     * scene subclass itself.
     * 
     * @param name   the name that will be used to identify the entity in the 
     *               current scenes {@linkplain Scene#entities entity collection}
     * @param entity the entity object to add
     */
    public static final void addEntity(String name, Entity entity) {
        scene.entities.put(name, entity);
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
            Logger.setDomain("core");
            Logger.logWarning("Failed to add light at index " + index + ".", e);
            Logger.setDomain(null);
        }
    }
    
}