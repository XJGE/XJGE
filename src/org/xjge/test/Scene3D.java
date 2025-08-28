package org.xjge.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector3f;
import org.joml.Vector3i;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Camera;
import org.xjge.core.Entity;
import org.xjge.core.Logger;
import org.xjge.core.Scene;
import org.xjge.core.Window;
import org.xjge.core.XJGE;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;

/**
 * Created: Jan 31, 2025
 * 
 * @author J Hoffman
 * @since  
 */
public class Scene3D extends Scene {
    
    final CameraOverhead camera = new CameraOverhead();
    
    private final Vector3i tempVec = new Vector3i();
    private final GridRenderer gridRenderer = new GridRenderer();
    private final Map<Vector3i, GridSpace> gridSpaces = new HashMap<>();
    
    private GameMode gameMode = new GameModeBattle();
    
    public Scene3D(String filename) {
        super("test");
        
        XJGE.setClearColor(Color.SILVER);
        Window.setViewportCamera(GLFW_JOYSTICK_1, camera);
        
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/org/xjge/assets/" + filename)));
            
            int z = 0;
            String line = reader.readLine();
            
            while(line != null) {
                String[] delims = line.split(" ");
                
                for(int x = 0; x < delims.length; x++) {
                    int type = Integer.parseInt(delims[x]);
                    GridSpace gridSpace = new GridSpace(type, x, (type == 1) ? 1 : 0, z);
                    
                    gridSpaces.put(new Vector3i(x, gridSpace.yLocation, z), gridSpace);
                    
                    //Spawn players and enemies
                    if(type == 2) {
                        Entity player = new Entity();
                        
                        player.addComponent(new ComponentPosition(x, 0.01f, z));
                        player.addComponent(new ComponentAABB(0.5f, 1.1f, 0.5f));
                        player.addComponent(new ComponentMovable());
                        player.addComponent(new ComponentUnit(player, "player", camera, GLFW_JOYSTICK_1));
                        
                        addEntity(player);
                        gridSpace.occupyingUnit = player.getComponent(ComponentUnit.class);
                        
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
        
        gridSpaces.forEach((location, gridSpace) -> {
            //left
            tempVec.set(location.x - 1, location.y, location.z);
            gridSpace.unreachableEdge[0] = gridSpaces.containsKey(tempVec);
            
            //right
            tempVec.set(location.x + 1, location.y, location.z);
            gridSpace.unreachableEdge[1] = gridSpaces.containsKey(tempVec);
            
            //front
            tempVec.set(location.x, location.y, location.z + 1);
            gridSpace.unreachableEdge[2] = gridSpaces.containsKey(tempVec);
            
            //back
            tempVec.set(location.x, location.y, location.z - 1);
            gridSpace.unreachableEdge[3] = gridSpaces.containsKey(tempVec);
        });
        
        entities.values().forEach(entity -> {
            if(entity.hasComponent(ComponentAABB.class) && entity.hasComponent(ComponentPosition.class)) {
                Vector3f position = entity.getComponent(ComponentPosition.class).position;
                entity.getComponent(ComponentAABB.class).update(position, gridSpaces, entities.values());
            }
        });
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera, int depthTexHandle) {
        gridRenderer.draw(glPrograms.get("grid"), gridSpaces);
        
        entities.values().forEach(entity -> {
            /*
            if(entity.hasComponent(ComponentUnit.class) && entity.hasComponent(ComponentPosition.class)) {
                entity.getComponent(ComponentUnit.class).render(
                        entity.getComponent(ComponentPosition.class).position, 
                        glPrograms.get("test"));
            }
            */
            if(entity.hasComponent(ComponentAABB.class) && Main.showBoundingVolumes()) {
                entity.getComponent(ComponentAABB.class).render(glPrograms.get("volume"));
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