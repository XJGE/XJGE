package org.xjge.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import org.joml.Vector3f;
import org.joml.Vector3i;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Camera;
import org.xjge.core.Entity;
import static org.xjge.core.Input.KEY_MOUSE_COMBO;
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
    
    private int openSpaceCount;
    
    final CameraOverhead camera = new CameraOverhead();
    private final GridRenderer gameBoard = new GridRenderer();
    
    private ComponentUnit activeUnit;
    private Queue<ComponentUnit> turns = new LinkedList<>();
    
    private final Map<UUID, ComponentUnit> units  = new HashMap<>();
    private final Map<Vector3i, GridSpace> spaces = new HashMap<>();
    
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
                    
                    if(type != 1) openSpaceCount++;
                    
                    spaces.put(new Vector3i(x, 0, z), space);
                    
                    //Spawn players and enemies
                    if(type == 2) {
                        Entity player = new Entity();
                        ComponentUnit unit = new ComponentUnit(new Vector3f(x, 0.01f, z), "player", 0, KEY_MOUSE_COMBO);
                        player.addComponent(unit);
                        addEntity(player);
                        space.occupyingUnit = unit;
                        turns.add(unit);
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
        
        activeUnit = turns.poll();
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        //if(gameMode != null) gameMode.execute(this, Collections.unmodifiableMap(entities));
        
        if(activeUnit.turnFinished(this, Collections.unmodifiableMap(units), Collections.unmodifiableMap(spaces))) {
            //activeUnit = turns.poll();
        }
        
        entities.forEach((uuid, entity) -> {
            if(entity != null) {
                if(entity.hasComponent(ComponentUnit.class)) {
                    ComponentUnit unit = entity.getComponent(ComponentUnit.class);
                    units.put(uuid, unit);
                    if(unit != activeUnit && !turns.contains(unit)) turns.add(unit);
                }
            }
        });
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera, int depthTexHandle) {
        gameBoard.draw(glPrograms.get("grid"), spaces);
        
        entities.values().forEach(entity -> {
            if(entity.hasComponent(ComponentUnit.class)) entity.getComponent(ComponentUnit.class).render(glPrograms.get("test"));
        });
    }

    @Override
    public void renderShadows(GLProgram depthProgram) {
    }

    @Override
    public void exit() {
    }

}