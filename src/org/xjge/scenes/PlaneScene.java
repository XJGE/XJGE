package org.xjge.scenes;

import java.util.Map;
import org.joml.Vector3f;
import org.xjge.core.Camera;
import org.xjge.core.Game;
import org.xjge.core.Scene;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Light;

/**
 * Dec 3, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class PlaneScene extends Scene {
    
    public PlaneScene() {
        super("plane");
        
        entities.put("plane", new EntityPlane(0, 0, 0, 50, 50, Color.SILVER));
        entities.put("cube", new EntityCube(0, 6, 0, 2, 2, 2, true));
        
        Game.setClearColor(Color.BLACK);
        
        addLightAtIndex(0, Light.midnight());
        addLight(new Light(3f, 1f, 1.25f, new Vector3f(2, 1, 0), Color.RED));
        
        System.out.println(getNumLights());
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        entities.values().forEach(entity -> entity.update(targetDelta, trueDelta));
        
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera) {
        glPrograms.get("default").use();
        entities.values().forEach(entity -> entity.render(glPrograms.get("default"), camera, getLightSources(), getNumLights()));
    }

    @Override
    public void exit() {
    }

}
