package org.xjge.scenes;

import java.util.Map;
import org.xjge.core.Camera;
import org.xjge.core.Scene;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;

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
        
        entities.put("plane", new EntityPlane(0, 0, 0, 50, 50, Color.GRAY));
        entities.put("cube", new EntityCube(0, 6, 0, 2, 2, 2, true));
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        entities.values().forEach(entity -> entity.update(targetDelta, trueDelta));
        
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera) {
        entities.values().forEach(entity -> entity.render(glPrograms.get("default"), camera, getLightSources(), getNumLights()));
    }

    @Override
    public void exit() {
    }

}
