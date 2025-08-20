package org.xjge.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector3i;
import org.xjge.core.Camera;
import org.xjge.core.Entity;
import org.xjge.core.Logger;
import org.xjge.core.Scene;
import org.xjge.graphics.GLProgram;

/**
 * Created: Jan 31, 2025
 * 
 * @author J Hoffman
 * @since  
 */
public class TestScene extends Scene {
    
    private int openSpaceCount;
    
    private final GridRenderer gameBoard = new GridRenderer();
    
    private final Map<Vector3i, GridSpace> spaces = new HashMap<>();
    
    public TestScene(String filename) {
        super("test");
        
        Entity entity = new Entity();
        entity.addComponent(new TestMesh(1, 0f, 0f, -5f));
        
        addEntity(entity);
        
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
                        //Unit player = new Unit(new Vector3f(x, 0.01f, z), "player", 0, KEY_MOUSE_COMBO);
                        //space.occupyingUnit = player;
                        //addEntity(player);
                        //turns.add(player);
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
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera, int depthTexHandle) {
        gameBoard.draw(glPrograms.get("grid"), spaces);
        
        entities.values().forEach(entity -> {
            if(entity.hasComponent(TestMesh.class)) entity.getComponent(TestMesh.class).render(glPrograms.get("test"));
        });
    }

    @Override
    public void renderShadows(GLProgram depthProgram) {
    }

    @Override
    public void exit() {
    }

}