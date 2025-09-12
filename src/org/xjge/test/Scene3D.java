package org.xjge.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.xjge.core.Camera;
import org.xjge.core.Entity;
import static org.xjge.core.Input.KEY_MOUSE_COMBO;
import org.xjge.core.Logger;
import org.xjge.core.Scene;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;

/**
 * Created: Jan 31, 2025
 * 
 * @author J Hoffman
 * @since  
 */
public class Scene3D extends Scene {
    
    private GameMode gameMode;
    
    private final CameraManager cameraManager   = new CameraManager();
    private final CameraFollow cameraFollow     = new CameraFollow();
    private final CameraOverhead cameraOverhead = new CameraOverhead();
    
    private final Vector3i tempVec = new Vector3i();
    private final GridRenderer gridRenderer = new GridRenderer();
    private final Map<Vector3i, GridSpace> gridSpaces = new HashMap<>();
    
    public Scene3D(String filename) {
        super("test");
        
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
                        
                        player.addComponent(new ComponentUnit(KEY_MOUSE_COMBO));
                        player.addComponent(new ComponentPosition(x, 0.01f, z));
                        player.addComponent(new ComponentAABB(0.5f, 1.1f, 0.5f, Color.BLUE));
                        
                        addEntity(player);
                        
                        gridSpace.occupyingUnit = player.getComponent(ComponentUnit.class);
                        
                    } else if(type == 3) {
                        /*
                        Entity enemy = new Entity();
                        
                        enemy.addComponent(new ComponentUnit(AI_GAMEPAD_1));
                        enemy.addComponent(new ComponentPosition(x, 0.01f, z));
                        enemy.addComponent(new ComponentAABB(0.5f, 1.1f, 0.5f, Color.RED));
                        
                        addEntity(enemy);
                        */
                    }
                }
                
                z++;
                line = reader.readLine();
            }
            
            //TODO: load initial game mode from map file data?
            setGameMode(new GameModeBattle());
            
        } catch(IOException exception) {
            Logger.logError("Failed to load map\"" + filename + "\"", exception);
        }
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        if(gameMode != null) {
            gameMode.execute(this, Collections.unmodifiableMap(entities), Collections.unmodifiableMap(gridSpaces));
        }
        
        cameraManager.update(targetDelta, trueDelta);
        
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
        cameraManager.render(glPrograms);
        
        gridRenderer.draw(glPrograms.get("grid"), gridSpaces);
        
        entities.values().forEach(entity -> {
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
    
    final void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }
    
    final void setCameraFollow(ComponentUnit unit, float duration) {
        Entity unitEntity = entities.values().stream()
                                    .filter(entity -> entity.getComponent(ComponentUnit.class) == unit)
                                    .findFirst().orElse(null);

        if(unitEntity != null) cameraFollow.follow(unitEntity);
        
        cameraManager.setActiveCamera(cameraFollow, duration);
    }
    
    final void setCameraOverhead(float duration) {
        cameraManager.setActiveCamera(cameraOverhead, duration);
    }

}