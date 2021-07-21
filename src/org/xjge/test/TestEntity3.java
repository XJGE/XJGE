package org.xjge.test;

import org.xjge.core.Camera;
import org.xjge.core.Entity;
import org.xjge.core.LightSource;
import org.xjge.core.XJGE;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Model;
import java.util.Map;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: Jun 15, 2021
 */

public class TestEntity3 extends Entity {

    private Model model;
    
    TestEntity3(float x, float y, float z) {
        super(new Vector3f(x, y, z));
        
        this.model = new Model("mod_buster.fbx");
        model.setAnimation("TPose", 0);
        model.setAnimationSpeed(0.25f);
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        model.delocalizeNormal();
        model.translate(position);
        model.scale(0.004f);
        
        model.updateAnimation();
    }

    @Override
    public void render(GLProgram glProgram, Camera camera, LightSource[] lights, int numLights) {
        model.render(XJGE.getDefaultGLProgram(), lights, numLights);
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, Camera camera, LightSource[] lights, int numLights) {
    }

    @Override
    protected void destroy() {
    }
    
}