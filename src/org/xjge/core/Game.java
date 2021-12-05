package org.xjge.core;

import static org.xjge.core.XJGE.glPrograms;
import org.xjge.graphics.Color;
import org.xjge.graphics.Light;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;
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
 * <li>{@linkplain #addLight(Light)}</li>
 * <li>{@linkplain #setClearColor(Color)}</li>
 * <li>{@linkplain #setScene(Scene)}</li>
 * <li>{@linkplain #tick(int)}</li>
 * </ul>
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Game {

    private static int fps;
    private static int tickCount = 0;
    
    private static double deltaMetric = 0;
    
    private static boolean ticked;
    
    private static Color clearColor = Color.create(92, 148, 252);
    private static Scene scene;
    
    //TODO: temp- move somewhere else.
    public static ShadowMap shadowMap  = new ShadowMap();
    private static Vector3f camUp = new Vector3f(0, 1, 0);
    
    private static final Queue<Event> events = new PriorityQueue<>(Comparator.comparing(Event::getPriority));
    
    /**
     * Central game loop that decouples game time progression from processor 
     * speed and framerate.
     * 
     * @param fbo       the handle of the framebuffer object used to render 
     *                  viewports
     * @param viewports an array of the viewports to render during split screen
     * @param terminal  a command terminal that can be used to interact with 
     *                  the engine
     * @param debugInfo an interface detailing the current state of the engine
     */
    static void loop(int fbo, Viewport[] viewports, Terminal terminal, DebugInfo debugInfo, GLProgram depthProgram) {
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
                tickCount = (tickCount == Integer.MAX_VALUE) ? 0 : tickCount + 1;
                
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
            
            //TODO: only if shadow map is not null.
            XJGE.getDefaultGLProgram().use();
            XJGE.getDefaultGLProgram().setUniform("uLightSpace", false, shadowMap.lightSpace);
            XJGE.getDefaultGLProgram().setUniform("uPCFValue", shadowMap.PCFValue);
            
            scene.setLightingUniforms();
            
            //Render scene from the perspective of each active viewport.
            for(Viewport viewport : viewports) {
                if(viewport.active) {
                    shadowMap.createMap(camUp, depthProgram, scene);
                    
                    if(viewport.id == 0) {
                        glClearColor(0, 0, 0, 0);
                        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                    }
                    
                    glBindFramebuffer(GL_FRAMEBUFFER, fbo);
                        glViewport(0, 0, viewport.width, viewport.height);
                        glClearColor(clearColor.r, clearColor.g, clearColor.b, 0);
                        switch(viewport.id) {
                            case 0 -> glDrawBuffer(GL_COLOR_ATTACHMENT0);
                            case 1 -> glDrawBuffer(GL_COLOR_ATTACHMENT1);
                            case 2 -> glDrawBuffer(GL_COLOR_ATTACHMENT2);
                            case 3 -> glDrawBuffer(GL_COLOR_ATTACHMENT3);
                        }
                        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

                        viewport.resetCamera(glPrograms);
                        
                        viewport.render(glPrograms, "camera");
                        scene.renderSkybox(viewport.currCamera.viewMatrix);
                        scene.render(glPrograms, viewport.id, viewport.currCamera);
                        scene.renderLightsources(viewport.currCamera);
                        viewport.render(glPrograms, "ui");
                    glBindFramebuffer(GL_FRAMEBUFFER, 0);
                    
                    glViewport(viewport.botLeft.x, viewport.botLeft.y, viewport.topRight.x, viewport.topRight.y);
                    projMatrix.setOrtho(viewport.width, 0, 0, viewport.height, 0, 1);
                    
                    glPrograms.get("default").use();
                    glPrograms.get("default").setUniform("uProjection", false, projMatrix);

                    viewport.render(glPrograms, "texture");
                }
            }
            
            //TODO: add extension for shadow mapping.
            
            if(XJGE.getTerminalEnabled() || debugInfo.show) {
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
     * Returns true whenever the specified number of update iterations 
     * (or cycles) has been reached. Intended to be used in for game systems 
     * that don't require the decoupled precision of the {@link Timer} class.
     * 
     * @param cycles the number of cycles to wait until the next tick will 
     *               occur
     * 
     * @return true every time the specified number of cycles has been reached
     */
    public static boolean tick(int cycles) {
        return tickCount % cycles == 0;
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
     * Adds a new light to the scene. If the maximum number of lights has been 
     * reached, it will recycle the first one in sequence.
     * 
     * @param light the light object to add
     */
    public static void addLight(Light light) {
        scene.addLight(light);
    }
    
    /**
     * Obtains engine runtime information.
     * 
     * @return the number of consecutive frames that have been rendered since 
     *         the last game tick
     */
    public static int getFPS() {
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
    public static float getDelta() {
        return (float) deltaMetric;
    }
    
    /**
     * Obtains engine runtime information.
     * 
     * @return true of the game has completed a pass of its update cycle
     */
    public static boolean getTicked() {
        return ticked;
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
    
}