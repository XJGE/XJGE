package org.xjge.test;

import java.util.Map;
import org.joml.Vector3f;
import org.xjge.core.Camera;
import org.xjge.graphics.Shader;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class OrthoCam extends Camera {

    private float orthoNear = Short.MIN_VALUE;
    private float orthoFar  = Short.MAX_VALUE;
    
    private final Vector3f tempFront = new Vector3f();
    
    @Override
    protected void update(double targetDelta, double trueDelta) {
    }

    @Override
    protected void render(Map<String, Shader> glPrograms, int width, int height) {
        viewMatrix.setLookAt(position, tempFront.set(direction).add(position), up);
        projMatrix.setOrtho(0, width, 0, height, orthoNear, orthoFar);
    }

}