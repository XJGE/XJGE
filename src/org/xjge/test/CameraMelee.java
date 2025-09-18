package org.xjge.test;

import java.util.Map;
import org.joml.Vector3f;
import org.xjge.core.Camera;
import org.xjge.graphics.GLProgram;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class CameraMelee extends Camera {

    private float pitch = 52f;
    private float yaw = -90f;
    
    private Vector3f nextPosition = new Vector3f();
    private final Vector3f adjustedTarget = new Vector3f();
    
    CameraMelee() {
        super(false);
    }
    
    @Override
    protected void update(double targetDelta, double trueDelta) {
        pitch = Math.max(15f, Math.min(80f, pitch)); //clamp pitch to avoid flipping
        
        float distance = 5f;
        
        adjustedTarget.set(nextPosition).add(0, 0.5f, 0);
        
        //compute offset from yaw/pitch spherical coords
        float offsetX = (float) (distance * Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw)));
        float offsetY = (float) (distance * Math.sin(Math.toRadians(pitch)));
        float offsetZ = (float) (distance * Math.cos(Math.toRadians(pitch)) * Math.sin(Math.toRadians(yaw)));
        
        position.set(adjustedTarget.x - offsetX, adjustedTarget.y + offsetY, adjustedTarget.z - offsetZ);
        direction.set(adjustedTarget).sub(position).normalize();
    }

    @Override
    protected void render(Map<String, GLProgram> glPrograms) {
        glPrograms.values().forEach(glProgram -> {
            if(glProgram.containsUniform("uView")) {
                glProgram.use();
                viewMatrix.setLookAt(position, adjustedTarget, up);
                glProgram.setUniform("uView", false, viewMatrix);
            }
        });
    }
    
    public void lookAt(Vector3f position, Vector3f target) {
        nextPosition.set(position);
        adjustedTarget.set(target);
        update(0.016, 0.016);
    }

}