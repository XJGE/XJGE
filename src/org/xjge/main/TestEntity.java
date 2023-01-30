package org.xjge.main;

//Created: Jan 29, 2023

import java.util.Map;
import org.joml.Vector3f;
import org.xjge.core.Camera;
import org.xjge.core.Entity;
import org.xjge.core.Light;
import org.xjge.graphics.GLProgram;


/**
 * @author J Hoffman
 * @since  
 */
public class TestEntity extends Entity {

    public TestEntity(float x, float y, float z) {
        super(new Vector3f(x, y, z));
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
    }

    @Override
    public void render(GLProgram glProgram, Camera camera, Light[] lights, int depthTexHandle) {
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, Camera camera, Light[] lights, int depthTexHandle) {
    }

    @Override
    public void renderShadow(GLProgram depthProgram) {
    }

    @Override
    protected void destroy() {
    }

}
