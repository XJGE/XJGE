package dev.theskidster.xjge2.core;

import static dev.theskidster.xjge2.core.XJGE.glPrograms;
import dev.theskidster.xjge2.graphics.Color;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author J Hoffman
 * Created: May 11, 2021
 */

public final class Game {

    private static int fps;
    private static int tickCount = 0;
    
    private static double deltaMetric = 0;
    
    private static boolean ticked;
    
    private static Color clearColor = Color.BLACK;
    private static Scene scene;
    private static Camera camera;
    
    private static final Queue<Event> events = new PriorityQueue<>(Comparator.comparing(Event::getPriority));
    
    static void loop() {
        camera = new FreeCam(); //this will be removed eventually- just here for testing purposes.
        
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
                
                //Process any unresolved events otherwise update the scene normally.
                if(events.size() > 0) {
                    Event event = events.peek();

                    if(!event.resolved) event.resolve();
                    else                events.poll();
                } else {
                    camera.update(); //TODO: temp
                    scene.update(TARGET_DELTA);
                    scene.processRemoveRequests();
                }
                
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
    
    static int getFPS() {
        return fps;
    }
    
    static float getDelta() {
        return (float) deltaMetric;
    }
    
    static boolean getTicked() {
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
    
}