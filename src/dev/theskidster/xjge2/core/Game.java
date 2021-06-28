package dev.theskidster.xjge2.core;

import static dev.theskidster.xjge2.core.XJGE.glPrograms;
import dev.theskidster.xjge2.graphics.Color;
import dev.theskidster.xjge2.graphics.Light;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import org.joml.Matrix4f;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author J Hoffman
 * Created: May 11, 2021
 */

public final class Game {

    private static int fps;
    private static int tickCount = 0;
    
    private static double deltaMetric = 0;
    
    private static boolean ticked;
    
    private static Color clearColor = Color.create(92, 148, 252);
    private static Scene scene;
    
    private static final Queue<Event> events = new PriorityQueue<>(Comparator.comparing(Event::getPriority));
    
    static void loop(int fbo, Viewport[] viewports, Terminal terminal, DebugInfo debugInfo) {
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
                    scene.update(TARGET_DELTA);
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
            
            //Render scene from the perspective of each active viewport.
            for(Viewport viewport : viewports) {
                if(viewport.active) {
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
    
    public static boolean tick(int cycles) {
        return tickCount % cycles == 0;
    }
    
    public static final void addEntity(String name, Entity entity) {
        scene.entities.put(name, entity);
    }
    
    public static final void addEvent(Event event) {
        events.add(event);
    }
    
    public static int getFPS() {
        return fps;
    }
    
    public static float getDelta() {
        return (float) deltaMetric;
    }
    
    public static boolean getTicked() {
        return ticked;
    }
    
    public static void setClearColor(Color color) {
        clearColor = color;
    }
    
    public static void setScene(Scene scene) {
        Logger.setDomain("core");
        Logger.logInfo("Current scene changed to \"" + scene.name + "\"");
        Logger.newLine();
        Logger.setDomain(null);
        
        if(Game.scene != null) scene.exit();
        Game.scene = scene;
    }
    
    //TODO: Temp
    static void addLight(Light light) {
        scene.addLightAtIndex(0, light);
    }
    
}