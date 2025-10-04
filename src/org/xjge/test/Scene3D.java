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
import static org.xjge.core.Input.KEY_MOUSE_COMBO;
import org.xjge.core.Logger;
import org.xjge.core.Scene;
import org.xjge.core.UI;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import static org.xjge.test.GridSpace.TYPE_SPAWN_ENEMY;
import static org.xjge.test.GridSpace.TYPE_SPAWN_PLAYER;

/**
 * Created: Jan 31, 2025
 * 
 * @author J Hoffman
 * @since  
 */
public class Scene3D extends Scene {
    
    private GameMode gameMode;
    
    private final ViewTransitioner cameraManager   = new ViewTransitioner();
    private final CameraFollow cameraFollow     = new CameraFollow();
    private final CameraOverhead cameraOverhead = new CameraOverhead();
    private final CameraMelee cameraMelee       = new CameraMelee();
    
    private final Vector3i tempVec = new Vector3i();
    private final GridRenderer gridRenderer = new GridRenderer();
    private final Map<Vector3i, GridSpace> gridSpaces = new HashMap<>();
    
    public Scene3D() {
        this("map_test.txt");
        //Added this for resetting the scene during runtime
    }
    
    public Scene3D(String filename) {
        super("test");
        
        UI.clearWidgets(GLFW_JOYSTICK_1);
        
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
                    if(type == TYPE_SPAWN_PLAYER) {
                        Entity player = new Entity();
                        player.addComponent(new ComponentUnit(true, KEY_MOUSE_COMBO, player.uuid));
                        player.addComponent(new ComponentPosition(x, 0.01f, z));
                        player.addComponent(new ComponentAABB(0.5f, 1.1f, 0.5f, Color.BLUE));
                        addEntity(player);
                        gridSpace.occupyingUnit = player.getComponent(ComponentUnit.class);
                    } else if(type == TYPE_SPAWN_ENEMY) {
                        Entity enemy = new Entity();
                        enemy.addComponent(new ComponentUnit(false, KEY_MOUSE_COMBO, enemy.uuid));
                        enemy.addComponent(new ComponentPosition(x, 0.01f, z));
                        enemy.addComponent(new ComponentAABB(0.5f, 1.1f, 0.5f, Color.RED));
                        addEntity(enemy);
                        gridSpace.occupyingUnit = enemy.getComponent(ComponentUnit.class);
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
            
            if(entity.hasComponent(ComponentMudBall.class)) {
                entity.getComponent(ComponentMudBall.class).update(targetDelta, gridSpaces, entity);
            }
        });
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera, int depthTexHandle) {
        cameraManager.render(glPrograms);
        
        gridRenderer.draw(glPrograms.get("grid"), gridSpaces);
        
        entities.values().forEach(entity -> {
            if(entity.hasComponent(ComponentMudBall.class) && entity.hasComponent(ComponentPosition.class)) {
                entity.getComponent(ComponentMudBall.class)
                      .render(glPrograms, 
                              entity.getComponent(ComponentPosition.class).position,
                              cameraOverhead.position);
            }
            
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
    
    final void setCameraMelee(float duration) {
        cameraMelee.position.set(cameraOverhead.position);
        cameraMelee.direction.set(cameraOverhead.direction);
        cameraMelee.resetSide();
        cameraMelee.update(0.016f, 0.016f);
        cameraManager.setActiveCamera(cameraMelee, duration);
    }
    
    void focusMeleeCamera(Vector3f attackerPos, Vector3f defenderPos) {
        cameraMelee.focus(attackerPos, defenderPos);
    }
    
    final void setCameraOverhead(float duration) {
        cameraManager.setActiveCamera(cameraOverhead, duration);
    }
    
    void focusOverheadCamera(GridSpace space, float speed) {
        Vector3f target = new Vector3f(space.xLocation, space.yLocation + 10, space.zLocation + 8);
        cameraOverhead.moveTo(target, speed);
    }
    
    void focusOverheadCamera(Entity entity, float speed) {
        if(entity.hasComponent(ComponentPosition.class)) {
            Vector3f target = new Vector3f(entity.getComponent(ComponentPosition.class).position.x,
                                           entity.getComponent(ComponentPosition.class).position.y + 10,
                                           entity.getComponent(ComponentPosition.class).position.z + 8);
            
            cameraOverhead.moveTo(target, speed);
        } else {
            Logger.logWarning("Failed to focus the overhead camera on entity " + entity.uuid +
                              " because it doesn not contain a position component", null);
        }
    }
    
    void snapOverheadCamera(GridSpace space) {
        Vector3f target = new Vector3f(space.xLocation, space.yLocation + 10, space.zLocation + 8);
        cameraOverhead.setPosition(target);
    }
    
    boolean CameraTransitionComplete() {
        return cameraManager.transitionComplete();
    }
    
    int getMeleeCameraSide() {
        return cameraMelee.getChosenSide();
    }

}