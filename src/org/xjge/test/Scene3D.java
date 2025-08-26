package org.xjge.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector3i;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Camera;
import org.xjge.core.Entity;
import org.xjge.core.Logger;
import org.xjge.core.Scene;
import org.xjge.core.Window;
import org.xjge.graphics.GLProgram;

/**
 * Created: Jan 31, 2025
 * 
 * @author J Hoffman
 * @since  
 */
public class Scene3D extends Scene {
    
    private final GridRenderer grid = new GridRenderer();
    final CameraOverhead camera = new CameraOverhead();
    
    private final Map<Vector3i, GridSpace> spaces = new HashMap<>();
    
    private GameMode gameMode = new GameModeBattle();
    
    public Scene3D(String filename) {
        super("test");
        
        Window.setViewportCamera(GLFW_JOYSTICK_1, camera);
        
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/org/xjge/assets/" + filename)));
            
            int z = 0;
            String line = reader.readLine();
            
            while(line != null) {
                String[] delims = line.split(" ");
                
                for(int x = 0; x < delims.length; x++) {
                    int type        = Integer.parseInt(delims[x]);
                    GridSpace space = new GridSpace(type, x, 0, z);
                    
                    spaces.put(new Vector3i(x, 0, z), space);
                    
                    //Spawn players and enemies
                    if(type == 2) {
                        Entity player = new Entity();
                        ComponentUnit unit = new ComponentUnit("player", camera, x, 0.01f, z, GLFW_JOYSTICK_1);
                        player.addComponent(unit);
                        player.addComponent(new ComponentAABB(0.5f, 0.6f));
                        addEntity(player);
                        space.occupyingUnit = unit;
                    } else if(type == 3) {
                        //Unit enemy = new Unit(new Vector3f(x, 0.01f, z), "enemy", 1, AI_GAMEPAD_1);
                        //space.occupyingUnit = enemy;
                        //addEntity(enemy);
                        //turns.add(enemy);
                    }
                }
                
                z++;
                line = reader.readLine();
            }
        } catch(IOException exception) {
            Logger.logError("Failed to load map\"" + filename + "\"", exception);
        }
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        if(gameMode != null) gameMode.execute(this, Collections.unmodifiableMap(entities));
        
        entities.values().forEach(entity -> {
            if(entity.hasComponent(ComponentAABB.class)) {
                //update collision detection
            }
        });
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera, int depthTexHandle) {
        grid.draw(glPrograms.get("grid"), spaces);
        
        entities.values().forEach(entity -> {
            if(entity.hasComponent(ComponentUnit.class)) {
                entity.getComponent(ComponentUnit.class).render(glPrograms.get("test"));
            }
            if(entity.hasComponent(ComponentAABB.class) && Main.showBoundingVolumes()) {
                //render AABB
            }
        });
    }

    @Override
    public void renderShadows(GLProgram depthProgram) {
    }

    @Override
    public void exit() {
    }

}