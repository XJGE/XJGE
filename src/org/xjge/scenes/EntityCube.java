package org.xjge.scenes;

import java.util.Map;
import org.joml.Vector3f;
import org.xjge.core.Camera;
import org.xjge.core.Entity;
import org.xjge.core.LightSource;
import org.xjge.graphics.GLProgram;

/**
 * Nov 9, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class EntityCube extends Entity {

    EntityCube(float x, float y, float z) {
        super(new Vector3f(x, y, z));
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
    }

    @Override
    public void render(GLProgram glProgram, Camera camera, LightSource[] lights, int numLights) {
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, Camera camera, LightSource[] lights, int numLights) {
    }

    @Override
    public void renderShadow(GLProgram depthProgram) {
    }

    @Override
    protected void destroy() {
    }

}