package org.xjge.test;

import java.util.Map;
import org.joml.Vector3f;
import org.xjge.core.Camera;
import org.xjge.core.XJGE;
import org.xjge.graphics.GLProgram;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class CameraOverhead extends Camera {

    private float pitch = 52f;
    private float yaw = -90f;
    private float moveSpeed;
    private float moveLerp;
    
    private final Vector3f temp = new Vector3f();
    private Vector3f nextPosition = new Vector3f();
    private final Vector3f adjustedTarget = new Vector3f();
    
    CameraOverhead() {
        super(false);
        fov = 25;
        direction.set(0, 0, -1);
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        if(!position.equals(nextPosition)) {
            moveLerp = XJGE.clampValue(0, 1, moveLerp += moveSpeed);
            
            if(moveLerp == 1) position.set(nextPosition);
            else              position.lerp(nextPosition, moveLerp);
        }
        
        direction.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        direction.y = (float) Math.sin(Math.toRadians(pitch)) * -1;
        direction.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        glPrograms.values().forEach(glProgram -> {
            if(glProgram.containsUniform("uView")) {
                glProgram.use();
                adjustedTarget.set(nextPosition).add(0, 0.5f, 0);
                viewMatrix.setLookAt(position, adjustedTarget, up);
                glProgram.setUniform("uView", false, viewMatrix);
            }
        });
    }
    
    public void moveTo(Vector3f nextPosition, float speed) {
        this.nextPosition = nextPosition;
        moveSpeed         = speed;
        moveLerp          = 0;
    }

}